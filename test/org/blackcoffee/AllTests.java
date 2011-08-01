package org.blackcoffee;

import org.blackcoffee.assertions.AstractStringAssertionTest;
import org.blackcoffee.assertions.DirectoryAssertionTest;
import org.blackcoffee.assertions.FileAssertionTest;
import org.blackcoffee.assertions.NumberAssertionTest;
import org.blackcoffee.assertions.StringAssertionTest;
import org.blackcoffee.commons.utils.ReaderIteratorTest;
import org.blackcoffee.parser.AssertionPredicateTest;
import org.blackcoffee.parser.AssertionTokenizerTest;
import org.blackcoffee.parser.PredicateTermTest;
import org.blackcoffee.parser.StringWrapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	TestSuiteReaderTest.class,
	KeyValueTest.class,
	
	/*
	 * assertions 
	 */
	DirectoryAssertionTest.class,
	FileAssertionTest.class,
	NumberAssertionTest.class,
	AstractStringAssertionTest.class,
	StringAssertionTest.class, 
	
	/*
	 * Parser
	 */
	AssertionPredicateTest.class,
	AssertionTokenizerTest.class,
	StringWrapperTest.class,
	PredicateTermTest.class,
	
	/*
	 * Utils
	 */
	ReaderIteratorTest.class
})


public class AllTests {


}
