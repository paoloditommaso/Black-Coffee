package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import java.io.File;

import org.blackcoffee.parser.AssertionContext;
import org.junit.Before;
import org.junit.Test;

public class FileAssertionTest {

	private FileAssertion test;


	@Before
	public void before() { 
		test = new FileAssertion("./test/sample.txt");
		test.initialize(new AssertionContext("."));
	}
	
	
	@Test
	public void testExist() { 
		assertTrue(test .exists());
	}

	@Test
	public void testContains() { 
		assertTrue(test.contains("Hello"));
		assertFalse(test.contains("xx"));
	}

	@Test
	public void testStartsWith() { 
		assertTrue( test .startsWith("Hello") );
		assertFalse( test.startsWith("xx") );
	}



	@Test
	public void testEndsWith() { 
		assertTrue( test .endsWith("World!") );
		assertFalse( test.endsWith("xx") );

	}


	@Test
	public void testMatches() { 
		assertTrue( test .matches("World!") );
		assertFalse( test.matches("xx") );
	}

	
	@Test
	public void testPaths()  { 

		/* create with an aboslute path */
		test = new FileAssertion("/hola.txt");
		test.initialize(null);
		assertEquals( new File("/hola.txt"), test.file);

		/* creates with relative path */
		test = new FileAssertion("hola.txt");
		test.initialize(null);
		assertEquals( new File("hola.txt"), test.file);

		
		/* creates with a relative path but initialize with  a root context path */
		test = new FileAssertion("hola.txt");
		test.initialize(new AssertionContext("/some/path"));
		assertEquals( new File("/some/path/hola.txt"), test.file);
		
		
	}
	
}
