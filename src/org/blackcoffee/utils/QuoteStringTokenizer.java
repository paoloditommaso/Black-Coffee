package org.blackcoffee.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Slipt a string into leteral tokens i.e. sequence of chars that does not contain a blank 
 * or wrapper by a quote or double quote
 * 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class QuoteStringTokenizer implements Iterator<String>, Iterable<String> {

	private List<Character> chars = Arrays.asList(' ');
	
	private List<String> tokens = new ArrayList<String>(); 
	
	private Iterator<String> itr;
	
	public QuoteStringTokenizer( String value ) { 
		this(value, ' ');
	}

	public QuoteStringTokenizer( String value, char... separators ) {
		
		if( separators == null || separators.length==0 ) {
			chars = new ArrayList<Character>(3);
			chars .add(' ');
		}
		else { 
			chars = new ArrayList<Character>(3);
			for( char ch : separators ) { chars.add(ch); }
		}
		
		chars.add('"');
		chars.add('\'');

		/* start parsing */
		parseNext(value != null ? value.trim() : "");

	}
	
	void parseNext( String value )  { 
		
		for( int i=0; i<value.length(); i++  ) { 
			
			char ch=value.charAt(i);
			if( chars.contains(ch) ) { 
				if( i>0 ) { 
					tokens.add(value.substring(0,i));
				}
				
				if( ch=='"' || ch=='\'' ) { 
					parseQuote(value.substring(i+1), ch);
				}
				else if( chars.contains(ch) ) { 
					parseNext(value.substring(i+1));
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
