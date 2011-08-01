package org.blackcoffee.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AssertionTokenizer implements Iterator<String>, Iterable<String> {

	private List<String> tokens = new ArrayList<String>(); 
	
	private Iterator<String> itr;
	
	public AssertionTokenizer( String value ) { 
		parseNext(value != null ? value.trim() : "");
	}

	void parseNext( String value )  { 
		List<Character> chars = Arrays.asList(' ', '"', '\'', ';');
		
		for( int i=0; i<value.length(); i++  ) { 
			
			char ch=value.charAt(i);
			if( chars.contains(ch) ) { 
				if( i>0 ) { 
					tokens.add(value.substring(0,i));
				}
				
				if( ch==' ') {
					parseNext(value.substring(i+1));
				}
				else if( ch=='"' || ch=='\'' ) { 
					parseQuote(value.substring(i+1), ch);
				}
				break;
			}
			// and of the string
			else if( i+1 == value.length() ) { 
				tokens.add(value);
			}
		}
	}
	
	void parseQuote( String value, char delim ) { 
		int p = value.indexOf(delim);
		if( p == -1 ) { 
			tokens.add(value);
			return;
		}
		
		tokens.add( value.substring(0,p) );
		parseNext(value.substring(p+1));
	}
	
	@Override
	public boolean hasNext() {
		return itr().hasNext();
	}

	@Override
	public String next() {
		return itr().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported");
	}

	@Override
	public String toString() { 
		return tokens.toString();
	}

	@Override
	public Iterator<String> iterator() {
		return itr();
	}

	/*
	 * lazy iterator creator
	 */
	private Iterator<String> itr() { 
		if( itr == null ) {
			itr = tokens.iterator();
		}
		return itr;
	}
	
	
}
