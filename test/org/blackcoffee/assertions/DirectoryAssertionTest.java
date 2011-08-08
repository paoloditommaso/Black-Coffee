package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import java.io.File;

import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.parser.AssertionContext;
import org.junit.Test;

public class DirectoryAssertionTest {

	
	@Test 
	public void testContains ()  { 
		DirectoryAssertion dir = new DirectoryAssertion("test");
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
		DirectoryAssertion dir = new DirectoryAssertion("none");
		dir.initialize(new AssertionContext("./") );
		// the method will raise an exception is is not empty 
		dir.isEmpty();
		
		dir = new DirectoryAssertion("test");
		dir.initialize(new AssertionContext("./") );
		
		try { 
			dir.isEmpty();
			fail();
		} 
		catch( AssertionFailed e ) { 
			// ok
		}
		
	}
	
	@Test
	public void testPaths()  { 
		DirectoryAssertion test;
		
		/* create with an aboslute path */
		test = new DirectoryAssertion("/hola");
		test.initialize(null);
		assertEquals( new File("/hola"), test.directory);

		/* creates with relative path */
		test = new DirectoryAssertion("hola");
		test.initialize(null);
		assertEquals( new File("hola"), test.directory);

		
		/* creates with a relative path but initialize with  a root context path */
		test = new DirectoryAssertion("hola");
		test.initialize(new AssertionContext("/some/path"));
		assertEquals( new File("/some/path/hola"), test.directory);
		
		
	}
}
