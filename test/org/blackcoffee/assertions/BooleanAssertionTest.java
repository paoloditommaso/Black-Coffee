package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import java.io.File;

import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.Predicate;
import org.junit.Test;

public class BooleanAssertionTest {

	private AssertionContext context;

	{
		context = new AssertionContext(new File("."));
	}
	
	
	
	boolean eval( String predicate ) { 
		return (Boolean) new Predicate(predicate) .parse() .invoke(context);
	}
	
	@Test
	public void testLiteral() { 
		assertTrue( eval("true = true") ); 
		assertFalse( eval("true = false") ); 
	}
}
