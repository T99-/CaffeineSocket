package io.t99.caffeinesocket.util;

import java.util.ArrayList;

public class NumberBaseConverter {
	
	public static Binary decToBin(int dec) {
		
		ArrayList<Boolean> binary = new ArrayList<>();
		binary.add(true);
		int factor = 0;
		int i1 = 1;
		boolean found = false;
		
		if (dec == 0) {
			
			return new Binary(new Boolean[] {false, false, false, false, false, false, false, false});
			
		}
		
		while (!found) {
			
			if (i1 * 2 > dec) {
				
				factor = i1;
				found = !found;
				
			} else {
				
				binary.add(false);
				i1 *= 2;
				
			}
			
		}
		
		dec -= factor;
		
		for (int i2 = new Double(Math.log(factor)/Math.log(2)).intValue(); i2 >= 0; i2--) {
			
			if (factor <= dec) {
				
				dec -= factor;
				binary.set(binary.size() - (i2 + 1), true);
				
			}
			
			factor /= 2;
			
		}
		
		while (binary.size() % 8 != 0) {
			
			binary.add(0, false);
			
		}
		
		return new Binary(binary);
		
	}
	
	public static int binToDec(Binary bin) {
		
		int size = bin.size() - 1;
		int dec = 0;
		
		for (int bit = 0; bit <= size; bit++) {
			
			if (bin.getBit(size - bit)) {
				
				dec += Math.pow(2, bit);
				
			}
			
		}
		
		return dec;
		
	}
	
}