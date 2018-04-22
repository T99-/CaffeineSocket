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
	final Thread listener;

	/**
	 * The parent WebSocket object which owns/uses this listener.
	 */
	final WebSocket parent;

	/**
	 * The InputStream of the parent WebSocket.
	 */
	InputStream input;
	
	volatile boolean isListening;

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
		this.start();

	}
	
	public boolean start() {
		
		if (!isListening && !listener.isAlive()) {
			
			isListening = true;
			listener.start();
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	public boolean stop() {
		
		if (isListening && listener.isAlive()) {
			
			isListening = false;
			
			try {
				
				listener.join();
				
			}
			catch (InterruptedException e) {
				
				if (CaffeineSocket.getDebug()) System.out.println("Interrupted the " + listener.getName() + " thread while attempting to join it.");
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}

	/**
	 * Continuously checks the InputStream for available bytes.
	 */
	@Override
	public void run() {
		
		boolean frameComplete;

		while (isListening) {
			
			WebSocketFrame frame = new WebSocketFrame(parent, true); // TODO - Unhardcode this value - not always going to be a server.
			frameComplete = false;
			
			while (!frameComplete) {

				try {

					if (input != null && input.available() > 0) {
						
						frameComplete = frame.process((byte) input.read());

					}

				} catch (IOException e) {
					
					if (CaffeineSocket.getDebug()) System.out.println("Attempted to read from closed parent WebSocket SocketInputStream.");
					this.stop();
					// break;

				} catch (IllegalStateException e) {
					
					if (CaffeineSocket.getDebug()) System.out.println(e.getMessage() + ": closing parent WebSocket...");
					parent.close();
					stop();
					// break;
					
				}

			}

		}

	}

}