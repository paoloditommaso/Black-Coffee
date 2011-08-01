package org.blackcoffee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;


/**
 * Read a TestSuite file 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestSuiteReader {

	
	
	public TestSuite read( File file ) { 
		try {
			return read( new FileReader(file) );
		} 
		catch (IOException e) {
			throw new BlackCoffeeException(e, "Cannot read test file: %s", file);
		}
	}
	
	public TestSuite read( String string ) { 
		try {
			return read( new  StringReader(string) );
		} 
		catch (IOException e) {
			throw new BlackCoffeeException(e, "Cannot parse test string:\n%s", string);
		}
	}
		
	
	enum Status { NONE, GLOBAL_ASSERTION, TEST, ASSERTION } 	

	int testCount=0;
	TestSuite result = new TestSuite();
	
	Status status = Status.NONE;
	List<TestAssertion> globalAssertions = new ArrayList<TestAssertion>();
	List<KeyValue> globalExports = new ArrayList<KeyValue>(); 
	List<KeyValue> localExports = new ArrayList<KeyValue>();
	Map<String,String> variables = new HashMap<String,String>();
		
	
	TestCase newTestCase( String str ) { 
		TestCase test = new TestCase(str);
		result.tests.put( ++testCount, test ); // <-- note the pre-increment, test are indexed in the map using a 1-based sequence number
		return test;
	}
	
	StrLookup variableResolver = new StrLookup() {
		
		@Override
		public String lookup(String key) {

			/*
			 * Try to lookup the value trying the following 
			 * 1. local variables
			 * 2. environment variables
			 */
			
			if( variables.containsKey(key) ) { 
				return variables.get(key);
			}
			
			if( System.getenv().containsKey(key) ) { 
				return System.getenv().get(key);
			}
			
			return null;
		}
	};
	
	StrSubstitutor substitutor = new StrSubstitutor(variableResolver);

	/**
	 * Main reader method 
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	TestSuite read( Reader stream ) throws IOException { 

		Pattern VAR = Pattern.compile("^([\\w_\\.]+)=((?:.|\n)*)");
		

		
		BufferedReader reader = !(stream instanceof BufferedReader) ? new BufferedReader(stream) : (BufferedReader)stream;
		StringBuilder buffer = new StringBuilder();
		String line;
		try {
			int lineCount=0;
			Integer assertCount=null;
			TestCase newTest=null;
			while( (line=reader.readLine()) != null ) {
			    lineCount++; 
			    if( line.matches(".*\\\\\\s*$") ) { 
			    	if(buffer.length()==0) { assertCount=lineCount; }
			    	// remove the trailing backslash and blanks
			    	line = line.replaceAll("\\\\\\s*$", "");
			    	// when the line ends with a DOUBLE backslash '\\' is interpreted as a new line
			    	if( !line.endsWith("\\") ) { 
				    	buffer.append(line);
			    	}
			    	else { 
			    		buffer.append( line.substring(0,line.length()-1) ) .append("\n");
			    	}
			    	continue;
			    }
			    if( buffer.length()>0 ) { 
			    	line = buffer.toString() + line;
			    	buffer.setLength(0);
			    }
			    
			    /* 
			     * replace variables
			     */
			    line = substitutor.replace(line);
			    
			    /* 
			     * handle exports declaration
			     */
			    if( line.startsWith("export: ") || line.startsWith("export ")) { 
			    	KeyValue pair = KeyValue.parse(line.substring(7));
			    	if( pair == null ) { continue; }
			    	if( status == Status.NONE || status == Status.GLOBAL_ASSERTION ){ 
			    		globalExports .add(pair);
			    	}
			    	else { 
			    		localExports .add(pair);
			    	}
			    	continue;
			    }
			    

			    /* 
			     * check for variable definitions 
			     */
			    Matcher matcher;
			    if( (matcher=VAR.matcher(line)) .matches() ) { 
			    	String key = matcher.group(1);
			    	String value = matcher.group(2);
			    	variables.put(key, value);
			    	
			    	continue;
			    }
			    

			    switch (status) { 
			    case NONE:
				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	status = Status.TEST;

				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.env.addAll(localExports); 
				    	
				    	break;
				    }

				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.GLOBAL_ASSERTION;
				    	TestAssertion assertion = new TestAssertion();
				    	assertion.declaration = line;
				    	assertion.line = lineCount;
				    	globalAssertions.add(assertion);
				    }
				    break;

			    
			    case GLOBAL_ASSERTION:
				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.GLOBAL_ASSERTION;
				    	TestAssertion assertion = new TestAssertion();
				    	assertion.declaration = line;
				    	assertion.line = lineCount;
				    	globalAssertions.add(assertion);
					    break;				    	
				    }

				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	status = Status.TEST;
				    	
				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.env.addAll(localExports); 
				    }
			    	break;
				    
			    case TEST:
				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.env.addAll(localExports); 
				    	break;
				    }

				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.ASSERTION;
				    	
			    		newTest.addAssertion(line,assertCount != null ? assertCount : lineCount);
				    	assertCount=null;
				    	localExports.clear();
				    }
				    break;
			    	
			    case ASSERTION: 
				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");

			    		newTest.addAssertion(line,assertCount != null ? assertCount : lineCount);
				    	assertCount=null;
				    	localExports.clear();
					    break;				    	
				    }				    	

				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");

				    	status = Status.TEST;

				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.env.addAll(localExports); 
				    }
				    break;
			    }
			    
			}
			
			/* 
			 * prepend the global exports and assertions
			 */
			for( TestCase test : result.tests.values() ) { 
				test.env.addAll(0, globalExports);
	    		test.assertions.addAll(0, globalAssertions);
			}

		} 
		finally {
			IOUtils.closeQuietly(reader);
		}
		
		
		return result;
	}	
	
	
}



