package org.blackcoffee.assertions;

import org.apache.commons.lang.math.NumberUtils;
import org.blackcoffee.parser.AssertionContext;

public class NumberAssertion extends AbstractAssertion implements Comparable<NumberAssertion>{

	Double value;
	
	public NumberAssertion( String value ) { 
		this.value = NumberUtils.createDouble(value);
	}

	
	public int compareTo(NumberAssertion that) {
		return (int) (this.value - that.value);
	}

	@Override
	public void initialize(AssertionContext context) { }
	
		
	public String toString() { 
		return String.valueOf(value != null ? value.intValue() : 0);
	}
}
