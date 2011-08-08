package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import java.io.File;

import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.Predicate;
import org.junit.Test;

public class StringAssertionTest {

	
	private AssertionContext context;

	{
		context = new AssertionContext(new File("."));
	}
	
	
	
	boolean eval( String predicate ) { 
		return (Boolean) new Predicate(predicate) .parse() .invoke(context);
	}
	
	@Test 
	public void testEquals() {
		assertTrue( eval( "string hola equals hola" ) );
		assertTrue( eval( "string hola = hola" ) );
		assertTrue( eval( "string hola != ciao" ) );
		assertTrue( eval( "string hola ! = ciao" ) );
		assertTrue( eval( "string hola not = ciao" ) );
		assertTrue( eval( "string hola not != hola" ) );
		
		assertFalse( eval( "string hola = cioa" ) );

	} 	
	
	@Test public void testContains() { 
	
		assertTrue( eval( "string hola contains hola" ) );
		assertTrue( eval( "string hola contains ol" ) );
		assertTrue( eval( "string hola not contains xxx" ) );
		assertTrue( eval( "string hola ! contains xxx" ) );
		
		assertFalse( eval( "string hola contains cioa" ) );
		
	}

	
	@Test public void testMatches() { 
		
		assertTrue( eval( "string hola matches hola" ) );
		assertTrue( eval( "string hola matches h.*a" ) );
		assertTrue( eval( "string hola !matches ciao" ) );
		assertTrue( eval( "string hola ! matches ciao" ) );


		assertFalse( eval( "string hola matches Ciao" ) );
	}	
	
	
	@Test public void testLines() { 
		
		assertTrue( eval( "string 'a\nb\nc' line 1 = a" ) );
		assertTrue( eval( "string 'a\nb\nc' line 2 = b" ) );
		assertTrue( eval( "string 'a\nb\nc' line 3 = c" ) );
		assertTrue( eval( "string 'a\nb\nc' line 4 = ''" ) );


		assertFalse(eval( "string 'a\nb\nc' line 4 = xx" ));
	}	


	
	@Test public void testLength() { 
		
		assertTrue( eval( "string 'abc' length = 3" ) );

		assertTrue( eval( "string 'abc' length != 4" ) );
		assertTrue( eval( "string 'abc' length ! = 4" ) );
		assertTrue( eval( "string 'abc' length not = 4" ) );


		assertFalse( eval( "string 'abc' length = 4" ));
	}
	
	@Test 
	public void testDistance() { 
		assertTrue( eval( "string 'abc' distance abc = 0" ) );
		assertTrue( eval( "string 'abc' distance abx = 1" ) );
		assertTrue( eval( "string 'abc' distance abxx = 2" ) );
		assertTrue( eval( "string 'abc' distance abxxx > 0" ) );

		assertFalse( eval( "string 'abc' distance abc > 0" ) );
	}
	
	
	@Test 
	public void testDifference() {
		assertTrue( eval( "string 'some string here' difference 'some string here' = '' " ) );
		assertTrue( eval( "string 'some string here' difference 'some string ERROR' = 'ERROR' " ) );
	
	} 
}
