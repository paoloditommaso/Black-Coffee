package org.blackcoffee.assertions;

import static org.junit.Assert.*;

import org.junit.Test;


public class NumberAssertionTest {

	@Test
	public void testCompareTo() { 
		NumberAssertion num = new NumberAssertion("10");
		
		assertTrue( num.compareTo( new NumberAssertion("10") ) == 0 );

		assertTrue( num.compareTo( new NumberAssertion("20") ) < 0 );
		assertFalse( num.compareTo( new NumberAssertion("20") ) >= 0 );

		assertTrue( num.compareTo( new NumberAssertion("0") ) > 0 );
		assertFalse( num.compareTo( new NumberAssertion("0") ) <= 0 );
		
	}
	
//	@Test
//	public void testEquals( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.eq("10") );
//		
//		try { 
//			assertEquals( _10, _10.eq("11") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//
//	
//	@Test
//	public void testNotEquals( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.ne("99") );
//		
//		try { 
//			assertEquals( _10, _10.ne("10") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//	
//	@Test
//	public void testGreaterThan( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.gt("9") );
//		
//		try { 
//			assertEquals( _10, _10.gt("11") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//
//	
//	@Test
//	public void testGreaterThanEquals( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.gte("9") );
//		assertEquals( _10, _10.gte("10") );
//		
//		try { 
//			assertEquals( _10, _10.gte("11") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//	
//	@Test
//	public void testLessThan( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.lt("20") );
//		
//		try { 
//			assertEquals( _10, _10.lt("0") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//
//	
//	@Test
//	public void testLessThanEquals( ) { 
//		NumberAssertion _10 = new NumberAssertion("10");
//		assertEquals( _10, _10.lte("10") );
//		assertEquals( _10, _10.lte("11") );
//		
//		try { 
//			assertEquals( _10, _10.lte("9") );
//			fail();
//		}
//		catch( AssertionFailed e ) { 
//			// OK
//		}
//	}
//		
	
	
}
