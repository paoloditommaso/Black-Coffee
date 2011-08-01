package org.blackcoffee.commons.utils;

import java.io.StringReader;

/**
 * Iterator over a string splitting in lines 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class StringIterator extends ReaderIterator {

	public StringIterator( String value ) { 
		super(new StringReader(value));
	}
}
