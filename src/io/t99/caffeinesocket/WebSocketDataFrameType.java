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

/**
 * Enumeration of valid WebSocket data frame types.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 * @see WebSocketFrameType
 * @see WebSocketControlFrameType
 */
public enum WebSocketDataFrameType implements WebSocketFrameType {
	
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
	BINARY				(0x2); // Decimal 02
	
	/**
	 * The decimal opcode of a given frame type.
	 */
	private final int opcode;
	
	/**
	 * Sole constructor for WebSocketDataFrameType, associates a frame type with a decimal opcode.
	 *
	 * @param opcode The decimal opcode of the given frame type.
	 */
	WebSocketDataFrameType(int opcode) {
	
		this.opcode = opcode;
	
	}
	
	/**
	 * Returns the opcode of the current instance of WebSocketDataFrameType.
	 *
	 * @return Decimal opcode.
	 * @see WebSocketFrameType#getOpcode()
	 */
	@Override
	public int getOpcode() {
		
		return opcode;
		
	}
	
}
