package org.blackcoffee;

import static org.junit.Assert.*;

import java.io.File;

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
		assertEquals( "hola", suite.getTest(1).command.toString() );
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
		assertEquals( "hola", suite.getTest(1).command.toString() );
		assertEquals( "something", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( 2, suite.getTest(1).assertions.get(0).line );

		assertEquals( "ciao", suite.getTest(2).command.toString() );
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
		
		assertEquals( "hola", suite.getTest(1).command.toString() );
		assertEquals( "hello", suite.getTest(2).command.toString() );
		assertEquals( "ciao", suite.getTest(3).command.toString() );
		
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
		
		assertEquals("hola", suite.getTest(1).command.toString() );
		assertEquals("hello", suite.getTest(2).command.toString() );
		assertEquals("ciao", suite.getTest(3).command.toString() );
		assertEquals("hi", suite.getTest(4).command.toString() );

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
		assertEquals( "hola", suite.getTest(1).command.toString() );
		assertEquals( "something", suite.getTest(1).assertions.get(0).declaration );
		assertEquals( "other assertion", suite.getTest(1).assertions.get(1).declaration );
		assertEquals( 2, suite.getTest(1).assertions.get(0).line );
		assertEquals( 3, suite.getTest(1).assertions.get(1).line );
		
		// second assertion 
		assertEquals( "hello", suite.getTest(2).command.toString() );
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
		assertEquals( "hola", suite.getTest(1).command.toString() );
		assertEquals( 0, suite.getTest(1).assertions.size() );
		
		// second test 
		assertEquals( "hello", suite.getTest(2).command.toString() );
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
		assertEquals( "hola", suite.getTest(1).command.toString() );
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
		assertEquals( "t 1", suite.getTest(1).command.toString() );
		assertEquals("global 1", 	suite.getTest(1).assertions.get(0).declaration);
		assertEquals("global 2", 	suite.getTest(1).assertions.get(1).declaration);
		assertEquals("verify a", 	suite.getTest(1).assertions.get(2).declaration);
		
		assertEquals( "t 2", suite.getTest(2).command.toString() );
		assertEquals("global 1", 	suite.getTest(2).assertions.get(0).declaration);
		assertEquals("global 2", 	suite.getTest(2).assertions.get(1).declaration);
		assertEquals("verify b", 	suite.getTest(2).assertions.get(2).declaration);
		assertEquals("verify c", 	suite.getTest(2).assertions.get(3).declaration);

		assertEquals( "t 3", suite.getTest(3).command.toString() );
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
		assertEquals( "hola hello halo", suite.getTest(1).command.toString() );
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
		
		assertEquals( "hola", suite.getTest(1).command.toString() );
		assertEquals( "a",  suite.getTest(1).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(1).exports.get(1).toString() );
		assertEquals( "1",  suite.getTest(1).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(1).variables.get("b").toString() );
	
		assertEquals( 2, 	  suite.getTest(1).exports.size() );

		assertEquals( "hello", suite.getTest(2).command.toString() );
		assertEquals( "a",  suite.getTest(2).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(2).exports.get(1).toString() );
		assertEquals( "x",  suite.getTest(2).exports.get(2).toString() );
		assertEquals( "1",  suite.getTest(2).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(2).variables.get("b").toString() );
		assertEquals( "9",  suite.getTest(2).variables.get("x").toString() );

		assertEquals( 3, 	  suite.getTest(2).exports.size() );

		assertEquals( "cross", suite.getTest(3).command.toString() );
		assertEquals( "a",  suite.getTest(3).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(3).exports.get(1).toString() );
		assertEquals( "w",  suite.getTest(3).exports.get(2).toString() );
		assertEquals( "o",  suite.getTest(3).exports.get(3).toString() );

		assertEquals( "1",  suite.getTest(3).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(3).variables.get("b").toString() );
		assertEquals( "2",  suite.getTest(3).variables.get("w").toString() );
		assertEquals( "8",  suite.getTest(3).variables.get("o").toString() );
		
		assertEquals( 4, 	  suite.getTest(3).exports.size() );
	
		assertEquals( "ciao", suite.getTest(4).command.toString() );
		assertEquals( "a",  suite.getTest(4).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(4).exports.get(1).toString() );
		assertEquals( "w",  suite.getTest(4).exports.get(2).toString() );
		assertEquals( "o",  suite.getTest(4).exports.get(3).toString() );
		assertEquals( "z",  suite.getTest(4).exports.get(4).toString() );

		assertEquals( "1",  suite.getTest(4).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(4).variables.get("b").toString() );
		assertEquals( "2",  suite.getTest(4).variables.get("w").toString() );
		assertEquals( "8",  suite.getTest(4).variables.get("o").toString() );
		assertEquals( "3",  suite.getTest(4).variables.get("z").toString() );

		assertEquals( 5, 	  suite.getTest(4).exports.size() );

		
		assertEquals( "t1", suite.getTest(5).command.toString() );
		assertEquals( "a",  suite.getTest(5).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(5).exports.get(1).toString() );
		assertEquals( "q",  suite.getTest(5).exports.get(2).toString() );
		assertEquals( "v",  suite.getTest(5).exports.get(3).toString() );

		assertEquals( "1",  suite.getTest(5).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(5).variables.get("b").toString() );
		assertEquals( "1",  suite.getTest(5).variables.get("q").toString() );
		assertEquals( "2",  suite.getTest(5).variables.get("v").toString() );

		assertEquals( 4, 	  suite.getTest(5).exports.size() );

		
		assertEquals( "t2", suite.getTest(6).command.toString() );
		assertEquals( "a",  suite.getTest(6).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(6).exports.get(1).toString() );
		assertEquals( "q",  suite.getTest(6).exports.get(2).toString() );
		assertEquals( "v",  suite.getTest(6).exports.get(3).toString() );

		assertEquals( "1",  suite.getTest(6).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(6).variables.get("b").toString() );
		assertEquals( "1",  suite.getTest(6).variables.get("q").toString() );
		assertEquals( "2",  suite.getTest(6).variables.get("v").toString() );
	
		assertEquals( 4, 	  suite.getTest(6).exports.size() );
		
		assertEquals( "last", suite.getTest(7).command.toString() );
		assertEquals( "a",  suite.getTest(7).exports.get(0).toString() );
		assertEquals( "b",  suite.getTest(7).exports.get(1).toString() );
		assertEquals( "1",  suite.getTest(7).variables.get("a").toString() );
		assertEquals( "2",  suite.getTest(7).variables.get("b").toString() );

		assertEquals( 2, 	  suite.getTest(7).exports.size() );
		
		
	} 
	
	@Test 
	public void testVariables () { 
		
		
		String test = 
			"var1=test\n" +
			"var.dos=20\n" +
			"" +
			"test: do ${var1}\n" +
			"assert: result ${var.dos}\n" +
			"x=1\n" +
			"y=2\n" +
			"assert: ${template}\n" +
			"z=3\n" +
			"test: hola\n" +
			"assert: something";
		
		TestSuite suite = reader.read(test);
		
		/*
		 * test #1
		 */
		assertEquals( 2, suite.getTest(1).variables.size() );
		assertEquals( "test", suite.getTest(1).variables.get("var1"));
		assertEquals( "20", suite.getTest(1).variables.get("var.dos"));
		
		assertEquals( 2, suite.getTest(1).assertions.get(0) .variables.size() );
		assertEquals( "test", suite.getTest(1).assertions.get(0) .variables.get("var1"));
		assertEquals( "20", suite.getTest(1).assertions.get(0) .variables.get("var.dos"));
		
		assertEquals( 4, suite.getTest(1).assertions.get(1) .variables.size() );
		assertEquals( "test", suite.getTest(1).assertions.get(1) .variables.get("var1"));
		assertEquals( "20", suite.getTest(1).assertions.get(1) .variables.get("var.dos"));
		assertEquals( "1", suite.getTest(1).assertions.get(1) .variables.get("x"));
		assertEquals( "2", suite.getTest(1).assertions.get(1) .variables.get("y"));
		
		/* 
		 * test #2
		 */
		assertEquals( 5, suite.getTest(2).variables.size() );
		assertEquals( "test", suite.getTest(2).variables.get("var1"));
		assertEquals( "20", suite.getTest(2).variables.get("var.dos"));
		assertEquals( "1", suite.getTest(2).variables.get("x"));
		assertEquals( "2", suite.getTest(2).variables.get("y"));
		assertEquals( "3", suite.getTest(2).variables.get("z"));

		assertEquals( 5, suite.getTest(2).assertions.get(0) .variables.size() );
		assertEquals( "test", suite.getTest(2).assertions.get(0) .variables.get("var1"));
		assertEquals( "20", suite.getTest(2).assertions.get(0) .variables.get("var.dos"));
		assertEquals( "1", suite.getTest(2).assertions.get(0) .variables.get("x"));
		assertEquals( "2", suite.getTest(2).assertions.get(0) .variables.get("y"));
		assertEquals( "3", suite.getTest(2).assertions.get(0) .variables.get("z"));
	}
	
	@Test 
	public void testLogVariables() { 


		String test = 
			"var1=something \\\n" +
			"very \\\n" +
			"long\n" +
			"" +
			"test: do ${var1}\n";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( "something very long", suite.getTest(1).variables.get("var1") );		
		
	}
	
	@Test
	public void testVeryLongVar () { 
		
		String test = 
			"var1=something\\\\\n" +
			"very\\\\\n" +
			"long\n" +
			"" +
			"test: do ${var1}\n";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( "something\nvery\nlong", suite.getTest(1).variables.get("var1") );
				
	}
	
	
	@Test 
	public void testTimeout() { 

		
		String test = 
			"test: hola\n" +
			"timeout: 5s\n" +
			"assert: something\n" +
			"test: ciao\n" +
			"assert: more\n" +
			"timeout: 5";
		
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 5000, suite.getTest(1).timeout.millis() );
		assertEquals( 60*1000, suite.getTest(2).timeout.millis() );
		
	
	}
	
	@Test 
	public void testDefaultTimeout() { 
		String test = 
			"timeout: 33s\n" +
			"test: hola\n" +
			"assert: something\n" +
			"test: ciao\n" +
			"timeout: 5min\n" + 
			"assert: more\n";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 33*1000, suite.defTimeout.millis() );
		assertEquals( 33*1000, suite.getTest(1).timeout.millis() );
		assertEquals( 5*60*1000, suite.getTest(2).timeout.millis() );
		
	}
	
	@Test 
	public void testDisabled() { 
		String test = 
			"test: hola\n" +
			"assert: something\n" +

			"test: ciao\n" +
			"disabled: true\n" +
			"assert: algo\n" +

			"test: hello\n" +
			"disabled: no\n" +
			
			"test: pippo\n" +
			"assert: toto";
		
		TestSuite suite = reader.read(test);
		assertFalse( suite.getTest(1).disabled);
		assertTrue( suite.getTest(2).disabled);
		assertFalse( suite.getTest(3).disabled);
		assertFalse( suite.getTest(4).disabled);

	}
	
	@Test
	public void testInput() { 
		String test = 
			"test: hola\n" +
			"input: file1\n" +	// just one file name
			
			"test: cioa\n" +
			"input: a\n" +		// a pair of declaration to specify a couple of file name
			"input: b\n" +
			"" +
			"test: hello\n" +	
			"input: a, b c, d\n" +	// the command ',' is used a separator as well 
			""+
			"test: hi\n" +
			"input: a b c\n" +		// when no comma is used the space is considered a separator
			"" +
			"test: \n" +
			"input: a 'b c'";		// use quote to capture file names with blanks

		TestSuite suite = reader.read(test);
		
		assertEquals( 1, suite.getTest(1).input.size() );
		assertEquals( "file1", suite.getTest(1).input.get(0));
		
		assertEquals( 2, suite.getTest(2).input.size() );
		assertEquals( "a", suite.getTest(2).input.get(0));
		assertEquals( "b", suite.getTest(2).input.get(1));

		assertEquals( 4, suite.getTest(3).input.size() );
		assertEquals( "a", suite.getTest(3).input.get(0));
		assertEquals( "b", suite.getTest(3).input.get(1));
		assertEquals( "c", suite.getTest(3).input.get(2));
		assertEquals( "d", suite.getTest(3).input.get(3));

		assertEquals( 3, suite.getTest(4).input.size() );
		assertEquals( "a", suite.getTest(4).input.get(0));
		assertEquals( "b", suite.getTest(4).input.get(1));
		assertEquals( "c", suite.getTest(4).input.get(2));

		assertEquals( 2, suite.getTest(5).input.size() );
		assertEquals( "a", suite.getTest(5).input.get(0));
		assertEquals( "b c", suite.getTest(5).input.get(1));
	}
	
	@Test
	public void testBefore() { 
		String test =
			"test: hola\n" +
			"before: mv a b \n" +
			"assert: something\n" +
			"" +
			"test: cioa\n" +
			"";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 1, suite.getTest(1).before.size() );
		assertEquals( null, suite.getTest(2).before);
		
		assertEquals( "mv a b", suite.getTest(1).before.get(0).toString() );
	}

	@Test
	public void testAfter() { 
		String test =
			"test: hola\n" +
			"after: mv a b \n" +
			"assert: something\n" +
			"" +
			"test: name\n" +
			"" +
			"" +
			"test: cioa\n" +
			"after: cp 1 2\n" +
			"after: cp 3 4\n" +
			"";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 1, suite.getTest(1).after.size() );
		assertEquals( null, suite.getTest(2).after);
		assertEquals( 2, suite.getTest(3).after.size() );
		
		assertEquals( "mv a b", suite.getTest(1).after.get(0).toString() );

		assertEquals( "cp 1 2", suite.getTest(3).after.get(0).toString() );
		assertEquals( "cp 3 4", suite.getTest(3).after.get(1).toString() );
	}
	
	@Test 
	public void testGlobalBeforeAfter() { 
		
		String test = 
			"before: cp a b \n" +
			"before: cp 1 2 \n" +
			"after: cp 5 7 \n" +
			"" +
			"test: hola \n" +
			"test: ciao\n" +
			"before: mv x y \n" +
			"test: hello\n" +
			"after: rm * \n" +
			"";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( 2, suite.getTest(1).before.size() );
		assertEquals( 1, suite.getTest(1).after.size() );

		assertEquals( 3, suite.getTest(2).before.size() );
		assertEquals( 1, suite.getTest(2).after.size() );

		assertEquals( 2, suite.getTest(3).before.size() );
		assertEquals( 2, suite.getTest(3).after.size() );

	}

	
	@Test
	public void testExit() { 
		String test = 
			"test: hola\n" +
			"assert: ciao\n" +
			"";
		
		TestSuite suite = reader.read(test);
		assertEquals( 0, (int)suite.defExitCode );
		assertEquals( 0, (int)suite.getTest(1).exit );

	}
	
	@Test
	public void testExit1() { 

		String test = 
			"test: hola\n" +
			"exit: 1\n" +
			"assert: ciao\n" +
			"";
		
		TestSuite suite = reader.read(test);
		assertEquals( 0, (int)suite.defExitCode );
		assertEquals( 1, (int)suite.getTest(1).exit );
	
		
	}
	
	@Test 
	public void testExitNotCheck() { 

		String test = 
			"test: hola\n" +
			"exit: any\n" +
			"assert: ciao\n" +
			"";
		
		TestSuite suite = reader.read(test);
		assertEquals( 0, (int)suite.defExitCode );
		assertEquals( null, suite.getTest(1).exit );
		
	}
	
	@Test 
	public void testExitDefault() { 

		String test = 
			"exit: 99\n" +
			"" +
			"test: first\n" +
			"assert: something\n" +
			"" +
			"" +
			"test: hola\n" +
			"exit: 0\n" +
			"assert: ciao\n" +
			"" +
			"test: more\n" +
			"exit: any";
		
		TestSuite suite = reader.read(test);
		assertEquals( 99, (int)suite.defExitCode );
		assertEquals( 99, (int)suite.getTest(1).exit );
		assertEquals( 0, (int)suite.getTest(2).exit );
		assertEquals( null, suite.getTest(3).exit );
		
	}
	
	@Test 
	public void testTags() { 
		String test = 
				"test: hola\n" +
				"test: hello\n" +
				"tag: a\n" +
				"" +
				"test: ciao\n" +
				"tag: a b \n" +
				"" +
				"test: hi\n" +
				"tag: a, b, c \n" +
				"" +
				"test: more\n" +
				"tag: a 'x y z'" +
				"";
		
		TestSuite suite = reader.read(test);
		
		assertEquals( null, suite.getTest(1).tags );
		assertEquals( 1, suite.getTest(2).tags.size() );
		assertEquals( 2, suite.getTest(3).tags.size() );
		assertEquals( 3, suite.getTest(4).tags.size() );
		assertEquals( 2, suite.getTest(5).tags.size() );

		assertEquals( "a", suite.getTest(2).tags.get(0) );

		assertEquals( "a", suite.getTest(3).tags.get(0) );
		assertEquals( "b", suite.getTest(3).tags.get(1) );
		
		assertEquals( "a", suite.getTest(4).tags.get(0) );
		assertEquals( "b", suite.getTest(4).tags.get(1) );
		assertEquals( "c", suite.getTest(4).tags.get(2) );

		assertEquals( "a", suite.getTest(5).tags.get(0) );
		assertEquals( "x y z", suite.getTest(5).tags.get(1) );

	}
	
	
	@Test 
	public void testReadWithConf() { 
		
		String conf = 
				"export a=1 \n" +
				"export b=2 \n" +
				"before: do that \n" +
				"timeout: 10s \n" +
				"" +
				"";
		
		String test =
				"export b=20 \n" +
				"export c=30 \n" +
				"after: hola \n" +
				"" +
				"test: 1\n" +
				"test: 2\n";
		
		TestSuite confSuite = new TestSuiteReader().read(conf);
		
		TestSuite suite = new TestSuiteReader(confSuite).read(test);
		
		
		assertEquals( 2, suite.size() );
		assertEquals( "1" , suite.getTest(1).command.toString() );
		assertEquals( "2" , suite.getTest(2).command.toString() );
		
		assertEquals( 3, suite.globalExports.size() );
		assertEquals( "a", suite.globalExports.get(0) );
		assertEquals( "b", suite.globalExports.get(1) );
		assertEquals( "c", suite.globalExports.get(2) );
	
		assertEquals( "1", suite.variables.value("a") );
		assertEquals( "20", suite.variables.value("b") );
		assertEquals( "30", suite.variables.value("c") );
	
		assertEquals( 3, suite.getTest(1).exports.size()  );
		assertEquals( "a", suite.getTest(1).exports.get(0) );
		assertEquals( "b", suite.getTest(1).exports.get(1) );
		assertEquals( "c", suite.getTest(1).exports.get(2) );

		assertEquals( "1", suite.getTest(1).variables.value("a") );
		assertEquals( "20", suite.getTest(1).variables.value("b") );
		assertEquals( "30", suite.getTest(1).variables.value("c") );

		
		assertEquals( 3, suite.getTest(2).exports.size()  );
		assertEquals( "a", suite.getTest(2).exports.get(0) );
		assertEquals( "b", suite.getTest(2).exports.get(1) );
		assertEquals( "c", suite.getTest(2).exports.get(2) );		
		

		assertEquals( "1", suite.getTest(2).variables.value("a") );
		assertEquals( "20", suite.getTest(2).variables.value("b") );
		assertEquals( "30", suite.getTest(2).variables.value("c") );

		assertEquals( 1, suite.getTest(1).before.size() );
		assertEquals( 1, suite.getTest(1).after.size() );

		assertEquals( "do that", suite.getTest(1).before.get(0).toString() );
		assertEquals( "hola", suite.getTest(1).after.get(0).toString() );

		
		assertEquals( 1, suite.getTest(2).before.size() );
		assertEquals( 1, suite.getTest(2).after.size() );

		assertEquals( "do that", suite.getTest(2).before.get(0).toString() );
		assertEquals( "hola", suite.getTest(2).after.get(0).toString() );
	
		
		assertEquals( 1, suite.getTest(1).before.size() );
		assertEquals( 1, suite.getTest(1).after.size() );

		assertEquals( 10*1000, suite.getTest(1).timeout.millis() );
		assertEquals( 10*1000, suite.getTest(2).timeout.millis() );
	
	}
	
	@Test
	public void testInputPath( ) { 
		String test = 
				"input: testsuite/data \n" +
				"" +
				"test: uno\n" +
				"test: dos\n" +
				"";
		
		
		TestSuite suite = new TestSuiteReader().read(test);
		
		assertEquals( new File(System.getProperty("user.dir"), "testsuite/data"), suite.getTest(1).inputPath );
	
	}
	
}
