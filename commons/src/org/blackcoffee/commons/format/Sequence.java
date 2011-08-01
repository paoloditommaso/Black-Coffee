package org.blackcoffee.commons.format;

import java.io.Serializable;

/**
 * A generic structure to hold sequences
 * 
 * @author Paolo Di Tommaso
 *
 */
@SuppressWarnings("serial")
public class Sequence implements Serializable {

	public String header;
	
	public String value;

	@Override
	public String toString() {
		return String.format(">%s|%s", header, value);
	}
	 
	
}
