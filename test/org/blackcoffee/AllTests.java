package org.blackcoffee;

import org.blackcoffee.assertions.AstractStringAssertionTest;
import org.blackcoffee.assertions.DirectoryAssertionTest;
import org.blackcoffee.assertions.FileAssertionTest;
import org.blackcoffee.assertions.NumberAssertionTest;
import org.blackcoffee.assertions.StringAssertionTest;
import org.blackcoffee.command.CommandTest;
import org.blackcoffee.command.TcoffeeCommandTest;
import org.blackcoffee.commons.utils.ReaderIteratorTest;
import org.blackcoffee.parser.AssertionPredicateTest;
import org.blackcoffee.parser.PredicateTermTest;
import org.blackcoffee.parser.StringWrapperTest;
import org.blackcoffee.utils.KeyValueListTest;
import org.blackcoffee.utils.KeyValueTest;
import org.blackcoffee.utils.QuoteStringTokenizerTest;
import org.blackcoffee.utils.VarHolderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	TestSuiteReaderTest.class,
	TestResultTest.class,
	
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
	QuoteStringTokenizerTest.class,
	StringWrapperTest.class,
	PredicateTermTest.class,
	
	/*
	 * command 
	 */
	CommandTest.class,
	TcoffeeCommandTest.class,
	
	/*
	 * Utils
	 */
	KeyValueTest.class,
	KeyValueListTest.class,
	VarHolderTest.class,
	
	ReaderIteratorTest.class
})


public class AllTests {


}
