package org.blackcoffee;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.blackcoffee.exception.AssertionFailed;

public class TestResult {

	/** The test final status */
	public TestStatus status;
	
	/** The elapsed time (millis) to run the test (not including assertions)*/
	public long elapsed;
	
	/** The test command exit code */
	public int exitCode;
	
	public AssertionFailed failure;
	
	/** The reported exception */
	public Throwable cause;
	
	public StringWriter messages = new StringWriter();
	
	public PrintWriter text = new PrintWriter(messages);
	
	/** back reference to the owner test case */
	public TestCase test;
	
	public File path() { 
		return test != null ? test.runPath : null;
	}
	
	public String toString() { 
		return new StringBuilder() 
			.append("TestResult[")
			.append("  status: ") .append(status) .append(",\n")
			.append("  elapsed: ") .append(elapsed) .append(",\n")
			.append("  exit: ") .append(exitCode) .append(",\n")
			.append("  failure: ") .append(failure) .append("\n] ") 
			.toString();
	}
}
