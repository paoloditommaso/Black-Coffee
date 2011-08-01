package org.blackcoffee;

import java.io.File;

import org.blackcoffee.assertions.AssertionFailed;

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
	
	/** The path the the test has run */
	public File path;
}
