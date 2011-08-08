package org.blackcoffee.parser;

import static org.junit.Assert.*;

import org.blackcoffee.assertions.FileAssertion;
import org.junit.Test;

public class PredicateTest {

	
	@Test
	public void testFileExists() throws SecurityException, NoSuchMethodException { 
		Predicate rule = new Predicate("file ./test/sample.txt exists") .parse();
		
		assertEquals( FileAssertion.class, rule.root.clazz );
		assertEquals( FileAssertion.class.getDeclaredConstructor(String.class), rule.root.constructor );
		assertEquals( "./test/sample.txt", rule.root.args[0] );
		assertEquals( FileAssertion.class.getMethod("exists"), rule.chain.get(0).method );

	}


	@Test
	public void testFileContains() throws SecurityException, NoSuchMethodException { 
		Predicate parser = new Predicate("file ./test/sample.txt contains 'Hello World!'");
		parser.parse();
		
		assertEquals( FileAssertion.class, parser.root.clazz );
		assertEquals( FileAssertion.class.getDeclaredConstructor(String.class), parser.root.constructor );
		assertEquals( FileAssertion.class.getMethod("contains", String.class), parser.chain.get(0).method );
		assertEquals( "Hello World!", parser.chain.get(0).args[0] );

	}
	

	
	@Test 
	public void testComposed() { 

		Predicate parser = new Predicate("file ./test/sample.txt size > 0 ");
		Predicate rule = parser.parse();
		
		assertEquals( FileAssertion.class, parser.root.clazz );
		assertEquals( 2, rule.chain.size() );
		assertEquals( "size", rule.chain.get(0).method.getName() );
		assertEquals( "compareTo", rule.chain.get(1).method.getName() );
		
		Object result = rule.invoke( new AssertionContext(".") );
		assertTrue( (Boolean)result );
		
	}

	

	@Test
	public void testWrap() throws Exception { 
		
		assertEquals( null, Predicate.wrap(String.class, null));
		assertEquals( "Hola", Predicate.wrap(String.class, "Hola"));
		assertEquals( new Long(1), Predicate.wrap(Long.class, "1"));
		assertEquals( new Integer(9), Predicate.wrap(Integer.class, "9"));
		assertEquals( Boolean.FALSE, Predicate.wrap(Boolean.class, "false"));
		assertEquals( Boolean.TRUE, Predicate.wrap(Boolean.class, "true"));
		
	
	}
	

	
	
}
