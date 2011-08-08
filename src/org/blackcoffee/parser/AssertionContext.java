package org.blackcoffee.parser;

import java.io.File;
import java.util.Map;

import org.blackcoffee.utils.VarHolder;

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
	public VarHolder variables;

	public Object previousAssertResult;

	public AssertionContext( String path ) { 
		this(new File(path));
	}
	
	public AssertionContext( File path )  { 
		this.path = path;
		this.variables = new VarHolder();
	}
	
	public AssertionContext( File path, Map<String,String> variables ) { 
		this.path = path;
		this.variables = new VarHolder(variables);
	}
	
	
	
	public String toString() { 
		return new StringBuilder()
			.append("AssertionContext[\n")
			.append("  path: '") .append(path) .append("',\n")
			.append("  vars: ") .append(variables) .append("\n]")
			.toString();
	}

	
}


