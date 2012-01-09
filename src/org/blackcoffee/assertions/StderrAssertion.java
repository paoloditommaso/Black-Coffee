package org.blackcoffee.assertions;

/**
 * Wraps program standard error saved by the framwork in the hidden file '.stderr'
 * 
 * @author Paolo Di Tommaso
 *
 */
public class StderrAssertion extends FileAssertion {

	public StderrAssertion() {
		super(".stderr");
	}

}
