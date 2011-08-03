package org.blackcoffee.exception;


@SuppressWarnings("serial")
public class ExitFailed extends BlackCoffeeException {

	public ExitFailed( String message, Object... args ) { 
		super(message,args);
	}
}
