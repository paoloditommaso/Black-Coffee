package org.blackcoffee.parser;

public interface StringExtension {

	Boolean equals( String value );
	Boolean contains( String value );
	Boolean matches( String pattern );
	Boolean startsWith( String value );
	Boolean endsWith( String value );
	Boolean isEmpty();
	Boolean isUpper();
	Boolean isLower();
	Boolean isAlpha();
	Boolean isAlphanumeric();
	Boolean isNumeric();
	Boolean isAscii();
	Boolean isBlank();
	
	String grep( String value );
	
	
	String toLower();
	String toUpper();
	
	String chomp();
	
	/** 
	 * Calculate the Levenstein distance i.e' the number of changes needed to change one String into another
	 */
	Integer distance( String another );
	

	/**
	 * Compares two Strings, and returns the portion where they differ. (More precisely, return the remainder of the second String, starting from where it's different from the first.)
	 * 
	 * For example, difference("i am a machine", "i am a robot") -> "robot".
	 *
 	 * difference(null, null) = null
 	 * difference("", "") = ""
 	 * difference("", "abc") = "abc"
 	 * difference("abc", "") = ""
 	 * difference("abc", "abc") = ""
 	 * difference("ab", "abxyz") = "xyz"
 	 * difference("abcde", "abxyz") = "xyz"
 	 * difference("abcde", "xyz") = "xyz"
	 */
	String difference( String another );
	
	String line( int n );
	
	String firstLine();
	
	String nextLine();
	
	String lastLine();
	
	// slipt and slipt iterator ?? 
	
	Character charAt( int pos );
	
	Boolean equalsIgnoreCase( String that );
	
	Integer indexOf( String value );
	
	Integer length();
	
	String trim();
}
