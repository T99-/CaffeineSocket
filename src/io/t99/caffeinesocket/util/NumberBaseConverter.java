package io.t99.caffeinesocket.util;

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

public class NumberBaseConverter {
	
	public static boolean[] decimalToBinary(int dec) {
		
		if (dec == 0) return new boolean[] {false};
		
		int powerOfTwo = (int) (Math.floor(Math.log(dec)/Math.log(2)));
		boolean[] bin = new boolean[powerOfTwo + 1];
		int divisor;
		
		while (dec >= 1) {
			
			powerOfTwo = (int) (Math.floor(Math.log(dec)/Math.log(2)));
			divisor = (int) Math.pow(2, powerOfTwo);
			if (dec >= divisor) bin[(bin.length - 1) - powerOfTwo] = true;
			dec -= divisor;
			
		}
		
		return bin;
		
	}
	
	public static int binaryToDecimal(boolean[] bin) {
		
		int size = bin.length;
		int dec = 0;
		
		for (int bit = 0; bit < size; bit++) {
			
			if (bin[bit]) dec += Math.pow(2, size - bit - 1);
			
		}
		
		return dec;
		
	}
	
	public static int signedByteToDecimal(byte b) {
		
		return (b < 0) ? b + 256: b;
		
	}
	
	public static boolean[] signedByteToBinary(byte b) {

		int dec = signedByteToDecimal(b);
		
		boolean[] bits = {false, false, false, false, false, false, false, false};

		int index = 0;
		
		for (int divisor = 128; divisor >= 1; divisor /= 2) {
			
			if (dec >= divisor) {
				
				bits[index] = true;
				dec -= divisor;
				
			}
			
			index++;
			
		}
		
		return bits;

	}
	
	public static boolean[] ensureBinaryLength(boolean[] original, int intendedLength) {
		
		boolean[] correctlySizedBinary = new boolean[intendedLength];
		
		for (int i = 0; i < intendedLength; i++) {
			
			if (original.length >= i + 1) correctlySizedBinary[i] = original[i];
			else correctlySizedBinary[i] = false;
			
		}
		
		return correctlySizedBinary;
		
	}
	
}