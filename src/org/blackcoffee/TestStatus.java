package org.blackcoffee;

public enum TestStatus {

	PASSED, 	// all assertion passed with success
	FAILED, 	// at least one assertion failed
	ERROR,		// general error execution tests 
	TIMEOUT		// the test cannot complete
	
}

