package org.blackcoffee;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestStatusTest {

	@Test
	public void testIsFailure() { 
		assertTrue( TestStatus.ERROR.notPassed() );
		assertTrue( TestStatus.FAILED.notPassed() );
		assertTrue( TestStatus.TIMEOUT.notPassed() );
		assertFalse( TestStatus.PASSED.notPassed() );
		assertFalse( TestStatus.SKIPPED.notPassed() );
	}
}
