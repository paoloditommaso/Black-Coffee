package test;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestRegEx {
	final Pattern regex = Pattern.compile("^([\\w_\\.]+)=((?:.|\n)*)");

	
	@Test
	public void testMatchVariable() { 
		
		Matcher matcher = regex.matcher("something=value");
		assertTrue( matcher.matches() );
		assertEquals( "something", matcher.group(1));
		assertEquals( "value", matcher.group(2));
		
	}
	
	@Test 
	public void testDotted() { 
		
		Matcher matcher = regex.matcher("dotted.var=value");
		assertTrue( matcher.matches() );
		assertEquals( "dotted.var", matcher.group(1));
		assertEquals( "value", matcher.group(2));

		
		
	}
	
	@Test 
	public void testWithNumber() { 
	
		Matcher matcher = regex.matcher("var1=value");
		assertTrue( matcher.matches() );
		assertEquals( "var1", matcher.group(1));
		assertEquals( "value", matcher.group(2));
	
		
	}
	
	@Test
	public void testWithNewLine() { 

		Matcher matcher = regex.matcher("var1=value\nlong\nvalue");
		assertTrue( matcher.matches() );
		assertEquals( "var1", matcher.group(1));
		assertEquals( "value\nlong\nvalue", matcher.group(2));

	}
	
	@Test 
	public void testColon() { 
		
		Matcher matcher = regex.matcher("var:=value");
		assertFalse( matcher.matches() );

		
	}
}
