package org.blackcoffee.assertions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.parser.AssertionContext;

/**
 * Defines assertions for the program exit value. 
 * 
 * The exit value is specified by a file '.exitcode' in the current directory
 * 
 * @author Paolo Di Tommaso
 *
 */
public class ExitAssertion extends NumberAssertion {

	public ExitAssertion( ) {
		super("0");
	}
	
	ExitAssertion( String value ) { 
		super(value);
	}
	
	@Override
	public void initialize(AssertionContext ctx)  {
		try { 
			value = Double.parseDouble(FileUtils.readFileToString(new File(ctx.path,".exitcode")));
		}
		catch( Exception e ) { 
			throw new BlackCoffeeException(e, "Cannot initialize ExitAssertion");
		}
		
	}

	public String toString() { 
		return String.valueOf(value != null ? value.intValue() : 0);
	}
	
}
