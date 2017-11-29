package io.t99.caffeinesocket.util;

public class ByteList {

	private byte[] array;
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
		growthStepSize = growthStepSize;
		
	}
	
	public void add(byte b) {
	
		if (index > array.length) {
		
			byte[] copy = array;
			
			//for (int index = 0; index < )
		
		} else {
			
			array[index] = b;
			
		}
	
	}
	
	public void insert(int index, byte b) {
	
	
	
	}

}
