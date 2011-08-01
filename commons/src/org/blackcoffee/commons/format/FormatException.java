package org.blackcoffee.commons.format;

@SuppressWarnings("serial")
public class FormatException extends RuntimeException {

	public FormatException() {} 
	
	public FormatException( String message, Object ... args ) { 
		super(String.format(message, args));
	}
	
	public FormatException(Throwable e) {
		super(e);
	} 
	
	public FormatException( Throwable e, String message, Object ... args ) { 
		super(String.format(message, args), e);
	}
	
}
