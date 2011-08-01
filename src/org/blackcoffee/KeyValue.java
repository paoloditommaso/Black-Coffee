package org.blackcoffee;

import org.apache.commons.lang.StringUtils;

/**
 * A simple holder for a key-value pair
 * 
 * @author Paolo Di Tommaso
 *
 */
public class KeyValue { 
	String key;
	String value;
	
	public KeyValue( String key, String value ) { 
		this.key = key;
		this.value = value;
	}
	
	public String toString() {
		return key + "=" + value;
	} 
	
	public static KeyValue parse ( String pair ) { 
		if( pair == null ) { return null; }
		int p = pair.indexOf("=");
		if( p == -1 ) { return null; }
		
		KeyValue result = new KeyValue( 
					pair.substring(0,p).trim(),
					pair.substring(p+1).trim()
					);
		
		if( StringUtils.isEmpty(result.key)) { return null; } 
		if( StringUtils.isEmpty(result.value) ) { result.value = null; }
		
		return result;
	}
}