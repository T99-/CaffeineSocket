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
 * Enumeration of valid WebSocket control frame types.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 * @see WebSocketFrameType
 * @see WebSocketDataFrameType
 */
public enum WebSocketControlFrameType implements WebSocketFrameType {
	
	/**
	 * A control frame that indicates the connected party (server or client) has initiated a connection close,
	 * <code>opcode</code> of 0x8 (decimal 8).
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
	 * Sole constructor for WebSocketControlFrameType, associates a frame type with a decimal opcode.
	 *
	 * @param opcode The decimal opcode of the given frame type.
	 */
	WebSocketControlFrameType(int opcode) {
		
		this.opcode = opcode;
		
	}
	
	/**
	 * Returns the opcode of the current instance of WebSocketControlFrameType.
	 *
	 * @return Decimal opcode.
	 * @see WebSocketFrameType#getOpcode()
	 */
	@Override
	public int getOpcode() {
		
		return opcode;
		
	}
	
	
}
