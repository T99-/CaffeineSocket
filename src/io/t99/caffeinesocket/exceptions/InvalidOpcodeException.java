package io.t99.caffeinesocket.exceptions;
//Created by Trevor Sears <trevorsears.main@gmail.com> at 11:08 PM, November 23, 2017.

public class InvalidOpcodeException extends Exception {
	
	public InvalidOpcodeException() {
		
		super();
		
	}
	
	public InvalidOpcodeException(String message) {
		
		super(message);
		
	}
	
	public InvalidOpcodeException(String message, Throwable cause) {
		
		super(message, cause);
		
	}
	
	public InvalidOpcodeException(Throwable cause) {
		
		super(cause);
		
	}
	
	protected InvalidOpcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		
		super(message, cause, enableSuppression, writableStackTrace);
		
	}
	
}