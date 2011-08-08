package org.blackcoffee.assertions;

import org.blackcoffee.parser.AssertionContext;

public class BoolAssertion extends AbstractAssertion implements Comparable<BoolAssertion> {

	Boolean value;
	
	public BoolAssertion( String bool ) { 
		value = Boolean.parseBoolean(bool);
	}
	
	@Override
	public void initialize(AssertionContext context) {
		// nothing 
	}
	
	
	@Override
	public int compareTo(BoolAssertion other) {
		return value.compareTo(other.value);
	}

}
