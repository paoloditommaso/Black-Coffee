package org.blackcoffee.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class AssertionTokenizerTest {

	@Test
	public void testSimple() { 
		AssertionTokenizer tkns = new AssertionTokenizer("a");
		assertEquals("a", tkns.next());
	}
	
	@Test
	public void testSimple2() { 
		AssertionTokenizer tkns = new AssertionTokenizer("a b c");
		assertEquals("a", tkns.next());
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
	}

	@Test
	public void testSimple3() { 
		AssertionTokenizer tkns = new AssertionTokenizer("  a b c ");
		assertEquals("a", tkns.next());
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
	}

	@Test
	public void testQuote() { 
		AssertionTokenizer tkns = new AssertionTokenizer("a 'b c' z");
		assertEquals("a", tkns.next());
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}

	@Test
	public void testQuote2() { 
		AssertionTokenizer tkns = new AssertionTokenizer("a \"b c\" z");
		assertEquals("a", tkns.next());
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}


	@Test
	public void testQuote3() { 
		AssertionTokenizer tkns = new AssertionTokenizer("'b c' z");
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}

	@Test
	public void testQuote4() { 
		AssertionTokenizer tkns = new AssertionTokenizer("b c' z'");
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
		assertEquals(" z", tkns.next());
	}

}
