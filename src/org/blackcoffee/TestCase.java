package org.blackcoffee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.blackcoffee.parser.AssertionContext;

/**
 * Defines a test case i.e. a test rule, plus a list of assertion 
 *
 */
public class TestCase { 
	
	public String label;
	public long timeout = 60000;
	public List<KeyValue> env = new ArrayList<KeyValue>();
	public String command; 
	public List<TestAssertion> assertions = new ArrayList<TestAssertion>();
	public AssertionContext context;
	
	/** Progressive index number starting from 1 */
	public int index;
	
	/** The line count where the test is defined in the file */
	public int line;		
	
	public TestCase( String test ) { 
		this.command = test;
	}
	
	public void addAssertion( String cond, int line ) { 
		
		TestAssertion item = new TestAssertion();
		item.line = line;
	
		int p = cond.indexOf("#");
		if( p != -1 ) { 
			item. message = cond.substring(p+1) .replaceAll("^\\s*", "");
			item.declaration = cond.substring(0,p);
		}
		else { 
			item.declaration = cond;
		}
		
		assertions.add(item);
	}
	
	public String toString() { 
		return new StringBuilder() 
			.append("TestCase[ \n") 
			.append("env: " ) .append( env ) . append("\n")
			.append("test: " ) .append( command ) .append("\n")
			.append("assert: ") .append( assertions ) .append("\n")
			.append("]") 
			.toString();
	}

	/**
	 * Compile the assertions predicates in this test case 
	 */
	public void compile() {
		Class<?> lastResultType = null;
		for( TestAssertion assertion : assertions ) { 
			assertion.compile( lastResultType );
			lastResultType  = assertion.predicate.lastResultType;
		}
	}

	
	/**
	 * Verify that all assertions are satisfied 
	 * 
	 * @param path directory that define the execution context for this test i.e. usually the directory 
	 * that contains all the files required/produced by the command under tests 
	 */
	public void verify(File path) {
		context = new AssertionContext(path,env);
		
		for( TestAssertion assertion : assertions ) { 
			
			/* 
			 * verify the assertion 
			 */
			Object value = assertion.verify(context);
			
			/* 
			 * save the assertion result value in the context, 
			 * so that can be reused if required 
			 */
			if( !assertion.predicate.root.isContinuation() ) { 
				context .previousInstance = value;
			}
		}
	}


}




