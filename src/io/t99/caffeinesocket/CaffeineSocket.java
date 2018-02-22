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

import io.t99.caffeinesocket.util.ByteList;

public class CaffeineSocket {
	
	private static boolean debug = false;
	
	// TODO - Provide option for asynchronous start of a server WebSocket.
	
	public static void main(String[] args) {
		
		setDebug(true);
		
		WebSocket webSocket = new WebSocket("main ws", 1200); // TODO - not yet fully functional, sometimes fails
		webSocket.handshake();
		
	}
	
	public static void setDebug(boolean bool) {
		
		debug = bool;
		
	}
	
	public static boolean getDebug() {
		
		return debug;
		
	}
	
	public static void shifter() {
		
		/*
		 * dec 0	= bin 0000 = hex 0
		 * dec 1	= bin 0001 = hex 1
		 * dec 2	= bin 0010 = hex 2
		 * dec 3	= bin 0011 = hex 3
		 * dec 4	= bin 0100 = hex 4
		 * dec 5	= bin 0101 = hex 5
		 * dec 6	= bin 0110 = hex 6
		 * dec 7	= bin 0111 = hex 7
		 * dec 8	= bin 1000 = hex 8
		 * dec 9	= bin 1001 = hex 9
		 * dec 10	= bin 1010 = hex A
		 * dec 11	= bin 1011 = hex B
		 * dec 12	= bin 1100 = hex C
		 * dec 13	= bin 1101 = hex D
		 * dec 14	= bin 1110 = hex E
		 * dec 15	= bin 1111 = hex F
		 */
		
		int preshift = 0xFF;
		int postshift = preshift >> 1;
		
		int[] nums = {preshift, postshift};
		
		for (int i: nums) {
			
			System.out.println("Decimal Representation:\t\t" + i);
			System.out.println("Hexadecimal Representation:\t0x" + Integer.toHexString(i).toUpperCase());
			System.out.println("Binary Representation:\t\t" + Integer.toBinaryString(i) + " (length: " + Integer.toBinaryString(i).length() + " bits, " + Integer.toBinaryString(i).length() / 8 + " bytes)");
			System.out.println();
			
		}
		
	}
	
}
