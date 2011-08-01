package org.blackcoffee;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestSuiteReaderTest {

	private TestSuiteReader reader;


	@Before
	public void before() { 
		this.reader = new TestSuiteReader();
	}

	@Test
	public void testReadBasic() { 
		String test = 
			"test: hola\n" +
			"assert: something";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 1, suite.tests.values().size() );
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( "something", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( 2, suite.getTest(1).assertions.get(0).line );
	}
	

	@Test
	public void testTwoTests() { 
		String test = 
			"test: hola\n" +
			"assert: something\n" +
			"\n" +
			"test: ciao\n" +
			"assert: second";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 2, suite.tests.values().size() );
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( "something", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( 2, suite.getTest(1).assertions.get(0).line );

		assertEquals( "ciao", suite.getTest(2).command );
		assertEquals( "second", suite.getTest(2).assertions.get(0).declaration );
	}
	
	@Test
	public void testThreeTests () { 
		
		String test = 
			"test: hola\n" +
			"test: hello\n" +
			"test: ciao\n" +
			"assert: something\n" +
			"assert: second";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 3, suite.tests.values().size() );
		assertEquals( 0, suite.getTest(1).assertions.size() );
		assertEquals( 0, suite.getTest(2).assertions.size() );
		assertEquals( 2, suite.getTest(3).assertions.size() );
		
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( "hello", suite.getTest(2).command );
		assertEquals( "ciao", suite.getTest(3).command );
		
		assertEquals( "something", suite.getTest(3).assertions.get(0).declaration );
		assertEquals( "second", suite.getTest(3).assertions.get(1).declaration );
	}
	
	@Test
	public void testGlobalAssertPlusMoreTests () { 
		String tests = 
			"assert: global 1\n" +
			"\n" +
			"test: hola\n" +
			"assert: this\n" +
			"test: hello\n" +
			"test: ciao\n" +
			"assert: one\n" +
			"assert: dos\n" +
			"test: hi\n" +
			"";
		
		TestSuite suite = reader.read(tests);

		assertEquals( 4, suite.size() );
		
		assertEquals("hola", suite.getTest(1).command );
		assertEquals("hello", suite.getTest(2).command );
		assertEquals("ciao", suite.getTest(3).command );
		assertEquals("hi", suite.getTest(4).command );

		assertEquals( 2, suite.getTest(1).assertions.size() );
		assertEquals("global 1", suite.getTest(1).assertions.get(0).declaration ); 
		assertEquals("this",     suite.getTest(1).assertions.get(1).declaration ); 
		
		assertEquals( 1, suite.getTest(2).assertions.size() );
		assertEquals("global 1", suite.getTest(2).assertions.get(0).declaration ); 

		assertEquals( 3, suite.getTest(3).assertions.size() );
		assertEquals("global 1", suite.getTest(3).assertions.get(0).declaration ); 
		assertEquals("one", suite.getTest(3).assertions.get(1).declaration ); 
		assertEquals("dos", suite.getTest(3).assertions.get(2).declaration ); 

		assertEquals( 1, suite.getTest(4).assertions.size() );
		assertEquals("global 1", suite.getTest(4).assertions.get(0).declaration ); 
		
	}
	
	@Test 
	public void testReadTwoTestCase() { 

		String test = 
			"test: hola\n" +
			"assert: something\n" +
			"assert: other assertion\n" +
			"\n" +
			"test: hello\n" +
			"\n" +
			"assert: a1\n" +
			"assert: a2\n" +
			"assert: a3";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 2, suite.tests.values().size() );
		// first assertion 
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( "something", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( "other assertion", suite.getTest(1).assertions.get(1).declaration );
		assertEquals( 2, suite.getTest(1).assertions.get(0).line );
		assertEquals( 3, suite.getTest(1).assertions.get(1).line );
		
		// second assertion 
		assertEquals( "hello", suite.getTest(2).command );
		assertEquals( "a1", suite.getTest(2).assertions.get(0).declaration );
		assertEquals( "a2", suite.getTest(2).assertions.get(1).declaration );
		assertEquals( "a3", suite.getTest(2).assertions.get(2).declaration );

		assertEquals( 7, suite.getTest(2).assertions.get(0).line );
		assertEquals( 8, suite.getTest(2).assertions.get(1).line );
		assertEquals( 9, suite.getTest(2).assertions.get(2).line );
	
	}
	
	
	@Test 
	public void testReadCrossTest() { 

		/*
		 * - the first test has 0 assertion 
		 * - the second test has 2 assertion 
		 */
		String test = 
			"test: hola\n" +
			"test: hello\n" +
			"assert: something\n" +
			"assert: other assertion\n" +
			"\n" ;
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 2, suite.tests.values().size() );
		
		// first test  
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( 0, suite.getTest(1).assertions.size() );
		
		// second test 
		assertEquals( "hello", suite.getTest(2).command );
		assertEquals( "something", suite.getTest(2).assertions.get(0).declaration );
		assertEquals( "other assertion", suite.getTest(2).assertions.get(1).declaration );
		assertEquals( 3, suite.getTest(2).assertions.get(0).line );
		assertEquals( 4, suite.getTest(2).assertions.get(1).line );
		
	}	

	
	@Test 
	public void testCommonAssertion() { 
		

		String test = 
			"assert: common1\n" +
			"assert: common2\n" +
			"\n" +
			"test: hola\n" +
			"assert: something\n" +
			"assert: more\n" +
			"\n" ;
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 1, suite.tests.values().size() );
		
		// common assertion 
		
		// first test  
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals("common1", 	suite.getTest(1).assertions.get(0).declaration);
		assertEquals("common2", 	suite.getTest(1).assertions.get(1).declaration);
		assertEquals( "something", 	suite.getTest(1).assertions.get(2).declaration );
		assertEquals( "more", 		suite.getTest(1).assertions.get(3).declaration );
		assertEquals( 1, suite.getTest(1).assertions.get(0).line );
		assertEquals( 2, suite.getTest(1).assertions.get(1).line );		
		assertEquals( 5, suite.getTest(1).assertions.get(2).line );
		assertEquals( 6, suite.getTest(1).assertions.get(3).line );		
		
	}
	

	@Test 
	public void testGlobalAsserts() { 
		

		String test = 
			"assert: global 1\n" +
			"assert: global 2\n" +
			"\n" +
			"test: t 1\n" +
			"assert: verify a\n" +
			"test: t 2\n" +
			"assert: verify b\n" +
			"assert: verify c\n" +
			"test: t 3\n" +
			"assert: verify z\n" +
			"\n" ;
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 3, suite.tests.values().size() );
		
		// first test  
		assertEquals( "t 1", suite.getTest(1).command );
		assertEquals("global 1", 	suite.getTest(1).assertions.get(0).declaration);
		assertEquals("global 2", 	suite.getTest(1).assertions.get(1).declaration);
		assertEquals("verify a", 	suite.getTest(1).assertions.get(2).declaration);
		
		assertEquals( "t 2", suite.getTest(2).command );
		assertEquals("global 1", 	suite.getTest(2).assertions.get(0).declaration);
		assertEquals("global 2", 	suite.getTest(2).assertions.get(1).declaration);
		assertEquals("verify b", 	suite.getTest(2).assertions.get(2).declaration);
		assertEquals("verify c", 	suite.getTest(2).assertions.get(3).declaration);

		assertEquals( "t 3", suite.getTest(3).command );
		assertEquals("global 1", 	suite.getTest(3).assertions.get(0).declaration);
		assertEquals("global 2", 	suite.getTest(3).assertions.get(1).declaration);
		assertEquals("verify z", 	suite.getTest(3).assertions.get(2).declaration);
	

	}
	

	@Test
	public void testBrokenLine() { 
		String test = 
			"test:\\\n" +
			" hola\\  \n" +  // <-- look traling blanks are removed
			" hello\\\n" +
			" halo\n" +
			"" +
			"assert: something\\\n" +
			" very\\\n" +
			" long\n" +
			"assert: short one\n" +
			"assert: more \\\n" +
			"long";
		
		TestSuite suite = reader.read(test);
		
		// test the test .. 
		assertEquals( 1, suite.tests.values().size() );
		assertEquals( "hola hello halo", suite.getTest(1).command );
		// test assertion value 
		assertEquals( "something very long", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( "short one", suite.getTest(1).assertions.get(1).declaration );
		assertEquals( "more long", suite.getTest(1).assertions.get(2).declaration );
		// test assertion location 
		assertEquals( 5, suite.getTest(1).assertions.get(0).line );
		assertEquals( 8, suite.getTest(1).assertions.get(1).line );
		assertEquals( 9, suite.getTest(1).assertions.get(2).line );

	}	
	
	
	@Test 
	public void testExports() {
		String test = 
			"export a=1\n" +		// declares TWO commons variables
			"export b=2\n" +		// inherited by all tests 
			"" +
			"test: hola\n" +
			"assert: something\n" +
			"assert: other assertion\n" +
			"\n" +
			"export x=9\n" +		// one local variable visibile only to test 'hello'
			"test: hello\n" +
			"assert: a1\n" +
			"\n" +
			"" +
			"export w=2\n" +		// TWO local variables visible only to test 'cross'
			"export o=8\n" +
			"test: cross\n" +
			"" +
			"export z=3\n" +		// ONE local variable visible only to test 'ciao' 
			"test: ciao\n" +
			"assert: a2\n" +
			"assert: a3\n" + 
			"\n" +
			"export q=1\n" +		
			"export v=2\n" +		
			"test: t1\n" +
			"test: t2\n" +
			"assert: a2\n" +
			"assert: a3\n" + 
			"\n" +
			"test: last\n" +
			"assert: a2\n" +
			"assert: a3";
		
			
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 7, suite.tests.values().size() );
		
		assertEquals( "hola", suite.getTest(1).command );
		assertEquals( "a=1",  suite.getTest(1).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(1).env.get(1).toString() );
		assertEquals( 2, 	  suite.getTest(1).env.size() );

		assertEquals( "hello", suite.getTest(2).command );
		assertEquals( "a=1",  suite.getTest(2).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(2).env.get(1).toString() );
		assertEquals( "x=9",  suite.getTest(2).env.get(2).toString() );
		assertEquals( 3, 	  suite.getTest(2).env.size() );

		assertEquals( "cross", suite.getTest(3).command );
		assertEquals( "a=1",  suite.getTest(3).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(3).env.get(1).toString() );
		assertEquals( "w=2",  suite.getTest(3).env.get(2).toString() );
		assertEquals( "o=8",  suite.getTest(3).env.get(3).toString() );
		assertEquals( 4, 	  suite.getTest(3).env.size() );
	
		assertEquals( "ciao", suite.getTest(4).command );
		assertEquals( "a=1",  suite.getTest(4).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(4).env.get(1).toString() );
		assertEquals( "w=2",  suite.getTest(4).env.get(2).toString() );
		assertEquals( "o=8",  suite.getTest(4).env.get(3).toString() );
		assertEquals( "z=3",  suite.getTest(4).env.get(4).toString() );
		assertEquals( 5, 	  suite.getTest(4).env.size() );

		
		assertEquals( "t1", suite.getTest(5).command );
		assertEquals( "a=1",  suite.getTest(5).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(5).env.get(1).toString() );
		assertEquals( "q=1",  suite.getTest(5).env.get(2).toString() );
		assertEquals( "v=2",  suite.getTest(5).env.get(3).toString() );
		assertEquals( 4, 	  suite.getTest(5).env.size() );

		
		assertEquals( "t2", suite.getTest(6).command );
		assertEquals( "a=1",  suite.getTest(6).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(6).env.get(1).toString() );
		assertEquals( "q=1",  suite.getTest(6).env.get(2).toString() );
		assertEquals( "v=2",  suite.getTest(6).env.get(3).toString() );
		assertEquals( 4, 	  suite.getTest(6).env.size() );
		
		assertEquals( "last", suite.getTest(7).command );
		assertEquals( "a=1",  suite.getTest(7).env.get(0).toString() );
		assertEquals( "b=2",  suite.getTest(7).env.get(1).toString() );
		assertEquals( 2, 	  suite.getTest(7).env.size() );
		
		
	} 
	
	@Test 
	public void testVariables () { 
		
		String test = 
			"var1=test\n" +
			"var.dos=20\n" +
			"template=\\\n" +
			" something\\\n" +
			" very\\\n" +
			" long\n" +
			"" +
			"test: do ${var1}\n" +
			"assert: result ${var.dos}\n" +
			"assert: ${template}";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( "do test", suite.getTest(1).command );
		assertEquals( "result 20", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( "something very long", suite.getTest(1).assertions.get(1).declaration );
		
		
	}
	
	@Test 
	public void testLogVariables() { 

		String test = 
			"var1=something\\\\\n" +
			"very\\\\\n" +
			"long\n" +
			"" +
			"test: do ${var1}\n";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( "do something\nvery\nlong", suite.getTest(1).command );
		
	}
}
