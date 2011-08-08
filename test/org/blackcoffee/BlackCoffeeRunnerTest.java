package org.blackcoffee;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class BlackCoffeeRunnerTest {

	
	@Test
	public void testTag() { 
		/* 
		 * declare a test with no tags 
		 */
		TestCase test = new TestCase("cmd");
		assertTrue(BlackCoffeeRunner.matchTag(test, null));						// any tag is required 
		assertFalse(BlackCoffeeRunner.matchTag(test, Arrays.asList("test")));	// a tag 'test'
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("!test")));	// all except 'test'

		/*
		 * add a tag hola
		 */
		test.addTag("hola");
		assertTrue(BlackCoffeeRunner.matchTag(test, null));						// any tag is required 
		assertFalse(BlackCoffeeRunner.matchTag(test, Arrays.asList("test")));	// a tag 'test'
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("!test")));	// all except 'test'

		assertTrue(BlackCoffeeRunner.matchTag(test, null));						// any tag is required 
		assertFalse(BlackCoffeeRunner.matchTag(test, Arrays.asList("!hola")));	// any tag but not hola
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("hola")));	// 
		
		
		/*
		 * two tag
		 */
		
		test = new TestCase("cmd");
		test.addTag("uno");
		test.addTag("dos");
		
		assertTrue(BlackCoffeeRunner.matchTag(test, null));						// any tag is required 
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("uno")));		// 
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("dos")));		// 
		assertFalse(BlackCoffeeRunner.matchTag(test, Arrays.asList("hola")));		// 
		assertTrue(BlackCoffeeRunner.matchTag(test, Arrays.asList("hola","uno")));		// 
		assertFalse(BlackCoffeeRunner.matchTag(test, Arrays.asList("!uno")));		// 
	}
}
