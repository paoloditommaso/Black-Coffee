package org.blackcoffee.commons.utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CmdLineUtilsTest {


	@Test 
	public void testCmdLineTokenizer() { 
		List<String> args = CmdLineUtils.cmdLineTokenizer("t-coffee input.fa\n-mode=something -about -output html score ascii -multi_core=4 -flag");
		
		assertEquals( 7, args.size());
		assertEquals( "t-coffee", args.get(0) );
		assertEquals( "input.fa", args.get(1) );
		assertEquals( "-mode=something", args.get(2) );
		assertEquals( "-about", args.get(3) );
		assertEquals( "-output html score ascii", args.get(4) );
		assertEquals( "-multi_core=4", args.get(5) );
		assertEquals( "-flag", args.get(6) );
	}
	
	@Test
	public void testInvalidDashSeparator() { 
		// in this test the option 'wrong' uses a bad option separator, it is not a minus character '-' 
		// but a dash character (usually it came out when using word processor auto replacement) 
		assertEquals( "-opt 1 -wrong -more", CmdLineUtils.normalize("-opt 1 Ðwrong -more") );
	}	
}
