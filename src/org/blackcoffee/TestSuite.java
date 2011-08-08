package org.blackcoffee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.blackcoffee.commons.utils.Duration;
import org.blackcoffee.utils.VarHolder;


/** 
 * A test suite is the collection of all tests defined in a test file 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestSuite {

	Duration defTimeout;
	Integer defExitCode = 0;
	
	Map<Integer,TestCase> tests = new TreeMap<Integer, TestCase>();

	List<TestAssertion> globalAssertions = new ArrayList<TestAssertion>();
	List<String> globalExports = new ArrayList<String>(); 

	VarHolder variables = new VarHolder();
	
	List<String> globalBefore = new ArrayList<String>();
	List<String> globalAfter = new ArrayList<String>();
	
	File testFile;

	File inputPath;
	
	/**
	 * Compile the all the test cases 
	 */
	public void compile() {

		int i=0;
		for( TestCase test : tests.values() ) { 
			test.index = ++i;
			// compile the condition 
			if( test.condition != null ) { 
				test.condition.compile(null);
			}
			// compile assertions
			test.compileAssertions();
		}
		
	}
	
	/**
	 * Get the test case by the index number. 
	 * 
	 * @param index The index count starts from 1
	 */
	public TestCase getTest(int index) { 
		TestCase result = tests.get(index);
		if( result == null ) { 
			throw new IllegalArgumentException("Invalid test index number");
		}
		return result;
	}
	
	
	public int size() {
		return tests != null ? tests.size() : 0;
	}

	public void configure(Config config, File testFile) {
		this.testFile = testFile; 
		
		for( TestCase test : tests.values() ) { 
			
			test.configure(testFile, config);
		}

	} 

}

