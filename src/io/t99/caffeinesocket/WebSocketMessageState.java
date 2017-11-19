package io.t99.caffeinesocket;

/**
 * Enumeration of the possible states of a WebSocketMessage.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 * @see WebSocketMessage
 * @see WebSocketMessage#state
 * @see WebSocketListener#run()
 */
public enum WebSocketMessageState {
	
	/**
	 * The WebSocketMessage is incomplete.
	 * <p>
	 * <code>(rawMessage.size() - headerSize) &#60; payloadLengthBytes * 8</code>
	 */
	INCOMPLETE,
	
	/**
	 * The WebSocketMessage is complete.
	 * <p>
	 * <code>(rawMessage.size() - headerSize) == payloadLengthBytes * 8</code>
	 */
	COMPLETE,
	
	/**
	 * An error has occurred while processing the message. The message is corrupt or otherwise inaccessible.
	 */
	ERROR
	
}
