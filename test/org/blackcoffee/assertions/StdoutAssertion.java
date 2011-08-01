package org.blackcoffee.assertions;

/**
 * Just a wrapper to handle program standard output saved by the framwork in the 
 * hidden file '.stdout'
 * 
 * @author Paolo Di Tommaso
 *
 */
public class StdoutAssertion extends FileAssertion {

	public StdoutAssertion() {
		super(".stdout");
	}

}
