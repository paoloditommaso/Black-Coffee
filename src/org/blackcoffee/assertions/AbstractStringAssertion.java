package org.blackcoffee.assertions;

import org.blackcoffee.parser.StringExtension;
import org.blackcoffee.parser.StringWrapper;


/**
 * Base class for string based assertions 
 * 
 * @author Paolo Di Tommaso
 *
 */
public abstract class AbstractStringAssertion extends AbstractAssertion  implements StringExtension {

	abstract protected StringWrapper content();

	public String toString() {
		return content().toString();
	} 

	@Override
	public Boolean isEmpty() {
		return content().isEmpty();
	}

	@Override
	public Boolean isUpper() {
		return content().isUpper();
	}

	@Override
	public Boolean isLower() {
		return content().isLower();
	}

	@Override
	public Boolean isAlpha() {
		return content().isAlpha();
	}

	@Override
	public Boolean isAlphanumeric() {
		return content().isAlphanumeric();
	}

	@Override
	public Boolean isNumeric() {
		return content().isNumeric();
	}

	@Override
	public Boolean isAscii() {
		return content().isAscii();
	}

	@Override
	public Boolean isBlank() {
		return content().isBlank();
	}

	@Override
	public String grep(String value) {
		return content().grep(value);
	}

	@Override
	public String toLower() {
		return content().toLower();
	}

	@Override
	public String toUpper() {
		return content().toUpper();
	}

	@Override
	public String chomp() {
		return content().chomp();
	}

	@Override
	public Integer distance(String another) {
		return content().distance(another);
	}

	@Override
	public String difference(String another) {
		return content().difference(another);
	}

	@Override
	public String line(int n) {
		return content().line(n);
	}

	@Override
	public String firstLine() {
		return content().firstLine();
	}

	@Override
	public String nextLine() {
		return content().nextLine();
	}

	@Override
	public Character charAt(int pos) {
		return content().charAt(pos);
	}

	@Override
	public Boolean equalsIgnoreCase(String that) {
		return content().equalsIgnoreCase(that);

	}

	@Override
	public Integer indexOf(String value) {
		return content().indexOf(value);
	}

	@Override
	public Integer length() {
		return content().length();
	}

	@Override
	public String trim() {
		return content().trim();
	}

	@Override
	public String lastLine() {
		return content().lastLine();
	} 	
	/**
	 * Verify that the file contains the specified string 
	 * 
	 * @param value the string that the file have to contains 
	 */
	public Boolean contains( String value ) { 
		return content().contains(value);
	}

	/**
	 * Verify that a file content is equals to another one 
	 * 
	 * @param target the other file which content must match the current under test
	 * 
	 */
	public Boolean equals( String target ) { 
		return content().equals(target);
	}
	
	/**
	 * Verify that the file content start with the specified value 
	 * 
	 */
	public Boolean startsWith( String value ) { 
		return content().startsWith(value);
	}

	/**
	 * Vefify that the file content ends with the specified string 
	 */
	public Boolean endsWith( String value ) { 
		return content().endsWith(value);
	}
	
	/**
	 * Verify that the file content matches the specified regular expression 
	 * 
	 */
	public Boolean matches( String pattern ) { 
		return content().matches(pattern);
	}
	
	
}
