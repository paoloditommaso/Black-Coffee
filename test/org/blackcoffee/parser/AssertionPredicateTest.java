package org.blackcoffee.parser;

import static org.junit.Assert.*;

import org.blackcoffee.assertions.FileAssertion;
import org.junit.Test;

public class AssertionPredicateTest {

	
	@Test
	public void testFileExists() throws SecurityException, NoSuchMethodException { 
		AssertionPredicate rule = new AssertionPredicate("file ./test/sample.txt exists") .parse();
		
		assertEquals( FileAssertion.class, rule.root.clazz );
		assertEquals( FileAssertion.class.getDeclaredConstructor(String.class), rule.root.constructor );
		assertEquals( "./test/sample.txt", rule.root.args[0] );
		assertEquals( FileAssertion.class.getMethod("exists"), rule.chain.get(0).method );

	}


	@Test
	public void testFileContains() throws SecurityException, NoSuchMethodException { 
		AssertionPredicate parser = new AssertionPredicate("file ./test/sample.txt contains 'Hello World!'");
		parser.parse();
		
		assertEquals( FileAssertion.class, parser.root.clazz );
		assertEquals( FileAssertion.class.getDeclaredConstructor(String.class), parser.root.constructor );
		assertEquals( FileAssertion.class.getMethod("contains", String.class), parser.chain.get(0).method );
		assertEquals( "Hello World!", parser.chain.get(0).args[0] );

	}
	

	
	@Test 
	public void testComposed() { 

		AssertionPredicate parser = new AssertionPredicate("file ./test/sample.txt size > 0 ");
		AssertionPredicate rule = parser.parse();
		
		assertEquals( FileAssertion.class, parser.root.clazz );
		assertEquals( 2, rule.chain.size() );
		assertEquals( "size", rule.chain.get(0).method.getName() );
		assertEquals( "compareTo", rule.chain.get(1).method.getName() );
		
		Object result = rule.invoke( new AssertionContext(".") );
		assertTrue( (Boolean)result );
		
	}

	

	@Test
	public void testWrap() throws Exception { 
		
		assertEquals( null, AssertionPredicate.wrap(String.class, null));
		assertEquals( "Hola", AssertionPredicate.wrap(String.class, "Hola"));
		assertEquals( new Long(1), AssertionPredicate.wrap(Long.class, "1"));
		assertEquals( new Integer(9), AssertionPredicate.wrap(Integer.class, "9"));
		assertEquals( Boolean.FALSE, AssertionPredicate.wrap(Boolean.class, "false"));
		assertEquals( Boolean.TRUE, AssertionPredicate.wrap(Boolean.class, "true"));
		
	
	}
	

	
	
}
