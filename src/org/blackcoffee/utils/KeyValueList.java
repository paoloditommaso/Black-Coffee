package org.blackcoffee.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.blackcoffee.parser.StringWrapper;

/**
 * Hold a list of KeyValue entries 
 * 
 * @author Paolo Di Tommaso
 *
 */
@SuppressWarnings("serial")
public class KeyValueList extends ArrayList<KeyValue> {

	/** Create an empty var holder */
	public KeyValueList() { } 
	
	/**
	 * Create a key-value holder starting a string formatted with the followinh syntax
	 * <pre>
	 *   key=value, key-value, ... 
	 * </pre>
	 * @param str
	 */
	public KeyValueList(String pairs) { 
		String[] _pairs = pairs.split(",");
		for( String item : _pairs ) { 
			if( StringUtils.isNotBlank(item)  ) 
				this. add( KeyValue.parse( item.trim() ));
		}
		
	}
	
	/**
	 * Create a var holder coping the content of the other list 
	 *  
	 * @param other 
	 */
	public KeyValueList( List<KeyValue> other ) { 
		if( other != null ) for( KeyValue item : other ) { 
			this. add(item);
		}
	}
	
	/**
	 * Create a var holder coping the content of the other list
	 * 
	 * @param that
	 */
	public KeyValueList( KeyValueList that ) { 
		if( that != null ) for( KeyValue item : that ) { 
			this. add(item);
		}
		
	}
	
	/**
	 * Create a var holder copying the content of a map 
	 * 
	 * @param map
	 */
	public KeyValueList( Map<String,String> map ) { 
		if( map != null ) for( Map.Entry<String, String> item : map.entrySet() ) { 
			this. add( new KeyValue(item.getKey(), item.getValue()));
		}
	}

	/**
	 * The position in the list of an entry with the specified 'key'
	 */
	public int indexOf( String key ) { 

		for( int i=0; i<size(); i++ ) { 
			if( get(i).key .equals(key) ) { 
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Verifyi if a entry with the key specified exists 
	 */
	public boolean contains( String key ) { 
		return indexOf(key) != -1;
	}

	/**
	 * Key the entry in the holder by its key 
	 * 
	 * @param key
	 * @return
	 */
	public KeyValue get(String key) {
		int p = indexOf(key);
		return p != -1 ? get(p) : null;
	}
	
	public String value(String key) {
		int p = indexOf(key);
		String val = p!=-1 ? get(p).value : null;
		return val != null ? substitutor.replace(val) : null;
	}

	
	/**
	 * Convert the holder to a map object 
	 * 
	 */
	public Map<String,String> toMap() { 
		Map<String,String> result = new TreeMap<String, String>( );
		for( KeyValue item : this ) { 
			result.put( item.key, item.value );
		}
		
		return result;
	}
	

	StrSubstitutor substitutor = new StrSubstitutor( new StrLookup() {
		@Override
		public String lookup(String key) {
			KeyValue result;
			return (result=get(key)) != null ? result.value : null ;
		} });
	
	
	
	public Object[] resolve(Object ... args) { 
		
		Object[] result = new Object[args != null ? args.length : 0];
		int i=0;
		if( args != null ) for( Object obj: args ) { 
			result[i++] = 
				(obj instanceof String || obj instanceof StringWrapper)
				? resolve(obj.toString())
				: obj;
		}
		
		return result;
		
	}
	
	public String resolve( String str ) {
		return substitutor.replace( str.toString() );
	}
	
}
