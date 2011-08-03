package org.blackcoffee.assertions;

import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.StringWrapper;

public class StringAssertion extends AbstractStringAssertion {

	StringWrapper str;
	
	public StringAssertion(String value) {
		str = new StringWrapper(value);
	}  
	
	@Override
	protected StringWrapper content() {
		return str;
	}

	@Override
	public void initialize(AssertionContext context) { /* empty */ }

	public String toString() { 
		return "StringAssertion["+str+"]";
	}
	
}
