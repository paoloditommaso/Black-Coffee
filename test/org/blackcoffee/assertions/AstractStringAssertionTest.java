package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.StringWrapper;
import org.junit.Test;

public class AstractStringAssertionTest {
	
	final class TestString extends AbstractStringAssertion{

		StringWrapper str; 
		
		public TestString(String val ) {  str = new StringWrapper(val); }
		
		@Override
		protected StringWrapper content() { return str; }

		@Override
		public void initialize(AssertionContext context) {	}
		
	} 
	
	
	@Test
	public void testChomp() { 
		assertEquals( "abc", new TestString("abc\r\n") .chomp() );
	}
	
	@Test
	public void testContains() { 
		assertTrue( new TestString("aaxbb").contains("axb") );
		assertFalse( new TestString("aaxbb").contains("ayb") );
	}
	
	@Test
	public void testDifference() { 
		assertEquals( "xyz", new TestString("ab").difference("abxyz") );
	}
	
	@Test
	public void testDistance() { 
		assertEquals( 0, (int)new TestString("ab").distance("ab") );
		assertEquals( 3, (int)new TestString("ab").distance("abxyz") );
	}

	@Test
	public void testEndsWith() { 
		assertTrue( new TestString("aaxbb").endsWith("bb") );
		assertFalse( new TestString("aaxbb").endsWith("aa") );
	}


	@Test
	public void testEquals() { 
		assertTrue( new TestString("abc") .equals("abc") );
	}
	
	@Test
	public void testFirstLine() { 
		assertEquals("aa", new TestString("aa\r\n\nbb\r\nccc") .firstLine());
	}
	
	@Test
	public void testGrep() { 
		TestString str = new TestString(
				">aa\n" +
				"idsdusdsdos\n" +
				"uidsuduodfs\n" +
				"\n" +
				">bb\n" +
				"odfdfkdlfld\n" +
				"dfifdkfjdfd\n" +
				">cc\n" +
				"dsdsklfsklfdlk\n" +
				"jkfdfdklfdfldl\n" +
				"");
		
		String expect = 
			">aa\n" +
			">bb\n" +
			">cc";
		
		assertEquals( expect, str.grep("\\>.*"));
		
		assertEquals( "", str.grep("xxx") );
	}

	@Test
	public void testIsAlpha() { 
		assertTrue( new TestString("AAA").isAlpha() );
		assertFalse( new TestString("A A").isAlpha() );
		assertFalse( new TestString("A999").isAlpha() );
	}
	
	@Test
	public void testIsAlphanumeric() { 
		assertTrue( new TestString("AAA").isAlphanumeric() );
		assertFalse( new TestString("A A").isAlphanumeric() );
		assertTrue( new TestString("A999").isAlphanumeric() );
		assertTrue( new TestString("999").isAlphanumeric() );
	}

	@Test
	public void testIsAscii() { 
		assertTrue( new TestString("AAA").isAscii() );
		assertTrue( new TestString("A A").isAscii() );
		assertTrue( new TestString("A999").isAscii() );
		assertTrue( new TestString("999").isAscii() );
		assertFalse( new TestString("Ž").isAscii() );
	}

	@Test
	public void testIsBlank() { 
		assertTrue( new TestString("").isBlank() );
		assertTrue( new TestString("   ").isBlank() );
		assertFalse( new TestString(" x ").isBlank() );
	}

	@Test
	public void testIsEmpty() { 
		assertTrue( new TestString("").isEmpty() );
		assertFalse( new TestString("   ").isEmpty() );
		assertFalse( new TestString(" x ").isEmpty() );
	}

	@Test
	public void testLower() { 
		assertTrue( new TestString("aaa").isLower() );
		assertFalse( new TestString("AAA").isLower() );
		assertTrue( new TestString("a999").isLower() );
		assertTrue( new TestString("999").isLower() );
	}

	@Test
	public void testIsNumeric() { 
		assertTrue( new TestString("999").isNumeric() );
		assertFalse( new TestString("AAA").isNumeric() );
		assertFalse( new TestString("a999").isNumeric() );
		assertFalse( new TestString(" 999").isNumeric() );
	}

	@Test
	public void testIsUpper() { 
		assertFalse( new TestString("aaa").isUpper() );
		assertTrue( new TestString("AAA").isUpper() );
		assertTrue( new TestString("A999").isUpper() );
		assertFalse( new TestString("a99").isUpper() );
	}

	@Test
	public void testLine() { 
		TestString str = new TestString("aa\r\nbb\r\ncc");
		assertEquals( "aa", str.line(1) );
		assertEquals( "bb", str.line(2) );
		assertEquals( "cc", str.line(3) );
		assertEquals( "", str.line(-1) );
		assertEquals( "", str.line(0) );
		assertEquals( "", str.line(4) );
	}

	@Test
	public void testMatch() { 
		assertTrue( new TestString("aaaxxxbbbb"). matches( "a*xxxb*" )  );
		assertFalse( new TestString("aaaxxxbbbb"). matches( "a*yyb*" )  );
	}
	
	@Test
	public void testNextLine() { 
		TestString str = new TestString("aa\r\nbb\r\ncc");
		assertEquals( "aa", str.nextLine() );
		assertEquals( "bb", str.nextLine() );
		assertEquals( "cc", str.nextLine() );
		assertEquals( "", str.nextLine() );
		assertEquals( "", str.nextLine() );
	}

	@Test
	public void testLastLine() { 
		TestString str = new TestString("aa\r\nbb\r\ncc");
		assertEquals( "cc", str.lastLine() );

		assertEquals( "", new TestString("").lastLine() );

	}	
	
	@Test
	public void testStartWith() { 
		assertTrue( new TestString("aabbcc").startsWith("aab") );
		assertFalse( new TestString("aabbcc").startsWith("cc") );
	}

	@Test
	public void testToLower() { 
		assertEquals( "aa11cc", new TestString("AA11CC").toLower() );
		assertEquals( "aa11cc", new TestString("aa11cc").toLower() );
	}

	@Test
	public void testToUpper() { 
		assertEquals( "AA11C C", new TestString("aa11c c").toUpper() );
		assertEquals( " AA11C C", new TestString(" AA11C C").toUpper() );
	}	

}
