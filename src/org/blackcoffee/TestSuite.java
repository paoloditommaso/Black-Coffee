package org.blackcoffee;

import java.util.Map;
import java.util.TreeMap;


/** 
 * A test suite is the collection of all tests defined in a test file 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestSuite {

	
	Map<Integer,TestCase> tests = new TreeMap<Integer, TestCase>();

	
	
	/**
	 * Compile the all the test cases 
	 */
	public void compile() {

		int i=0;
		for( TestCase test : tests.values() ) { 
			test.index = ++i;
			test.compile();
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

}

