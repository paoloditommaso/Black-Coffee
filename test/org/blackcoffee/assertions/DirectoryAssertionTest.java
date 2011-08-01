package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import org.blackcoffee.parser.AssertionContext;
import org.junit.Test;

public class DirectoryAssertionTest {

	
	@Test 
	public void testContains ()  { 
		DirectoryAssertion dir = new DirectoryAssertion("./test");
		dir.initialize( new AssertionContext(".") );
		
		// verify that a file specifid by name exists 
		dir.contains("sample.txt");
		
		// wildcards are supported 
		dir.contains("sample*");
		
		// the following file is not contained in the path
		try { 
			dir.contains("hola");
			fail("File hola should not exist");
			
		} catch( AssertionFailed e ) { 
			// OK
		}
	}
	
	
	@Test 
	public void testIsEmpty() { 
		DirectoryAssertion dir = new DirectoryAssertion("./none");
		dir.initialize(new AssertionContext("./") );
		// the method will raise an exception is is not empty 
		dir.isEmpty();
		
		dir = new DirectoryAssertion("./tests");
		dir.initialize(new AssertionContext("./") );
		
		try { 
			dir.isEmpty();
			fail();
		} 
		catch( AssertionFailed e ) { 
			// ok
		}
		
	}
	
	
}
