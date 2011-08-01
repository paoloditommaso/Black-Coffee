package org.blackcoffee.assertions;

import static org.junit.Assert.*;

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

}
