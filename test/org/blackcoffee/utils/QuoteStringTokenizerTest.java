package org.blackcoffee.utils;

import static org.junit.Assert.*;

import org.blackcoffee.utils.QuoteStringTokenizer;
import org.junit.Test;

public class QuoteStringTokenizerTest {

	@Test
	public void testSimple() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("a");
		assertEquals("a", tkns.next());
	}
	
	@Test
	public void testSimple2() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("a b c");
		assertEquals("a", tkns.next());
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
	}

	@Test
	public void testSimple3() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("  a b c ");
		assertEquals("a", tkns.next());
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
	}

	@Test
	public void testQuote() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("a 'b c' z");
		assertEquals("a", tkns.next());
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}

	@Test
	public void testQuote2() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("a \"b c\" z");
		assertEquals("a", tkns.next());
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}


	@Test
	public void testQuote3() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("'b c' z");
		assertEquals("b c", tkns.next());
		assertEquals("z", tkns.next());
	}

	@Test
	public void testQuote4() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("b c' z'");
		assertEquals("b", tkns.next());
		assertEquals("c", tkns.next());
		assertEquals(" z", tkns.next());
	}

	@Test
	public void testOtherDelimiter() { 
		QuoteStringTokenizer tkns = new QuoteStringTokenizer("a; b, 'c d'", ';',',', ' ');
		
		assertEquals("a", tkns.next());
		assertEquals("b", tkns.next());
		assertEquals("c d", tkns.next());
		
	}
}
