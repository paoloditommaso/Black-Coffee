package org.blackcoffee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.blackcoffee.commons.utils.Duration;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.utils.KeyValue;
import org.blackcoffee.utils.PathUtils;
import org.blackcoffee.utils.VarHolder;


/**
 * Read a TestSuite file 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestSuiteReader {

	
	enum Status { NONE, GLOBAL_ASSERTION, TEST, ASSERTION } 	

	int testCount = 0;
	
	final TestSuite result;
	
	Status status = Status.NONE;
	
	List<String> localExports = new ArrayList<String>();
	
	PathUtils path = new PathUtils();
	
	/** Creates an TesSuite reader with an empty template */
	public TestSuiteReader() {
		this(null);
	} 
	
	/**
	 * Create reader with a precomfigured template 
	 */
	public TestSuiteReader( TestSuite suite ) { 
		this.result = suite != null ? suite : new TestSuite();
	}
	
	
	public TestSuite read( File file ) { 
		try {
			// note: setting the file content folder has the current file, so that non-absolute paths (e.g. ./path/to/input )
			// will be relative to the current file 
			path.current( file.getParentFile() );	
			
			// now read the file 
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
		


	
	TestCase newTestCase( String str ) { 
		TestCase test = new TestCase(str);
		test.variables = new VarHolder( result.variables );	// make a 'snapshot' of current variables state
		
		if( result.globalBefore.size()>0 ) for( String cmd : result.globalBefore) { 
			test.addBeforeCommand(cmd);
		}
		
		if( result.globalAfter.size()>0  ) for( String cmd : result.globalAfter ) { 
			test.addAfterCommand(cmd);
		}
		
		if( result.defTimeout != null ) { 
			test.timeout = result.defTimeout;
		}
		test.inputPath = result.inputPath;
		test.exit = result.defExitCode;
		
		result.tests.put( ++testCount, test ); 			// <-- note the pre-increment, test are indexed in the map using a 1-based sequence number
		return test;
	}
	

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
			     * handle exports declaration
			     */
			    if( line.startsWith("export: ") || line.startsWith("export ")) { 
			    	KeyValue pair = KeyValue.parse(line.substring(7));
			    	if( pair == null ) { continue; }
			    	
			    	// an export defines implicitly a new variable
			    	if( pair.value != null ) { 
			    		result.variables.put(pair.key, pair.value);
			    	}
			    	
			    	// add the export name to the right list
			    	if( status == Status.NONE || status == Status.GLOBAL_ASSERTION ){ 
			    		if(!result.globalExports.contains(pair.key) ) { 
			    			result.globalExports.add(pair.key);
			    		}
			    	}
			    	else { 
			    		localExports .add(pair.key);
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
			    	result.variables.put(key, value);
			    	
			    	continue;
			    }
			    

			    switch (status) { 
			    case NONE:
				    if( line.startsWith("timeout:") ) { 
			    		String duration = line.substring("timeout:".length()).trim();
			    		result.variables.resolve(duration);
			    		result.defTimeout = Duration.parse(duration);
				    	break;
				    }
				    
				    if( line.startsWith("before:") ) { 
				    	result.globalBefore.add(line.substring("before:".length()).trim());
				    	break;
				    }
				    
				    if( line.startsWith("after:")) { 
				    	result.globalAfter.add(line.substring("after:".length()).trim());
				    	break;
				    }
		
				    if( line.startsWith("exit:")) { 
				    	String val = line.substring("exit:".length()).trim();
				    	result.defExitCode = NumberUtils.isDigits(val) ?  NumberUtils.toInt(val) : null; 
				    	break;
				    }
				    
				    if( line.startsWith("input:") ) { 
				    	result.inputPath = path.absolute(line.substring("input:".length()).trim()) ;
				    	break;
				    }

				    
				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	status = Status.TEST;

				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.exports.addAll(localExports); 
				    	
				    	break;
				    }

				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.GLOBAL_ASSERTION;
				    	
				    	TestAssertion assertion = new TestAssertion();
				    	assertion.declaration = line;
				    	assertion.line = lineCount;
				    	assertion.variables = new VarHolder(result.variables);
				    	result.globalAssertions.add(assertion);
				    }
				    break;

			    
			    case GLOBAL_ASSERTION:
				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.GLOBAL_ASSERTION;
				    	TestAssertion assertion = new TestAssertion();
				    	assertion.declaration = line;
				    	assertion.line = lineCount;
				    	assertion.variables = new VarHolder(result.variables);
				    	result.globalAssertions.add(assertion);
					    break;				    	
				    }

				    if( line.startsWith("timeout:") ) { 
			    		String duration = line.substring("timeout:".length()).trim();
			    		result.variables.resolve(duration);
			    		result.defTimeout = Duration.parse(duration);
				    	break;
				    }

				    if( line.startsWith("before:") ) { 
				    	result.globalBefore.add(line.substring("before:".length()).trim());
				    	break;
				    }
				    
				    if( line.startsWith("after:")) { 
				    	result.globalAfter.add(line.substring("after:".length()).trim());
				    	break;
				    }
				    
				    if( line.startsWith("exit:")) { 
				    	String val = line.substring("exit:".length()).trim();
				    	result.defExitCode = NumberUtils.isDigits(val) ?  NumberUtils.toInt(val) : null; 
				    	break;
				    }
  
				    if( line.startsWith("input:") ) { 
				    	result.inputPath = path.absolute(line.substring("input:".length()).trim()) ;
				    	break;
				    }
	    
				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	status = Status.TEST;
				    	
				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.exports.addAll(localExports); 
				    }
			    	break;
				    
			    case TEST:
				    
			    	/*
			    	 * parse a new test declaration 
			    	 */
			    	if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");
				    	
				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.exports.addAll(localExports); 
				    	break;
				    }

			    	/*
			    	 * parse a new assertion declaration 
			    	 */
				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");
				    	status = Status.ASSERTION;
				    	
				    	int count = assertCount != null ? assertCount : lineCount;
			    		newTest.addAssertion( line, count, result.variables );
				    	assertCount=null;
				    	localExports.clear();
				    	break;
				    }

				    
				    /*
				     * parse 'timeout declaration
				     */
				    if( line.startsWith("timeout:") ) { 
				    	if( newTest != null ) { 
				    		String duration = line.substring("timeout:".length()).trim();
				    		result.variables.resolve(duration);
				    		newTest.timeout = Duration.parse(duration);
				    	}
				    	else { 
				    		System.out.printf("~ Warning: 'timeout' should come after a 'test:' declaration. See line: %s\n", lineCount );
				    	}
				    	break;
				    }
				    
				    /*
				     * parse 'disabled' 
				     */
				    if( line.startsWith("disabled:") ) { 
				    	String val = line.substring("disabled:".length()).trim().toLowerCase();
				    	newTest.disabled = "true".equals(val) || "yes".equals(val) || "1".equals(val);
				    	break;
				    }
				    
				    if( line.startsWith("if:") ) { 
				    	String condition = line.substring("if:".length()).trim();
				    	if( StringUtils.isNotBlank(condition) ) { 
				    		newTest.condition = new TestCondition();
				    		newTest.condition .line = lineCount;
				    		newTest.condition .variables = new VarHolder(result.variables);
				    		newTest.condition .declaration = condition;
				    	}
				    } 
				    
				    /*
				     * parse 'input' definition 
				     */

				    if( line.startsWith("input:") ) { 
				    	newTest.addInputFile(line.substring("input:".length()).trim());
				    	break;
				    }
 
				    if( line.startsWith("before:") ) { 
				    	newTest.addBeforeCommand(line.substring("before:".length()).trim());
				    	break;
				    }
				    
				    if( line.startsWith("after:")) { 
				    	newTest.addAfterCommand(line.substring("after:".length()).trim());
				    	break;
				    }
				    
				    if( line.startsWith("exit:")) { 
				    	String val = line.substring("exit:".length()).trim();
				    	newTest.exit = NumberUtils.isDigits(val) ?  NumberUtils.toInt(val) : null; 
				    	break;
				    }

				    if( line.startsWith("tag:")) { 
				    	String sTags = line.substring("tag:".length()).trim();
				    	newTest.addTag(sTags);
				    	break;
				    }
 
				    
				    /*
				     * parse 'label'
				     */
				    if( line.startsWith("label:") ) { 
				    	if( newTest != null ) { 
				    		String label = line.substring("label:".length()).trim();
				    		result.variables.resolve(label);
				    		newTest.label = label;
				    	}
				    	else { 
				    		System.out.printf("~ Warning: 'label' should come after a 'test:' declaration. See line: %s\n", lineCount );
				    	}
				    	break;
				    }

				    break;
			    
				    
			    case ASSERTION: 
				    if( line.startsWith("assert:") ) { 
				    	line = line.replaceAll("^assert:\\s+", "");

				    	int count = assertCount != null ? assertCount : lineCount;
			    		newTest.addAssertion(line, count, result.variables);
				    	assertCount=null;
				    	localExports.clear();
					    break;				    	
				    }				    	

				    if( line.startsWith("test:") ) { 
				    	line = line.replaceAll("^test:\\s+", "");

				    	status = Status.TEST;

				    	newTest = newTestCase(line);
				    	newTest.line = lineCount;
				    	newTest.exports.addAll(localExports); 
				    }
				    break;
			    }
			    
			}
			
			/* 
			 * prepend the global exports and assertions
			 */
			for( TestCase test : result.tests.values() ) { 
				test.exports.addAll(0, result.globalExports);
	    		test.assertions.addAll(0, result.globalAssertions);
			}

		} 
		finally {
			IOUtils.closeQuietly(reader);
		}
		
		
		return result;
	}	
	
	
}



