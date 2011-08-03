package org.blackcoffee;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.blackcoffee.commons.utils.Duration;


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

	public File testFile;

	
	/**
	 * Compile the all the test cases 
	 */
	public void compile() {

		int i=0;
		for( TestCase test : tests.values() ) { 
			test.index = ++i;
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

	public void configure(Config config) {
		
		for( TestCase test : tests.values() ) { 
			
			test.variables .putAll( config.vars );
			
			/*
			 *  set the test input path: 
			 *  if the attribute has been specified on the command line use the value provided by 'inputFile'
			 *  otherwise just the directory containing the test file
			 */
			test.inputPath = config.inputPath != null 
					? config.inputPath 
					: testFile.getParentFile();
		}

	} 

}

