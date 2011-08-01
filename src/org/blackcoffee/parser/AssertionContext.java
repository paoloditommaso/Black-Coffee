package org.blackcoffee.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.blackcoffee.KeyValue;

/**
 * Defines the execution context for any assertion 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class AssertionContext {

	/** The current directory path where the test is executed */
	public File path;
	
	/** The variable defined for the test */
	public List<KeyValue> env;

	public Object previousInstance;

	public AssertionContext( String path ) { 
		this(new File(path));
	}
	
	public AssertionContext( File path )  { 
		this.path = path;
		this.env = new ArrayList<KeyValue>();
	}
	
	public AssertionContext( File path, List<KeyValue> items ) { 
		this.path = path;
		this.env = new ArrayList<KeyValue>(items);
	}
}


