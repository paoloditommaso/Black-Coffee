package org.blackcoffee.assertions;

import org.blackcoffee.TestAssertion;

/**
 * Notify an assertion failure 
 * 
 * @author Paolo Di Tommaso
 *
 */
@SuppressWarnings("serial")
public class AssertionFailed extends RuntimeException {
	
	public TestAssertion assertion;

	public AssertionFailed() { super((String)null); } 
	
	public AssertionFailed( String message, Object ... args ) {
		super(String.format(message, args));
	}
	
	public AssertionFailed( Throwable e ) { 
		super(e);
	}
	
	public AssertionFailed( Throwable e, String message, Object ... args ) { 
		super(String.format(message,args),e);
	}
	
	public AssertionFailed( Throwable cause, TestAssertion assertion ) { 
		super(cause);
		this.assertion = assertion;
	}
}
