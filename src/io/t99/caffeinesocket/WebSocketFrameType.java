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

import io.t99.caffeinesocket.exceptions.InvalidOpcodeException;

/**
 * Enumeration of valid WebSocket frame types.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public enum WebSocketFrameType {
	
	/**
	 * Denotes a continuation frame with an <code>opcode</code> of 0x0 (decimal 0).
	 */
	CONTINUATION		(0x0), // Decimal 00
	
	/**
	 * Denotes a text frame with an <code>opcode</code> of 0x1 (decimal 1).
	 */
	TEXT				(0x1), // Decimal 01
	
	/**
	 * Denotes a binary frame with an <code>opcode</code> of 0x2 (decimal 2).
	 */
	BINARY				(0x2), // Decimal 02
	
	/**
	 * A control frame that indicates the connected party (server or client) has initiated a connection close, <code>opcode</code> of 0x8 (decimal 8).
	 */
	CONNECTION_CLOSE	(0x8), // Decimal 08
	
	/**
	 * Denotes a ping! frame with an <code>opcode</code> of 0x9 (decimal 9).
	 */
	PING				(0x9), // Decimal 09
	
	/**
	 * Denotes a pong! frame with an <code>opcode</code> of 0xA (decimal 10).
	 */
	PONG				(0xA); // Decimal 10
	
	/**
	 * The decimal opcode of a given frame type.
	 */
	private final int opcode;
	
	/**
	 * Sole constructor for WebSocketFrameType, associates a frame type with a decimal opcode.
	 *
	 * @param opcode The decimal opcode of the given frame type.
	 */
	WebSocketFrameType(int opcode) {
	
		this.opcode = opcode;
	
	}
	
	/**
	 * Returns the opcode of the current instance of WebSocketFrameType.
	 *
	 * @return Decimal opcode.
	 */
	public int getOpcode() {
		
		return opcode;
		
	}
	
	/**
	 * Returns a matching WebSocketFrameType, when provided a valid opcode.
	 *
	 * @param opcode The opcode for the desired frame type.
	 * @return A WebSocketFrameType that matches the given opcode.
	 * @throws InvalidOpcodeException If the provided opcode does not match a valid frame type.
	 */
	public static WebSocketFrameType getFrameTypeForOpcode(int opcode) throws InvalidOpcodeException {
		
		switch (opcode) {
			
			case 0x0:
				return CONTINUATION;
				
			case 0x1:
				return TEXT;
				
			case 0x2:
				return BINARY;
				
			case 0x8:
				return CONNECTION_CLOSE;
				
			case 0x9:
				return PING;
				
			case 0xA:
				return PONG;
				
			default:
				throw new InvalidOpcodeException("An opcode of " + opcode + " was received, and matched no valid/recognized frame type.");
			
		}
		
	}
	
}
