package io.t99.caffeinesocket;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private int port;
	
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
	 * {@link WebSocketListener} that continously checks a given WebSocket instance's {@link InputStream},
	 * {@link #input} for incoming bytes.
	 */
	private WebSocketListener listener;
	
	/**
	 * {@link String} that holds either a passed 'name', or a randomly assigned 'name' used for identification of
	 * different WebSockets, as well as for naming the Thread that runs a {@link WebSocketListener}. See
	 * {@link WebSocketListener#provideInputStream(InputStream)}.
	 */
	public final String name;
	
	/**
	 *
	 */
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
		
		try {
			
			socket = new ServerSocket(port);
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("Could not get the requested port.");
			return false;
			
		}
		
		try {
			
			if (CaffeineSocket.getDebug()) System.out.println("Server has started on " + socket.getInetAddress().getLocalHost().getHostAddress() + ":" + port + ".\r\nWaiting for a connection...\r\n");
			
		} catch (UnknownHostException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("Unknown host.");
			
		}
		
		Socket client;
		
		try {
			
			client = socket.accept();
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to accept client connection.");
			return false;
			
		}
		
		if (CaffeineSocket.getDebug()) System.out.println("A client connected.\r\n");
		
		try {
			
			input = client.getInputStream();
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to get the client's InputStream.");
			return false;
			
		}
		
		try {
			
			output = client.getOutputStream();
			
		} catch (IOException e) {
			
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
			
			if (CaffeineSocket.getDebug()) System.out.println("Failed to write the HTTP 101 Switching Protocols response to the client's OutputStream.");
			return false;
			
		}
		
		listener.provideInputStream(input);
		
		return true;
	
	}
	
	public boolean close() {
		
		try {
			
			input.close();
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("InputStream could not be closed.");
			if (CaffeineSocket.getDebug()) System.err.println(e);
			return false;
			
		}
		
		try {
			
			output.close();
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("OutputStream could not be closed.");
			if (CaffeineSocket.getDebug()) System.err.println(e);
			return false;
			
		}
		
		try {
			
			socket.close();
			
		} catch (IOException e) {
			
			if (CaffeineSocket.getDebug()) System.out.println("Socket could not be closed.");
			if (CaffeineSocket.getDebug()) System.err.println(e);
			return false;
			
		}
		
		return true;
		
	}
	
	private static String generateName() {
	
		String output = "";
		Random random = new Random();
		
		char[] chars = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890=+".toCharArray();
		
		for (int i = 0; i < 8; i++) {
			
			output += chars[random.nextInt(chars.length)];
			
		}
		
		return output;
	
	}

}
