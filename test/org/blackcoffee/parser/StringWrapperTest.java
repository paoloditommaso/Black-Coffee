package org.blackcoffee.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringWrapperTest {

	@Test
	public void testChomp() { 
		assertEquals( "abc", new StringWrapper("abc\r\n") .chomp() );
	}
	
	@Test
	public void testContains() { 
		assertTrue( new StringWrapper("aaxbb").contains("axb") );
		assertFalse( new StringWrapper("aaxbb").contains("ayb") );
	}
	
	@Test
	public void testDifference() { 
		assertEquals( "xyz", new StringWrapper("ab").difference("abxyz") );
	}
	
	@Test
	public void testDistance() { 
		assertEquals( 0, (int)new StringWrapper("ab").distance("ab") );
		assertEquals( 3, (int)new StringWrapper("ab").distance("abxyz") );
	}

	@Test
	public void testEndsWith() { 
		assertTrue( new StringWrapper("aaxbb").endsWith("bb") );
		assertFalse( new StringWrapper("aaxbb").endsWith("aa") );
	}


	@Test
	public void testEquals() { 
		assertTrue( new StringWrapper("abc") .equals("abc") );
	}
	
	@Test
	public void testFirstLine() { 
		assertEquals("aa", new StringWrapper("aa\r\n\nbb\r\nccc") .firstLine());
	}
	
	@Test
	public void testGrep() { 
		StringWrapper str = new StringWrapper(
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
		assertTrue( new StringWrapper("AAA").isAlpha() );
		assertFalse( new StringWrapper("A A").isAlpha() );
		assertFalse( new StringWrapper("A999").isAlpha() );
	}
	
	@Test
	public void testIsAlphanumeric() { 
		assertTrue( new StringWrapper("AAA").isAlphanumeric() );
		assertFalse( new StringWrapper("A A").isAlphanumeric() );
		assertTrue( new StringWrapper("A999").isAlphanumeric() );
		assertTrue( new StringWrapper("999").isAlphanumeric() );
	}

	@Test
	public void testIsAscii() { 
		assertTrue( new StringWrapper("AAA").isAscii() );
		assertTrue( new StringWrapper("A A").isAscii() );
		assertTrue( new StringWrapper("A999").isAscii() );
		assertTrue( new StringWrapper("999").isAscii() );
		assertFalse( new StringWrapper("Ž").isAscii() );
	}

	@Test
	public void testIsBlank() { 
		assertTrue( new StringWrapper("").isBlank() );
		assertTrue( new StringWrapper("   ").isBlank() );
		assertFalse( new StringWrapper(" x ").isBlank() );
	}

	@Test
	public void testIsEmpty() { 
		assertTrue( new StringWrapper("").isEmpty() );
		assertFalse( new StringWrapper("   ").isEmpty() );
		assertFalse( new StringWrapper(" x ").isEmpty() );
	}

	@Test
	public void testLower() { 
		assertTrue( new StringWrapper("aaa").isLower() );
		assertFalse( new StringWrapper("AAA").isLower() );
		assertTrue( new StringWrapper("a999").isLower() );
		assertTrue( new StringWrapper("999").isLower() );
	}

	@Test
	public void testIsNumeric() { 
		assertTrue( new StringWrapper("999").isNumeric() );
		assertFalse( new StringWrapper("AAA").isNumeric() );
		assertFalse( new StringWrapper("a999").isNumeric() );
		assertFalse( new StringWrapper(" 999").isNumeric() );
	}

	@Test
	public void testIsUpper() { 
		assertFalse( new StringWrapper("aaa").isUpper() );
		assertTrue( new StringWrapper("AAA").isUpper() );
		assertTrue( new StringWrapper("A999").isUpper() );
		assertFalse( new StringWrapper("a99").isUpper() );
	}

	@Test
	public void testLine() { 
		StringWrapper str = new StringWrapper("aa\r\nbb\r\ncc");
		assertEquals( "aa", str.line(1) );
		assertEquals( "bb", str.line(2) );
		assertEquals( "cc", str.line(3) );
		assertEquals( "", str.line(-1) );
		assertEquals( "", str.line(0) );
		assertEquals( "", str.line(4) );
	}

	@Test
	public void testMatch() { 
		assertTrue( new StringWrapper("aaaxxxbbbb"). matches( "a*xxxb*" )  );
		assertFalse( new StringWrapper("aaaxxxbbbb"). matches( "a*yyb*" )  );
	}
	
	@Test
	public void testNextLine() { 
		StringWrapper str = new StringWrapper("aa\r\nbb\r\ncc");
		assertEquals( "aa", str.nextLine() );
		assertEquals( "bb", str.nextLine() );
		assertEquals( "cc", str.nextLine() );
		assertEquals( "", str.nextLine() );
		assertEquals( "", str.nextLine() );
	}

	@Test
	public void testLastLine() { 
		StringWrapper str = new StringWrapper("aa\r\nbb\r\ncc");
		assertEquals( "cc", str.lastLine() );

		assertEquals( "", new StringWrapper("").lastLine() );

	}	
	
	@Test
	public void testStartWith() { 
		assertTrue( new StringWrapper("aabbcc").startsWith("aab") );
		assertFalse( new StringWrapper("aabbcc").startsWith("cc") );
	}

	@Test
	public void testToLower() { 
		assertEquals( "aa11cc", new StringWrapper("AA11CC").toLower() );
		assertEquals( "aa11cc", new StringWrapper("aa11cc").toLower() );
	}

	@Test
	public void testToUpper() { 
		assertEquals( "AA11C C", new StringWrapper("aa11c c").toUpper() );
		assertEquals( " AA11C C", new StringWrapper(" AA11C C").toUpper() );
	}

}
