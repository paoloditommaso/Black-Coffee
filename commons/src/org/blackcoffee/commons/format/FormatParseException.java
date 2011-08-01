package org.blackcoffee.commons.format;

/**
 * Exception raised when something is wrong during sequences parsing 
 * 
 * @author Paolo Di Tommaso
 *
 */
@SuppressWarnings("serial")
public class FormatParseException extends FormatException {

	public FormatParseException(String message, Object... args) {
		super(String.format(message,args));
	}

	public FormatParseException(Throwable e, String message, Object... args) {
		super(String.format(message,args),e);
	}	
}
