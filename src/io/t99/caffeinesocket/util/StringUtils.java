package io.t99.caffeinesocket.util;
//Created by Trevor Sears <trevorsears.main@gmail.com> at 11:45 PM, November 23, 2017.

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class StringUtils {
	
	public static boolean isPureASCII(String string) {
		
		CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
		return asciiEncoder.canEncode(string);
		
	}
	
}