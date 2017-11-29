package io.t99.caffeinesocket.util;

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

import java.util.ArrayList;
import java.util.Arrays;

public class Binary {
	
	ArrayList<Boolean> bin = new ArrayList<>();
	
	public Binary() {}
	
	public Binary(ArrayList<Boolean> binary) {
		
		bin.addAll(binary);
		
	}
	
	public Binary(Boolean[] binary) {
		
		bin.addAll(Arrays.asList(binary));
		
	}
	
	public Binary(Binary binary, int i1) {
		
		bin.addAll(Arrays.asList(binary.getSubset(i1).toBooleanArray()));
		
	}
	
	public Binary(Binary binary, int i1, int i2) {
		
		bin.addAll(Arrays.asList(binary.getSubset(i1, i2).toBooleanArray()));
		
	}
	
	public int size() {
		
		return bin.size();
		
	}
	
	public void append(Binary binary) {
		
		bin.addAll(binary.bin);
		
	}
	
	public void append(boolean bit) {
		
		bin.add(bit);
		
	}
	
	public void prepend(Binary binary) {
		
		bin.addAll(0, binary.bin);
		
	}
	
	public void prepend(boolean bit) {
		
		bin.add(0, bit);
		
	}
	
	public boolean getBit(int index) {
		
		return bin.get(index);
		
	}
	
	public Binary getSubset(int i1) throws IndexOutOfBoundsException {
		
		if (i1 < 0 || i1 > bin.size()) throw new IndexOutOfBoundsException();
		
		return getSubset(i1, bin.size());
		
	}
	
	public Binary getSubset(int i1, int i2) throws IndexOutOfBoundsException {
		
		if (i1 < 0 || i1 >= i2 || i2 > bin.size()) throw new IndexOutOfBoundsException();
		
		ArrayList<Boolean> subset = new ArrayList<>();
		
		for (int i = i1; i < i2; i++) {
			
			subset.add(bin.get(i));
			
		}
		
		return new Binary(subset);
		
	}
	
	public static Binary logicalNot(Binary binary) {
		
		ArrayList<Boolean> output = new ArrayList<>();
		
		for (int i = 0; i < binary.size(); i++) {
			
			output.add(!binary.getBit(i));
			
		}
		
		return new Binary(output);
		
	}
	
	public static Binary logicalAnd(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		if (binary1.size() != binary2.size()) throw new IndexOutOfBoundsException("The two provided Binary objects were not of equal length.");
		
		ArrayList<Boolean> output = new ArrayList<>();
		
		for (int i = 0; i < binary1.size(); i++) {
			
			output.add(binary1.getBit(i) ^ binary2.getBit(i));
			
		}
		
		return new Binary(output);
		
	}
	
	public static Binary logicalOr(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		if (binary1.size() != binary2.size()) throw new IndexOutOfBoundsException("The two provided Binary objects were not of equal length.");
		
		ArrayList<Boolean> output = new ArrayList<>();
		
		for (int i = 0; i < binary1.size(); i++) {
			
			output.add(binary1.getBit(i) ^ binary2.getBit(i));
			
		}
		
		return new Binary(output);
		
	}
	
	public static Binary logicalXor(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		if (binary1.size() != binary2.size()) throw new IndexOutOfBoundsException("The two provided Binary objects were not of equal length.");
		
		ArrayList<Boolean> output = new ArrayList<>();
		
		for (int i = 0; i < binary1.size(); i++) {
			
			output.add(binary1.getBit(i) ^ binary2.getBit(i));
			
		}
		
		return new Binary(output);
		
	}
	
	public static Binary logicalNand(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		return logicalNot(logicalAnd(binary1, binary2));
		
	}
	
	public static Binary logicalNor(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		return logicalNot(logicalOr(binary1, binary2));
		
	}
	
	public static Binary logicalXnor(Binary binary1, Binary binary2) throws IndexOutOfBoundsException {
		
		return logicalNot(logicalXor(binary1, binary2));
		
	}
	
	//public static Binary signedLeftShift(Binary binary, int positions) { /* TODO */ }
	
	//public static Binary signedRightShift(Binary binary, int positions) { /* TODO */ }
	
	//public static Binary unsignedRightShift(Binary binary) { /* TODO */ }
	
	public Boolean[] toBooleanArray() {
		
		return bin.toArray(new Boolean[0]);
		
	}
	
	public Binary[] toBinaryOctetArray() {
		
		ArrayList<Binary> octetList = new ArrayList<>();
		
		for (int i = 0; i < bin.size() / 8; i++) {
			
			octetList.add(getSubset(i * 8, (i * 8) + 8));
			
		}
		
		return octetList.toArray(new Binary[0]);
		
	}
	
	public byte[] toByteArray() {
		
		Binary[] octets = toBinaryOctetArray();
		byte[] bytes = new byte[octets.length];
		
		for (int index = 0; index < octets.length; index++) {
			
			bytes[index] = (byte) NumberBaseConverter.binToDec(octets[index]);
			
		}
		
		return bytes;
		
	}
	
	@Override
	public String toString() {
		
		String output = "";
		
		for (boolean bit: bin) {
			
			output += (bit) ? "1" : "0";
			
		}
		
		return output;
		
	}
	
	public String toFormattedString(boolean multiline, boolean nibbleSpaces, boolean octetSpaces) {
		
		StringBuilder output = new StringBuilder(toString());
		
		if (octetSpaces && !nibbleSpaces && !multiline) {
			
			int lengthWithOctetSpaces = output.length() + (output.length() / 8);
			
			for (int i = 8; i < lengthWithOctetSpaces - 1; i += 9) {
				
				output.insert(i, " ");
				
			}
			
		}
		
		if (nibbleSpaces) {
			
			int lengthWithNibbleSpaces = output.length() +  (output.length() / ((multiline) ? 9 : 5));
			
			for (int i = 4; i <= lengthWithNibbleSpaces - 1; i += ((multiline) ? 9 : 5)) {
				
				output.insert(i, " ");
				
			}
			
		}
		
		if (multiline) {
			
			int lengthWithMultiline = output.length() + output.length() / ((nibbleSpaces) ? 9 : 8);
			
			for (int i = ((nibbleSpaces) ? 9 : 8); i <= lengthWithMultiline; i += ((nibbleSpaces) ? 11 : 10)) {
				
				output.insert(i, "\r\n");
				
			}
			
		}
		
		return output.toString();
		
	}
	
}