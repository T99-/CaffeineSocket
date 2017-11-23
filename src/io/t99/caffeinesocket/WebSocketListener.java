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

import io.t99.caffeinesocket.util.NumberBaseConverter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Listener that processes incoming bytes from a WebSocket client.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public class WebSocketListener implements Runnable {
	
	/**
	 * Thread that continuously runs the listener.
	 */
	Thread listener;
	
	/**
	 * The parent WebSocket object which owns/uses this listener.
	 */
	WebSocket parent;
	
	/**
	 * The InputStream of the parent WebSocket.
	 */
	InputStream input;
	
	/**
	 * Standard constructor for a WebSocketListener.
	 *
	 * @param parent The parent WebSocket object.
	 */
	protected WebSocketListener(WebSocket parent) {
		
		this.parent = parent;
		listener = new Thread(this, "wsl for ws:'" + parent.name + "'");
		
	}
	
	/**
	 * Provides an InputStream for the WebSocketListener to listen on, and starts the {@link Thread}.
	 *
	 * @param inputStream InputStream from which this listener will read.
	 * @see WebSocket#input End of WebSocket.handshake() usage.
	 */
	public void provideInputStream(InputStream inputStream) {
		
		input = inputStream;
		listener.start();
		
	}
	
	/**
	 * Continously checks the InputStream for available bytes.
	 */
	@Override
	public void run() {
	
		WebSocketMessageState messageState = WebSocketMessageState.INCOMPLETE;
		
		while (true) {
		
			WebSocketMessage message = new WebSocketMessage();
			
			while (messageState == WebSocketMessageState.INCOMPLETE) {
				
				try {
					
					if (input != null && input.available() > 0) {
						
						messageState = message.process(NumberBaseConverter.decToBin(input.read()));
					
					}
					
				} catch (IOException e) {
					
					if (CaffeineSocket.getDebug()) System.out.println("not sure what this exception is");
					
				}
				
				if (messageState == WebSocketMessageState.ERROR) {
					
					parent.close();
					
				}
				
			}
		
		}
	
	}
	
}