package org.blackcoffee;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestResultTest {

	
	@Test
	public void testText() { 
		TestResult result = new TestResult();
		
		result.text.print("Hola");
		
		assertEquals( "Hola", result.messages.toString() );
	}
}
