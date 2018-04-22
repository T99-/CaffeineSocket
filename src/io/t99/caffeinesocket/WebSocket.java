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

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A WebSocket server.
 *
 * @author <a href="mailto:trevorsears.main@gmail.com">Trevor Sears</a>
 * @version v0.1.0
 */
public class WebSocket {
	
	/**
	 * Globally unique identifier (GUID) used to authenticate the WebSocket connection. As outlined in
	 * <a href="https://tools.ietf.org/html/rfc6455#section-1.3">[RFC6455]</a>, WS_GUID holds the GUID
	 * '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'.
	 */
	public static final String WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	
	/**
	 * Port on which a given {@link WebSocket} instance will listen.
	 */
	private final int port;
	
	private WebSocket.State state = WebSocket.State.PRESTART;
	
	/**
	 * {@link ServerSocket} that acts as the 'Socket' in 'WebSocket'.
	 */
	private ServerSocket socket;
	
	/**
	 * {@link InputStream} from which the {@link #handshake()} method reads the client's half of the opening
	 * handshake, as well as where a given WebSocket instance's {@link WebSocketListener} reads from.
	 */
	private InputStream input;
	
	/**
	 * {@link OutputStream} to which the {@link #handshake()} method writes out this server's half of the opening
	 * handshake, as well as where bytes are written out to a given client.
	 */
	private OutputStream output;
	
	/**
	 * {@link WebSocketListener} that continuously checks a given WebSocket instance's {@link InputStream},
	 * {@link #input} for incoming bytes.
	 */
	private final WebSocketListener listener;
	
	/**
	 * {@link String} that holds either a passed 'name', or a randomly assigned 'name' used for identification of
	 * different WebSockets, as well as for naming the Thread that runs a {@link WebSocketListener}. See
	 * {@link WebSocketListener#provideInputStream(InputStream)}.
	 */
	public final String name;
	
	private boolean secure = false;
	
	public WebSocket() {
		
		this(generateName(), 0);
		
	}
	
	public WebSocket(String name) {
		
		this(name, 0);
		
	}
	
	public WebSocket(int port) {
		
		this(generateName(), port);
		
	}
	
	public WebSocket(String name, int port) {
	
		this.name = name;
		this.port = port;
		listener = new WebSocketListener(this);
	
	}
	
	public boolean handshake() {
		
		state = WebSocket.State.HANDSHAKING;
		
		try {
			
			socket = new ServerSocket(port);
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Could not get the requested port.");
			return false;
			
		}
		
		try {
			
			if (CaffeineSocket.getDebug()) System.out.println("Server has started on " + InetAddress.getLocalHost().getHostAddress() + ":" + port + "." + System.lineSeparator() + "Waiting for a connection..." + System.lineSeparator());
			
		} catch (UnknownHostException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Unknown host.");
			
		}
		
		Socket client;
		
		try {
			
			client = socket.accept();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to accept client connection.");
			return false;
			
		}
		
		if (CaffeineSocket.getDebug()) System.out.println("A client connected." + System.lineSeparator());
		
		try {
			
			input = client.getInputStream();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to get the client's InputStream.");
			return false;
			
		}
		
		try {
			
			output = client.getOutputStream();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to get the client's OutputStream.");
			return false;
			
		}
		
		String receivedClientHeaders = new Scanner(input, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
		
		// Get the WebSocket Key sent by the client.
		// This is extracted via regex from the client's sent headers.
		Pattern p = Pattern.compile("(?<=Sec-WebSocket-Key: )\\S+");
		Matcher m = p.matcher(receivedClientHeaders);
		String websocketReceivedKey = null;
		
		if (m.find()) {
			
			websocketReceivedKey = m.group(0);
			
		}
		
		String websocketAcceptKey;
		
		try {
			
			websocketAcceptKey = DatatypeConverter.printBase64Binary(MessageDigest.getInstance("SHA-1").digest((websocketReceivedKey + WS_GUID).getBytes("UTF-8")));
			
		} catch (Exception e) {
			
			state = WebSocket.State.ERRORED;
			
			return false;
			
		}
		
		byte[] response = (
				"HTTP/1.1 101 Switching Protocols\r\n" +
				"Connection: upgrade\r\n" +
				"Upgrade: websocket\r\n" +
				"Sec-WebSocket-Accept: " + websocketAcceptKey + "\r\n" +
				"\r\n"
		).getBytes(); // TODO - Remember to add conditionals for subprotocols and extensions, as well as different WebSocket versions.
		
		try {
			
			output.write(response, 0, response.length);
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to write the HTTP 101 Switching Protocols response to the client's OutputStream.");
			return false;
			
		}
		
		listener.provideInputStream(input);
		
		state = WebSocket.State.RUNNING;
		
		return true;
	
	}
	
	public boolean close() {
		
		state = WebSocket.State.CLOSING;
		
		try {
			
			input.close();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("InputStream could not be closed.");
			if (CaffeineSocket.getDebug()) e.printStackTrace();
			return false;
			
		}
		
		try {
			
			output.close();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("OutputStream could not be closed.");
			if (CaffeineSocket.getDebug()) e.printStackTrace();
			return false;
			
		}
		
		try {
			
			socket.close();
			
		} catch (IOException e) {
			
			state = WebSocket.State.ERRORED;
			
			if (CaffeineSocket.getDebug()) System.out.println("Socket could not be closed.");
			if (CaffeineSocket.getDebug()) e.printStackTrace();
			return false;
			
		}
		
		state = WebSocket.State.CLOSED;
		
		if (CaffeineSocket.getDebug()) System.out.println("Successfully closed the WebSocket.");
		
		return true;
		
	}
	
	public WebSocket.State getState() {
		
		return state;
		
	}
	
	private static String generateName() {
	
		StringBuilder output = new StringBuilder();
		Random random = new Random(); // random random, random
		
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890=+".toCharArray();
		
		for (int i = 0; i < 8; i++) {
			
			output.append(chars[random.nextInt(chars.length)]);
			
		}
		
		return output.toString();
	
	}
	
	public enum State {
		
		PRESTART		(false),
		HANDSHAKING		(true),
		RUNNING			(true),
		CLOSING			(true),
		CLOSED			(false),
		ERRORED			(false);
		
		/**
		 * Indicates whether or not the WebSocket is currently doing *anything*, not necessarily whether or not it is 'running' as a WebSocket.
		 */
		boolean isOperating; // TODO
		
		State(boolean isOperating) {
			
			this.isOperating = isOperating;
			
		}
		
		public boolean getOperatingStatus() {
			
			return isOperating;
			
		}
		
		public State advance() {
			
			switch (this) {
				
				case PRESTART:
					return HANDSHAKING;
					
				case HANDSHAKING:
					return RUNNING;
					
				case RUNNING:
					return CLOSING;
					
				case CLOSING:
					return CLOSED;
					
				default:
					throw new IllegalStateException("Cannot advance state - state does not have a next chronological step.");
				
			}
			
		}
		
	}

}