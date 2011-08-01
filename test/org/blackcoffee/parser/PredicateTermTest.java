package org.blackcoffee.parser;

import static org.junit.Assert.*;

import org.junit.Test;


public class PredicateTermTest {

	@Test
	public void testOpString() { 
		assertEquals( "=", CompareOperator.EQ.toString() );
		assertEquals( "!=", CompareOperator.NE.toString() );
		assertEquals( "<", CompareOperator.LT.toString() );
		assertEquals( "<=", CompareOperator.LTE.toString() );
		assertEquals( ">", CompareOperator.GT.toString() );
		assertEquals( ">=", CompareOperator.GTE.toString() );
		
		
		assertEquals( CompareOperator.EQ, CompareOperator.fromString("=") );
		assertEquals( CompareOperator.NE, CompareOperator.fromString("!=") );
		assertEquals( CompareOperator.LT, CompareOperator.fromString("<") );
		assertEquals( CompareOperator.LTE, CompareOperator.fromString("<=") );
		assertEquals( CompareOperator.GT, CompareOperator.fromString(">") );
		assertEquals( CompareOperator.GTE, CompareOperator.fromString(">=") );
		assertEquals( null, CompareOperator.fromString("xxx") );
	}
	
	@Test 
	public void testOp() { 
		assertTrue( CompareOperator.EQ.eval(0) );
		assertFalse( CompareOperator.EQ.eval(1) );

		assertTrue( CompareOperator.NE.eval(1) );
		assertFalse( CompareOperator.NE.eval(0) );

		assertFalse( CompareOperator.GT.eval(0) );
		assertTrue( CompareOperator.GT.eval(1) );
		assertFalse( CompareOperator.GT.eval(-1) );

		assertTrue( CompareOperator.GTE.eval(0) );
		assertTrue( CompareOperator.GTE.eval(1) );
		assertFalse( CompareOperator.GTE.eval(-1) );
	
		assertFalse( CompareOperator.LT.eval(0) );
		assertFalse( CompareOperator.LT.eval(1) );
		assertTrue( CompareOperator.LT.eval(-1) );

		assertTrue( CompareOperator.LTE.eval(0) );
		assertFalse( CompareOperator.LTE.eval(1) );
		assertTrue( CompareOperator.LTE.eval(-1) );
		
	
	}
}
