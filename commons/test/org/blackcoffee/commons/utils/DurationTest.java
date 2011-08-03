package org.blackcoffee.commons.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class DurationTest {

	@Test
	public void testParseMillis() { 
		
		assertEquals( 1002, Duration.parse("1002").millis() );
		assertEquals( 1001, Duration.parse("1001ms").millis() );

	}
	
	@Test
	public void testParseSecs() { 
		assertEquals( 2, Duration.parse("2s").secs() );
		assertEquals( 3, Duration.parse("3s").secs() );
	}
	
	@Test
	public void testParseMins() { 
		assertEquals( 2, Duration.parse("2mn").mins() );
		assertEquals( 3, Duration.parse("3min").mins() );
	}

	@Test
	public void testParseHours() { 
		assertEquals( 2, Duration.parse("2h").hours() );
		assertEquals( 3, Duration.parse("3h").hours() );
	}	

	@Test
	public void testParsedays() { 
		assertEquals( 2, Duration.parse("2d").days() );
		assertEquals( 3, Duration.parse("3d").days() );
	}	
}
