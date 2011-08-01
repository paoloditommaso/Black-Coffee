package org.blackcoffee;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyValueTest {

	@Test 
	public void testCreate() { 
		KeyValue pair = new KeyValue( "k", "v");
		assertEquals( "k", pair.key );
		assertEquals( "v", pair.value );
	}
	
	@Test 
	public void testParse() { 
		KeyValue pair = KeyValue.parse("k=1");
		assertEquals( "k", pair.key );
		assertEquals( "1", pair.value );
		
		pair = KeyValue.parse("k=9999=9999");
		assertEquals( "k", pair.key );
		assertEquals( "9999=9999", pair.value );
		
		pair = KeyValue.parse("");
		assertNull( pair );

		pair = KeyValue.parse("=1");
		assertNull( pair );

		
		pair = KeyValue.parse("k=");
		assertEquals( "k", pair.key );
		assertNull( pair.value );
	
	}
	
	@Test
	public void testBlanks() { 
		KeyValue pair = KeyValue.parse(" k  =  1 ");
		assertEquals( "k", pair.key );
		assertEquals( "1", pair.value );

	}
}
