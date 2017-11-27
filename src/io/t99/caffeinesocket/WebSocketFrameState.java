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
 * Enumeration of the possible states of a WebSocketFrame.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 * @see WebSocketFrame
 * @see WebSocketFrame#state
 * @see WebSocketListener#run()
 */
public enum WebSocketFrameState {
	
	/**
	 * The WebSocketFrame is incomplete.
	 * <p>
	 * <code>(rawMessage.size() - headerSize) &#60; payloadLengthBytes * 8</code>
	 */
	INCOMPLETE,
	
	/**
	 * The WebSocketFrame is complete.
	 * <p>
	 * <code>(rawMessage.size() - headerSize) == payloadLengthBytes * 8</code>
	 */
	COMPLETE,
	
	/**
	 * An error has occurred while processing the message. The message is corrupt or otherwise inaccessible.
	 */
	ERROR
	
}
