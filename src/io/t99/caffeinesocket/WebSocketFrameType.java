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

/**
 * Enumeration interface, serving as an umbrella class for both Control Frame types and Data Frame types.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public interface WebSocketFrameType {
	
	/*
	 * The opcode for the frame. Available codes listed below.
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
	
	/**
	 * Returns the WebSocketFrameType's opcode.
	 *
	 * @return The opcode of the given instance of a WebSocketFrameType.
	 */
	int getOpcode();
	
	/**
	 * Returns a matching WebSocketDataFrameType, when provided a valid opcode.
	 *
	 * @param opcode The opcode for the desired frame type.
	 * @return A WebSocketDataFrameType that matches the given opcode.
	 * @throws InvalidOpcodeException If the provided opcode does not match a valid frame type.
	 */
	static WebSocketFrameType getFrameTypeForOpcode(int opcode) throws InvalidOpcodeException {
		
		switch (opcode) {
			
			case 0x0:
				return WebSocketDataFrameType.CONTINUATION;
			
			case 0x1:
				return WebSocketDataFrameType.TEXT;
			
			case 0x2:
				return WebSocketDataFrameType.BINARY;
			
			case 0x8:
				return WebSocketControlFrameType.CONNECTION_CLOSE;
			
			case 0x9:
				return WebSocketControlFrameType.PING;
			
			case 0xA:
				return WebSocketControlFrameType.PONG;
			
			default:
				throw new InvalidOpcodeException("An opcode of " + opcode + " was received, and matched no valid/recognized frame type.");
			
		}
		
	}
	
}