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
import java.util.Iterator;

public class ByteList implements Iterable<Byte> {

	private byte[] array;
	
	/**
	 * Acts as the 'cursor' in the array, indicating where the next value should be placed. This therefore means that
	 * `index` also always points to the first non-real value in the array, as while all indices are initialized to
	 * zero, we know that any zero values that have an index equal to or greater than this value are non-real.
	 */
	private int index = 0;
	private int growthStepSize;
	
	public ByteList() {
		
		this(10, 1);
		
	}
	
	public ByteList(int size) {
		
		this(size, 1);
		
	}
	
	public ByteList(int size, int growthStepSize) {
		
		if (size <= 0) throw new IllegalArgumentException("Illegal ByteList size: " + size);
		array = new byte[size];
		this.growthStepSize = growthStepSize;
		
	}
	
	public ByteList(ByteList byteList, int i1, int i2) {
		
		this(byteList, i1, i2, 1);
		
	}
	
	public ByteList(ByteList byteList, int i1, int i2, int growthStepSize) {
		
		this(i2-i1, growthStepSize);
		
		for (int i = i1; i < i2; i++) {
			
			this.add(byteList.get(i));
			
		}
		
	}
	
	public byte get(int index) {
		
		return array[index];
		
	}
	
	public boolean getBit(int bit) {
		
		return NumberBaseConverter.signedByteToBinary(get(bit/8))[bit%8];
		
	}
	
	public boolean getBit(int b, int bit) {

		return NumberBaseConverter.signedByteToBinary(get(b))[bit];

	}
	
	public boolean[] getBits(int i1, int i2) {
		
		boolean[] bits = new boolean[i2 - i1];
		
		for (int i = i1; i < i2; i++) {
			
			bits[i - i1] = getBit(i);
			
		}
		
		return bits;
		
	}
	
	private synchronized void resizeUp() {
		
		// Store a copy of the array in `copy`.
		byte[] copy = array;
		
		// Reset `array` to an array of it's previous size, plus the growth step size.
		array = new byte[copy.length + growthStepSize];
		
		// Copy the previous elements of `array` (currently stored in `copy`) to the new `array` array.
		System.arraycopy(copy, 0, array, 0, copy.length);
		
	}
	
	private synchronized void resizeDown() {
		
		// Store a copy of the array in `copy`.
		byte[] copy = array;
		
		// Reset `array` to an array of it's previous size, minus the growth step size.
		array = new byte[copy.length - growthStepSize];
		
		// Copy the previous elements of `array` (currently stored in `copy`) to the new `array` array.
		System.arraycopy(copy, 0, array, 0, copy.length - growthStepSize);
		
	}
	
	public synchronized void add(byte b) {
		
		// If the current array is not large enough for the new element, call `resizeUp()`.
		if ((index + 1) > array.length) resizeUp();
		
		// Store the new element at the active index of the array.
		array[index] = b;
		
		// Move the index to the new appropriate position.
		index++;
	
	}
	
	public synchronized void add(ByteList b) {
		
		// If the current array is not large enough for the new elements, call `resizeUp()`.
		if ((index + b.size()) > array.length) resizeUp();
		
		// Store the new elements at the active index of the array.
		for (int index = 0; index < b.size(); index++) {
			
			// Store the nth element of b at the this.nth index of the array.
			array[this.index] = b.get(index);
			
			// Move the index to the new appropriate position.
			this.index++;
		
		}
		
	}
	
	public synchronized byte remove(int index) throws IndexOutOfBoundsException {
		
		if (index >= this.index) throw new IndexOutOfBoundsException("Attempted to remove index " + index + ", which is beyond the greatest index of " + (this.index - 1) + ".");
		
		byte returned = array[index];
		byte[] copy = new byte[this.index - (index + 1)];
		System.arraycopy(array, index + 1, copy, 0, copy.length);
		this.index--;
		System.arraycopy(copy, 0, array, index, copy.length);
		
		if (this.index <= (array.length - growthStepSize)) resizeDown();
		
		return returned;
		
	}
	
	public synchronized void insert(int index, byte b) {
	
	
	
	}
	
	
	public int size() {
		
		return index;
		
	}
	
	public String getDebugInfo() {
		
		String info = "";
		
		info += "array:\t\t\t\t"		+ hrArray(array)			+ "\r\n";
		info += "reported size:\t\t"	+ size()					+ "\r\n";
		info += "array.length:\t\t"		+ array.length				+ "\r\n";
		info += "unused indicies:\t"	+ (array.length - size())	+ "\r\n";
		info += "growth step:\t\t"		+ growthStepSize;
		
		return info;
		
	}
	
	public static String hrArray(byte[] bytes) {
		
		String s = "[";
		
		for (int i = 0; i < bytes.length - 1; i++) {
			
			s += bytes[i] + ", ";
			
		}
		
		s += bytes[bytes.length - 1] + "]";
		
		return s;
		
	}
	
	@Override
	public Iterator<Byte> iterator() { // This is nasty and probably grossly inefficient. If only Java supported primitively typed generics.
		
		ArrayList<Byte> arrayList = new ArrayList<>();
		
		byte[] copy = new byte[index];
		System.arraycopy(array, 0, copy, 0, index);
		
		for (byte b: copy) {
			
			arrayList.add(b);
			
		}
		
		return arrayList.iterator();
		
	}
	
}
