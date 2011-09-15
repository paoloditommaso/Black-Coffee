package org.blackcoffee;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.blackcoffee.Config.Stop;
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
	
	
	@Test
	public void testStopCondition() { 
		
		Config config;

		/* 
		 * when stop is NEVER, the stop method will return always false
		 */
		config = Config.parse("--stop", "never").initiliaze();
		assertEquals( Stop.never, config.stop );
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, false));
		// never stops for disabled test 
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, true));
		
		/* 
		 * when stop is NEVER, the stop method will return always false
		 */
		config = Config.parse("--stop", "error").initiliaze();
		assertEquals( Stop.error, config.stop );
		assertTrue( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, false));
		// never stops for disabled test 
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, true));
		
		/* 
		 * when stop is FAILED, the stop method will return always false
		 */
		config = Config.parse("--stop", "failed").initiliaze();
		assertEquals( Stop.failed, config.stop );
		assertTrue( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, false));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, false));
		assertTrue( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, false));
		assertTrue( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, false));
		assertTrue( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, false));
		// never stops for disabled test 
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.ERROR, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.PASSED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.FAILED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.SKIPPED, true));
		assertFalse( BlackCoffeeRunner.stopTestExecution(config, TestStatus.TIMEOUT, true));	
		
	}
}
