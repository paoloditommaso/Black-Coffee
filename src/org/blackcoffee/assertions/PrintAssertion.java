package org.blackcoffee.assertions;

import org.blackcoffee.parser.AssertionContext;

/**
 * Use in the assert declaration to print out something to the stdout console 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class PrintAssertion extends AbstractAssertion {
	
	private String str;

	public PrintAssertion(String strToPrint) { 
		this.str = strToPrint;
	}
	
	@Override
	public void initialize(AssertionContext context) {
		System.out.println(str);
	}
	
	public String toString() { 
		return "PrintAssertion["+str+"]";
	}
	

}
