package org.blackcoffee.utils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;

/**
 * Hold a list of KeyValue entries 
 * 
 * @author Paolo Di Tommaso
 *
 */
@SuppressWarnings("serial")
public class VarHolder extends TreeMap<String,String> {

	/** Create an empty var holder */
	public VarHolder() { } 
	
	/**
	 * Create a key-value holder starting a string formatted with the followinh syntax
	 * <pre>
	 *   key=value, key-value, ... 
	 * </pre>
	 * @param str
	 */
	public VarHolder(String pairs) { 
		String[] _pairs = pairs.split(",");
		for( String item : _pairs ) { 
			if( StringUtils.isNotBlank(item)  ) {
				KeyValue kv = KeyValue.parse( item.trim() );
				this. put( kv.key, kv.value );
			}
		}
		
	}
	
	/**
	 * Create a var holder coping the content of the other list 
	 *  
	 * @param other 
	 */
	public VarHolder( List<KeyValue> other ) { 
		if( other != null ) for( KeyValue item : other ) { 
			this. put(item.key, item.value);
		}
	}
	
	/**
	 * Create a var holder coping the content of the other list
	 * 
	 * @param that
	 */
	public VarHolder( Map<String,String> that ) { 
		super(that);
	}
	
	public String value(String key) {
		String val = get(key) ;
		return val != null ? substitutor.replace(val) : null;
	}

	

	StrSubstitutor substitutor = new StrSubstitutor( new StrLookup() {
		@Override
		public String lookup(String key) {
			String defValue = null;
			
			/* check if is defined an 'elvis' operator */
			int p=key.indexOf("?:");
			if( p != -1 ) { 
				defValue = key.substring(p+2).trim();
				key = key.substring(0,p).trim();
			}
			
			// get the value for 'key' in the variable holder 
			String value = get(key);
			// fallback on environment variable 
			if( value == null ) { 
				value = System.getenv(key);
			}
			// fallback to java property 
			if( value == null ) { 
				value = System.getProperty(key);
			}
			
			if( value == null ) { 
				System.err.printf("Unknown value for variable '%s'\n",key);
			}
			return value != null ? value : defValue;
		} });

	
	public String resolve( String str ) {
		return substitutor.replace( str.toString() );
	}
	
}
