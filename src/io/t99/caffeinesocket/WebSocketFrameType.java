package io.t99.caffeinesocket;

import io.t99.caffeinesocket.exceptions.InvalidOpcodeException;

public interface WebSocketFrameType {
	
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