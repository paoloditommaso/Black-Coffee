package org.blackcoffee.exception;

@SuppressWarnings("serial")
public class BlackCoffeeException extends RuntimeException {

	public BlackCoffeeException() {
		super();
	}
	
	public BlackCoffeeException(Throwable t) {
		super(t);
	}
	
	public BlackCoffeeException(String message, Object... args) {
		super( message != null ? String.format(message,args) : message );
	}

	public BlackCoffeeException(Throwable e, String message, Object... args) {
		super(String.format(message,args),e);
	}
	
}