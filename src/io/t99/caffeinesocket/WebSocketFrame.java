package io.t99.caffeinesocket;

/*
 *	Copyright 2018, Trevor Sears <trevorsears.main@gmail.com>
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */

import io.t99.caffeinesocket.exceptions.InvalidOpcodeException;
import io.t99.caffeinesocket.util.ByteList;
import io.t99.caffeinesocket.util.NumberBaseConverter;
import io.t99.caffeinesocket.util.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * Processor for incoming {@link WebSocket} frames, and builder for outgoing <code>WebSocket</code> frames.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public class WebSocketFrame {

	/* TODO
	 *  - Make this class.
	 *  - Provide a constructor for creating a frame.
	 *  - Provide a method for getting the bytes of a frame to feed directly into the method parameters of the WebSocket 'write()' method.
	 *  - Figure out how to more effectively loop through the construction of a received frame.
	 *  - Account for non-FIN frames, pings, and other control frames.
	 */

	/*
	 *
	 *	                         Frame format:
	​​ *
	 *	 0                   1                   2                   3
	 *	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *	+-+-+-+-+-------+-+-------------+-------------------------------+
	 *	|F|R|R|R|       |M|             |    Extended payload length    |
	 *	|I|S|S|S|opcode |A| Payload len |            (16/64)            |
	 *	|N|V|V|V|  (4)  |S|     (7)     |   (if payload len==126/127)   |
	 *	| |1|2|3|       |K|             |                               |
	 *	+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
	 *	|   Extended payload length continued, if payload len == 127    |
	 *	+ - - - - - - - - - - - - - - - +-------------------------------+
	 *	|                               | Masking-key, if MASK set to 1 |
	 *	+-------------------------------+-------------------------------+
	 *	|    Masking-key (continued)    |         Payload Data          |
	 *	+-------------------------------- - - - - - - - - - - - - - - - +
	 *	:                   Payload Data continued ...                  :
	 *	+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
	 *	|                   Payload Data continued ...                  |
	 *	+---------------------------------------------------------------+
	 *
	 *           Adapted from RFC 6455 - The WebSocket Protocol
	 *           https://tools.ietf.org/html/rfc6455#section-5.2
	 *
	 */

	// Fields
	
	/**
	 * The WebSocket that received this frame.
	 */
	WebSocket parent;
	
	/**
	 * Indicator of the current completeness of a given WebSocketFrame instance (the entirety of the frame, not just the header).
	 */
	boolean isComplete = false;

	/**
	 * Indicator of the current completeness of a given WebSocketFrame's headers.
	 */
	boolean headerComplete = false;

	/**
	 * A ByteList containing the raw bytes received of a given frame.
	 *
	 * Initially empty, but appended to by the parent {@link WebSocketListener} by way of calling {@link #process(byte)}.
	 *
	 * @see io.t99.caffeinesocket.util.ByteList
	 */
	private ByteList rawMessage;

	/**
	 * Assigned to numeric variables before they have been set.
	 * <p>
	 * This allows for an easy (and uniform) way to check if a certain numeric field in a <code>WebSocketFrame</code> has been set
	 * yet, as there is no way for a numeric field to be set to a negative value otherwise.
	 *
	 * @see #headerSize
	 * @see #payloadLength
	 * @see #payloadLengthIndicator
	 */
	private static final int NOT_SET = -1;
	
	/*
	 * These constants denote the payload size scheme that a frame *can* use.
	 *
	 *	- PLS_SMALL		= 07 bits used to encode the payload size.
	 *	- PLS_MEDIUM	= 23 bits used to encode the payload size.
	 *	- PLS_LARGE		= 71 bits used to encode the payload size.
	 *
	 * 9 bits are then added for the FIN bit (1), the RSV1 (1), RSV2 (1), and RSV3 bits (1), the Opcode bits (4), and the mask bit (1) which all
	 * precede the payload size in the WebSocket header.
	 */

	/**
	 * Constant used to denote the smallest number of bytes that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 16b/2B.
	 */
	private static final int PLS_SMALL	= 2;

	/**
	 * Constant used to denote the middle-sized number of bytes that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 32b/4B.
	 */
	private static final int PLS_MEDIUM	= 4;

	/**
	 * Constant used to denote the largest number of bytes that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 80b/10B.
	 */
	private static final int PLS_LARGE	= 10;

	/**
	 * The finality marker for the frame. If this is true, this is the last frame in a series. Singlet frames are marked
	 * with `fin = true` as well.
	 */
	private Boolean fin;

	/**
	 * I'm not 100% sure what these are for yet... something to do with extensions maybe.
	 */
	private Boolean rsv1;

	/**
	 * I'm not 100% sure what these are for yet... something to do with extensions maybe.
	 */
	private Boolean rsv2;

	/**
	 * I'm not 100% sure what these are for yet... something to do with extensions maybe.
	 */
	private Boolean rsv3;
	
	/**
	 * The frame type, indicated by it's received opcode.
	 *
	 * @see io.t99.caffeinesocket.WebSocketFrameType
	 */
	private WebSocketFrameType frameType;

	/**
	 * The mask marker for the frame. If this is true, the frame is masked, as is usually (and as should be) the case
	 * with client-to-server communication.
	 */
	private Boolean masked;

	/**
	 * Whether or not this message SHOULD be masked. If this does not match the information provided by the frame, error
	 * the frame and disconnect.
	 */
	private boolean maskRequirement;

	/**
	 * The decimal value found from the first seven bits that can be used to encode the payload size. Indicates what
	 * payload size-group is being used.
	 */
	private int payloadLengthIndicator = NOT_SET;

	/**
	 * The length of the payload in bytes.
	 */
	private long payloadLength = NOT_SET;

	/**
	 * The masking key to decode the payload.
	 */
	private ByteList maskingKey;

	/**
	 * The size of the WebSocket 'headers'.
	 */
	private int headerSize = NOT_SET;

	/**
	 * The actual raw data of the payload, with the metadata stripped.
	 *
	 * payload = rawMessage - (fin + RSV# + opcode + masked + payloadLength + maskingKey)
	 */
	private ByteList payload;
	
	/**
	 *
	 */
	private StringBuilder textPayload = new StringBuilder();

	public WebSocketFrame(WebSocket parent, boolean maskRequirement) {

		this.parent = parent;
		this.maskRequirement = maskRequirement;
		rawMessage = new ByteList(16, 10);

	}
	
	public WebSocketFrame(WebSocket parent, boolean maskRequirement, WebSocketControlFrameType controlFrameType, String string) { // Use the String
		
		this.parent = parent;
		this.maskRequirement = maskRequirement;
		frameType = controlFrameType;
		
		rawMessage = new ByteList(16, 10);

	}

	public WebSocketFrame(WebSocket parent, boolean maskRequirement, WebSocketControlFrameType controlFrameType, ByteList bytelist) { // Use the ByteList

		this.parent = parent;
		this.maskRequirement = maskRequirement;
		frameType = controlFrameType;
		
		rawMessage = new ByteList(16, 10);

	}
	
	/**
	 * Constructor for composing new control frames bound for other WebSockets.
	 *
	 * @param maskRequirement
	 * @param controlFrame
	 */
	public WebSocketFrame(WebSocket parent, boolean maskRequirement, WebSocketControlFrameType controlFrame) {
		
		this(parent, maskRequirement, controlFrame, "");
		
		rawMessage = new ByteList(16, 10);
		
	}
	
	public WebSocketFrame(WebSocket parent, boolean maskRequirement, String string) throws UnsupportedEncodingException {

		if (!StringUtils.isPureASCII(string)) throw new UnsupportedEncodingException("String passed to WebSocketFrame(boolean, String) was not pure ASCII.");

		fin = false;
		rsv1 = false;
		rsv2 = false;
		rsv3 = false;
		frameType = WebSocketDataFrameType.TEXT;
		this.maskRequirement = maskRequirement;
		
		rawMessage = new ByteList(16, 10);

		if (string.length() < PLS_SMALL) {} // TODO - Figure out size cutoffs

	}

	public boolean process(byte b) throws IllegalStateException {
		
		rawMessage.add(b);

		if (!headerComplete) {

			if (rawMessage.size() >= 1) {
				
				if (fin == null) fin = rawMessage.getBit(0, 0);
				
				if (rsv1 == null) rsv1 = rawMessage.getBit(0,1);
				
				if (rsv2 == null) rsv2 = rawMessage.getBit(0,2);
				
				if (rsv3 == null) rsv3 = rawMessage.getBit(0,3);
				
				if (frameType == null) {
					
					try {
						
						frameType = WebSocketFrameType.getFrameTypeForOpcode(NumberBaseConverter.binaryToDecimal(rawMessage.getBits(4, 8)));
						
					} catch (InvalidOpcodeException e) {
						
						if (CaffeineSocket.getDebug()) System.err.println(e);
						
					}
					
				}
				
			}

			if (masked == null && rawMessage.size() >= 2) {

				masked = rawMessage.getBit(8);

				if (!(masked == maskRequirement)) {

					throw new IllegalStateException("A message that required a mask was received unmasked");

				}

			}
			
			if (payloadLengthIndicator == NOT_SET && rawMessage.size() >= PLS_SMALL) {
				
				payloadLengthIndicator = NumberBaseConverter.binaryToDecimal(rawMessage.getBits(9, (PLS_SMALL * 8)));

			}

			if (payloadLength == NOT_SET && payloadLengthIndicator <= 125 && payloadLengthIndicator != NOT_SET) {

				payloadLength = payloadLengthIndicator;
				headerSize = PLS_SMALL; // Without the masking key.

			}

			if (payloadLength == NOT_SET && payloadLengthIndicator == 126 && rawMessage.size() >= PLS_MEDIUM) {

				payloadLength = NumberBaseConverter.binaryToDecimal(rawMessage.getBits(16, PLS_MEDIUM * 8));
				headerSize = PLS_MEDIUM; // Without the masking key.

			}

			if (payloadLength == NOT_SET && payloadLengthIndicator == 127 && rawMessage.size() >= PLS_LARGE) {

				payloadLength = NumberBaseConverter.binaryToDecimal(rawMessage.getBits(16, PLS_LARGE * 8));
				headerSize = PLS_LARGE; // Without the masking key.

			}

			if (masked != null && masked && maskingKey == null && headerSize != NOT_SET && rawMessage.size() >= headerSize + 4) {

				maskingKey = new ByteList(rawMessage, headerSize, headerSize + 4, 1);
				headerSize += 4; // Now it includes the masking key.

			}

			if (areHeadersComplete()) headerComplete = true;

		}
		
		/*
		 * I know it seems like I could have used an else statement, but I couldn't have.
		 *
		 * Just trust me on this.
		 *
		 * It has to do with zero-length payloads.
		 */
		
		if (headerComplete) {

			if (payloadLength == 0) {
				
				payload = null;
				textPayload = null;
				
				isComplete = true;
				
			} else if ((rawMessage.size() - headerSize) == payloadLength) {
				
				System.out.println("reached");
				
				payload = new ByteList(rawMessage, headerSize, rawMessage.size(), 1);

				ByteList encodedPayload = payload;				// This will hold the masked version of the payload.
				payload = new ByteList((int) payloadLength);	// The payload variable can now hold the unmasked version.

				for (int octet = 0; octet < encodedPayload.size(); octet++) {

					payload.add((byte) ((int) encodedPayload.get(octet) ^ (int) maskingKey.get(octet % 4)));

				}
				
				for (byte character: payload) {
					
					textPayload.append((char) NumberBaseConverter.signedByteToDecimal(character));
					
				}
				
				isComplete = true;

			}
			
			if (frameType instanceof WebSocketControlFrameType) {

				switch ((WebSocketControlFrameType) frameType) {

					case CONNECTION_CLOSE:
						System.out.println("Received CLOSE frame, queuing closing of parent WebSocket...");
						parent.close();
						break;

					case PING:
						break;

					case PONG:
						break;

				}

			}

		}
		
		System.out.println(getDebugInfo());
		
		return isComplete;

	}

 	private boolean areHeadersComplete() {
		
		if (fin == null) return false;
		if (rsv1 == null) return false;
		if (rsv2 == null) return false;
		if (rsv3 == null) return false;
		if (frameType == null) return false;
		if (masked == null) return false;
		if (payloadLength == NOT_SET) return false;
		if (masked && maskingKey == null) return false;
		//if (masked && maskingKey.size() != 32) return false; // TODO - Why was this commented out?
		return true;

	}
	
	public String getDebugInfo() {
		
		StringBuilder debugInfo = new StringBuilder();
		
		debugInfo.append("IS COMPLETE:\t\t" + isComplete + "\n");
		
		if (fin != null) debugInfo.append("FIN:\t\t\t\t" + fin + "\n");
		else debugInfo.append("FIN:\t\t\t\tNULL\n");
		
		if (rsv1 != null) debugInfo.append("RSV1:\t\t\t\t" + rsv1 + "\n");
		else debugInfo.append("RSV1:\t\t\t\tNULL\n");
		
		if (rsv2 != null) debugInfo.append("RSV2:\t\t\t\t" + rsv2 + "\n");
		else debugInfo.append("RSV2:\t\t\t\tNULL\n");
		
		if (rsv3 != null) debugInfo.append("RSV3:\t\t\t\t" + rsv3 + "\n");
		else debugInfo.append("RSV3:\t\t\t\tNULL\n");
		
		if (masked != null) debugInfo.append("Message Masked:\t\t" + masked + "\n");
		else debugInfo.append("Message Masked:\t\tNULL\n");
		
		if (frameType != null) debugInfo.append("OpCode:\t\t\t\t" + frameType.toString() + "\n");
		else debugInfo.append("OpCode:\t\t\t\tNULL\n");
		
		if (maskingKey != null) debugInfo.append("Masking Key:\t\t" + maskingKey.getArray() + "\n");
		else debugInfo.append("Masking Key:\t\tNULL\n");
		
		if (payloadLength != NOT_SET) debugInfo.append("Payload Size:\t\t" + payloadLength + "\n");
		else debugInfo.append("Payload Size:\t\tNOT_SET\n");
		
		if (rawMessage != null) debugInfo.append("Raw Message:\t\t" + rawMessage.getArray() + "\n");
		else debugInfo.append("Raw Message:\t\tNULL\n");
		
		if (payload != null) debugInfo.append("Raw Payload:\t\t" + payload.getArray() + "\n");
		else debugInfo.append("Raw Payload:\t\tNULL\n");
		
		if (textPayload != null) debugInfo.append("Textual Payload:\t" + textPayload + "\n");
		else debugInfo.append("Textual Payload:\tNULL\n");
		
		return debugInfo.toString();
		
	}

}