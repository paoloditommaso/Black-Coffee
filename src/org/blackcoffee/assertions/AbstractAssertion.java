package org.blackcoffee.assertions;

import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.parser.AssertionContext;

/**
 * A generic assertion exception 
 * 
 * @author Paolo Di Tommaso
 *
 */
public abstract class AbstractAssertion {

	abstract public void initialize( AssertionContext context ) ; 
	
	
	final public void fail() { 
		throw new AssertionFailed();
	}
	
	final public void fail(String message, Object... args) { 
		throw new AssertionFailed(message, args);
	}
}
