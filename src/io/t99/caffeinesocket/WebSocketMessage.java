package io.t99.caffeinesocket;

/*
 *	Copyright 2017, Trevor Sears <trevorsears.main@gmail.com>
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

import io.t99.caffeinesocket.util.Binary;
import io.t99.caffeinesocket.util.NumberBaseConverter;

/**
 * Processor for incoming {@link WebSocket} messages, and builder for outgoing <code>WebSocket</code> messages.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public class WebSocketMessage {

	/* TODO
	 *  - Make this class.
	 *  - Provide a constructor for creating a message.
	 *  - Provide a method for getting the bytes of a message to feed directly into the method parameters of the WebSocket 'write()' method.
	 *  - Figure out how to more effectively loop through the construction of a received message.
	 *  - Account for non-FIN messages, pings, and other control frames.
	 */
	
	/*
	 *
	 *	Frame format:
	​​ *
	 *	 0                   1                   2                   3
	 *	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *	+-+-+-+-+-------+-+-------------+-------------------------------+
	 *	|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
	 *	|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
	 *	|N|V|V|V|       |S|             |   (if payload len==126/127)   |
	 *	| |1|2|3|       |K|             |                               |
	 *	+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
	 *	|     Extended payload length continued, if payload len == 127  |
	 *	+ - - - - - - - - - - - - - - - +-------------------------------+
	 *	|                               |Masking-key, if MASK set to 1  |
	 *	+-------------------------------+-------------------------------+
	 *	| Masking-key (continued)       |          Payload Data         |
	 *	+-------------------------------- - - - - - - - - - - - - - - - +
	 *	:                     Payload Data continued ...                :
	 *	+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
	 *	|                     Payload Data continued ...                |
	 *	+---------------------------------------------------------------+
	 *
	 */
	
	// Fields
	
	/**
	 * Indicator of the current completeness of a given WebSocketMessage instance.
	 */
	WebSocketMessageState state = WebSocketMessageState.INCOMPLETE;
	
	/**
	 * The raw binary of a given instance of WebSocketMessage. Initially empty, but appended to as the provider {@link WebSocketListener} calls {@link #process(Binary)}.
	 */
	private Binary rawMessage = new Binary();
	
	/**
	 * Assigned to numeric variables before they have been set.
	 * <p>
	 * This allows for an easy (and uniform) way to check if a certain numeric field in a <code>WebSocketMessage</code> has been set
	 * yet, as there is no way for a numeric field to be set to a negative value otherwise.
	 *
	 * @see #headerSize
	 * @see #opcode
	 * @see #payloadLengthBytes
	 * @see #payloadLengthPlaceholder
	 * @see #payloadLengthSize
	 */
	public static final int NOT_SET = -1;
	
	/*
	 * These constants denote the payload size scheme that a message *can* used.
	 *
	 *	- PLS_SMALL		= 07 bits used to encode the payload size.
	 *	- PLS_MEDIUM	= 23 bits used to encode the payload size.
	 *	- PLS_LARGE		= 71 bits used to encode the payload size.
	 *
	 * 9 bits are then added for the FIN bit, the RSV1, RSV2, and RSV3 bits, the Opcode bits, and the mask bit which all
	 * precede the payload size in the WebSocket header.
	 */
	/**
	 * Constant used to denote the smallest number of bits that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 16b/2B.
	 */
	public static final int PLS_SMALL	= 16;
	
	/**
	 * Constant used to denote the middle-sized number of bits that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 32b/4B.
	 */
	public static final int PLS_MEDIUM	= 32;
	
	/**
	 * Constant used to denote the largest number of bits that can precede either the {@link #maskingKey}, or the
	 * {@link #payload}, depending on whether or not the {@link #masked} boolean is true. Set to 80b/10B.
	 */
	public static final int PLS_LARGE	= 80;
	
	// The finality marker for the message. If this is true, this is the last message in a series. Singlet messages
	// are marked with `fin = true` as well.
	private Boolean fin;
	
	// I'm not 100% sure what these are for yet... something to do with extensions maybe.
	private Boolean rsv1;
	private Boolean rsv2;
	private Boolean rsv3;
	
	/*
	 * The OpCode for the message. Available codes listed below.
	 *
	 *	- 0x0 (dec 00): Continuation Frame
	 *	- 0x1 (dec 01): Text Frame
	 *	- 0x2 (dec 02): Binary Frame
	 *	- 0x3 (dec 03): [Reserved for further non-control frames...]
	 *	- 0x4 (dec 04): [Reserved for further non-control frames...]
	 *	- 0x5 (dec 05): [Reserved for further non-control frames...]
	 *	- 0x6 (dec 06): [Reserved for further non-control frames...]
	 *	- 0x7 (dec 07): [Reserved for further non-control frames...]
	 *	- 0x8 (dec 08): Close Connection
	 *	- 0x9 (dec 09): Ping!
	 *	- 0xA (dec 10): Pong!
	 *	- 0xB (dec 11): [Reserved for further control frames...]
	 *	- 0xC (dec 12): [Reserved for further control frames...]
	 *	- 0xD (dec 13): [Reserved for further control frames...]
	 *	- 0xE (dec 14): [Reserved for further control frames...]
	 *	- 0xF (dec 15): [Reserved for further control frames...]
	 */
	private int opcode = NOT_SET;
	
	// The mask marker for the message. If this is true, the message is masked, as is usually (and as should be) the
	// case with client-to-server communication.
	private Boolean masked;
	
	// The decimal value found from the first seven bits that can be used to encode the payload size.
	private int payloadLengthPlaceholder = NOT_SET;
	
	// The length of the payload.
	private long payloadLengthBytes = NOT_SET;
	
	// The masking key to decode the payload.
	private Binary maskingKey;
	
	// The size of the WebSocket 'headers'.
	private int headerSize = NOT_SET;
	
	// The actual raw data of the payload, with the metadata stripped.
	// payload = rawMessage - (fin + RSV# + opcode + masked + payloadLengthBytes + maskingKey)
	private Binary payload;
	
	public WebSocketMessageState process(Binary bin) {

		rawMessage.append(bin);

		if (fin == null && rawMessage.size() >= 1) {

			fin = rawMessage.getBit(0);

		}

		if (rsv1 == null && rawMessage.size() >= 2) {

			rsv1 = rawMessage.getBit(1);

		}

		if (rsv2 == null && rawMessage.size() >= 3) {

			rsv2 = rawMessage.getBit(2);

		}

		if (rsv3 == null && rawMessage.size() >= 4) {

			rsv3 = rawMessage.getBit(3);

		}

		if (opcode == NOT_SET && rawMessage.size() >= 8) {

			opcode = NumberBaseConverter.binToDec(new Binary(rawMessage, 4, 8));

		}

		if (masked == null && rawMessage.size() >= 9) {

			masked = rawMessage.getBit(8);
			
			if (!masked) {

				state = WebSocketMessageState.ERROR;

			}

		}

		if (payloadLengthPlaceholder == NOT_SET && rawMessage.size() >= PLS_SMALL) {

			payloadLengthPlaceholder = NumberBaseConverter.binToDec(new Binary(rawMessage, 9, PLS_SMALL));

		}

		if (payloadLengthBytes == NOT_SET && payloadLengthPlaceholder <= 125 && payloadLengthPlaceholder != NOT_SET) {

			payloadLengthBytes = payloadLengthPlaceholder;
			headerSize = PLS_SMALL; // Without the masking key.

		}

		if (payloadLengthBytes == NOT_SET && payloadLengthPlaceholder == 126 && rawMessage.size() >= PLS_MEDIUM) {

			payloadLengthBytes = NumberBaseConverter.binToDec(new Binary(rawMessage, 9, PLS_MEDIUM));
			headerSize = PLS_MEDIUM; // Without the masking key.

		}

		if (payloadLengthBytes == NOT_SET && payloadLengthPlaceholder == 127 && rawMessage.size() >= PLS_LARGE) {

			payloadLengthBytes = NumberBaseConverter.binToDec(new Binary(rawMessage, 9, PLS_LARGE));
			headerSize = PLS_LARGE; // Without the masking key.

		}

		if (masked != null && masked && maskingKey == null && rawMessage.size() >= headerSize + 32) {

			maskingKey = new Binary(rawMessage, headerSize, headerSize + 32);
			headerSize += 32; // Now it includes the masking key.

		}

		if ((rawMessage.size() - headerSize) == payloadLengthBytes * 8) {

			payload = new Binary(rawMessage, headerSize);

			Binary encodedPayload = payload;	// This will hold the masked version of the payload.
			payload = new Binary();				// The payload variable can now hold the unmasked version.

			// Here's where we unmask the payload.
			Binary[] maskingKeyOctets = maskingKey.toBinaryOctetArray();
			Binary[] payloadOctets = encodedPayload.toBinaryOctetArray();

			for (int octet = 0; octet < payloadOctets.length; octet++) {

				payload.append(Binary.logicalXor(payloadOctets[octet], maskingKeyOctets[octet % 4]));

			}

			state = WebSocketMessageState.COMPLETE;

			for (Binary binary: payload.toBinaryOctetArray()) {

				System.out.print((char) NumberBaseConverter.binToDec(binary));

			}

			System.out.print("\r\n");

		}

		return state;

	}
	
	public void formMessage(String string) {
		
		fin = false;
		rsv1 = false;
		rsv2 = false;
		rsv3 = false;
		
		
	}

}