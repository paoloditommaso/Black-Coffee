package org.blackcoffee.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.commons.utils.StringIterator;

public class StringWrapper implements StringExtension, Comparable<StringWrapper> {

	private String str;
	private List<String> fLines;
	private Iterator<String> fIterator;
	
	public StringWrapper( String value ) { 
		this.str = value;
	}
	
	@Override
	public String toString() { 
		return str != null ? str.toString() : "";
	}
	
	@Override
	public Boolean equals(String value) {
		return str == value || str.equals(value);
	}

	@Override
	public Boolean contains(String value) {
		return StringUtils.contains(str, value);
	}

	@Override
	public Boolean matches(String pattern) {
		if( str == null ) { 
			return false;
		}
		
		return Pattern.compile(pattern).matcher(str).find();
	}

	@Override
	public Boolean startsWith(String prefix) {
		return StringUtils.startsWith(str, prefix);
	}

	@Override
	public Boolean endsWith(String suffix) {
		return StringUtils.endsWith(str, suffix);
	}

	@Override
	public Boolean isEmpty() {
		return StringUtils.isEmpty(str);
	}

	@Override
	public Boolean isUpper() {
		for( int i=0, c=str.length(); i<c; i++ ) { 
			String ch = String.valueOf(str.charAt(i));
			if( StringUtils.isAlpha(ch.toString()) && StringUtils.isAllLowerCase(ch)) {
				return Boolean.FALSE;
			}   
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean isLower() {
		for( int i=0, c=str.length(); i<c; i++ ) { 
			String ch = String.valueOf(str.charAt(i));
			if( StringUtils.isAlpha(ch.toString()) && StringUtils.isAllUpperCase(ch)) {
				return Boolean.FALSE;
			}   
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean isAlpha() {
		return StringUtils.isAlpha(str);
	}

	@Override
	public Boolean isAlphanumeric() {
		return StringUtils.isAlphanumeric(str);
	}

	@Override
	public Boolean isNumeric() {
		return StringUtils.isNumeric(str);
	}

	@Override
	public Boolean isAscii() {
		return StringUtils.isAsciiPrintable(str);
	}

	@Override
	public Boolean isBlank() {
		return StringUtils.isBlank(str);
	}

	@Override
	public String grep(String regex) {
		StringBuilder result = new StringBuilder();
		Pattern pattern = Pattern.compile(regex);
		
		int i=0;
		Iterator<String> it = lines().iterator();
		while( it.hasNext() ) { 
			String _line = it.next();
			if( pattern.matcher(_line) .find() ) { 
				if(i++ > 0) { 
					result.append("\n");
				}
				result.append(_line) ;
			}
		}
		
		return result.toString();
	}

	@Override
	public String toLower() {
		return StringUtils.lowerCase(str);
	}

	@Override
	public String toUpper() {
		return StringUtils.upperCase(str);
	}

	@Override
	public Integer distance(String another) {
		return StringUtils.getLevenshteinDistance(str, another);
	}

	@Override
	public String difference(String another) {
		return StringUtils.difference(str, another);
	}

	
	List<String> lines() { 
		if( str == null ) { 
			return Collections.emptyList();
		}
		
		if( fLines == null ) { 
			/* a linked list will grow better than a array list */
			List<String> result = new LinkedList<String>();
			for( String line : new StringIterator(str) ) { 
				result.add(line);
			}
			
			fLines = new ArrayList<String>(result);
		}
		return fLines;
		
	}
	
	@Override
	public String line(int n) {
		List<String> ll = lines();
		int index = n-1;
		if( index < 0 ) return "";
		if( index >= ll.size() ) return "";
		return lines().get(n-1);
	}

	@Override
	public String firstLine() {
		fIterator = lines().iterator();
		return fIterator.hasNext() ? fIterator.next() : "";
	}

	@Override
	public String nextLine() {
		if( fIterator == null ) { 
			fIterator = lines().iterator();
		}
		return fIterator.hasNext() ? fIterator.next() : "";
	}
	
	@Override
	public String lastLine() {
		return line( lines().size() );
	}
	

	@Override
	public String chomp() {
		return StringUtils.chomp(str);
	}

	@Override
	public int compareTo(StringWrapper that) {
		return str.compareTo(that.str);
	}

	@Override
	public Character charAt(int index) {
		return str.charAt(index);
	}

	@Override
	public Boolean equalsIgnoreCase(String other) {
		return str.equalsIgnoreCase(other);
	}

	@Override
	public Integer indexOf(String value) {
		return str.indexOf(value);
	}

	@Override
	public Integer length() {
		return str != null ? str.length() : 0;
	}

	@Override
	public String trim() {
		return str .trim();
	}


}
