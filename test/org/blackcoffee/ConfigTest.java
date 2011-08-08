package org.blackcoffee;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.blackcoffee.Config.Delete;
import org.blackcoffee.Config.Print;
import org.blackcoffee.Config.Stop;
import org.blackcoffee.report.ConsoleReport;
import org.blackcoffee.report.HtmlReport;
import org.blackcoffee.report.TextReport;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {
	
	private String working;
	private String home;

	@Before
	public void before() { 
		home = System.getProperty("user.home");
		working = System.getProperty("user.dir");
	}

	public void testRange(String opt) { 

		Config config = Config.parse(opt, "9").initiliaze();
		assertEquals( 1, config.range.length );
		assertEquals( 9, config.range[0] );
	
		
		config = Config.parse(opt, "1:9").initiliaze();
		assertEquals( 2, config.range.length );
		assertEquals( 1, config.range[0] );
		assertEquals( 9, config.range[1] );
	
		config = Config.parse(opt, "1:").initiliaze();
		assertEquals( 2, config.range.length );
		assertEquals( 1, config.range[0] );
		assertEquals( Integer.MAX_VALUE, config.range[1] );

		config = Config.parse(opt, ":99").initiliaze();
		assertEquals( 2, config.range.length );
		assertEquals( 1, config.range[0] );
		assertEquals( 99, config.range[1] );
	}
	
	@Test
	public void testRange() { 
		testRange("-r");
		testRange("--range");
	}

	
	@Test
	public void testInputDir() { 
		testInputDir("-i");
		testInputDir("--input-dir");
	}
	
	public void testInputDir(String opt) {

		Config config = Config.parse(opt, "path" ).initiliaze();
		assertEquals(new File(working, "path"), config.inputPath );
	
		config = Config.parse(opt, "./path" ).initiliaze();
		assertEquals(new File(working, "path"), config.inputPath );

		config = Config.parse(opt, "/some/abs/path" ).initiliaze();
		assertEquals(new File("/some/abs/path"), config.inputPath );
	
	}
	
	@Test
	public void testSandboxPath() {
		testSandboxPath("-s");
		testSandboxPath("--sandbox-dir");
	}

	private void testSandboxPath(String opt) {

		Config config = Config.parse(opt, "path" ).initiliaze();
		assertEquals(new File(working, "path"), config.sandboxPath );
	
		config = Config.parse(opt, "./path" ).initiliaze();
		assertEquals(new File(working, "path"), config.sandboxPath );

		config = Config.parse(opt, "/some/abs/path" ).initiliaze();
		assertEquals(new File("/some/abs/path"), config.sandboxPath );
	} 
	
	
	@Test
	public void testOutputFile() { 
		testOutputFile("-o");
		testOutputFile("--output-file");
	}

	private void testOutputFile(String opt) {

		/* html report */
		Config config = Config.parse(opt, "file.html" ).initiliaze();
		assertEquals(new File(working, "file.html"), ((HtmlReport)config.report).getFile() );
		assertEquals( HtmlReport.class, config.report.getClass() );
		
		config = Config.parse(opt, "./file.html" ).initiliaze();
		assertEquals(new File(working, "file.html"), ((HtmlReport)config.report).getFile() );

		config = Config.parse(opt, "/some/abs/file.html" ).initiliaze();
		assertEquals(new File("/some/abs/file.html"), ((HtmlReport)config.report).getFile() );

		/* text file report */
		config = Config.parse(opt, "file.txt" ).initiliaze();
		assertEquals(new File(working, "file.txt"), ((TextReport)config.report).getFile() );

		/* console report by default */
		config = Config.parse( ).initiliaze();
		assertEquals( ConsoleReport.class, config.report.getClass() );
	
	
	}
	
	@Test
	public void testDelete() { 
		testDelete("-d");
		testDelete("--delete");
	}

	private void testDelete(String opt) {
		
		/* passed|failed|all|never */
		
		Config config = Config.parse(opt, "passed" ).initiliaze();
		assertEquals( Delete.passed, config.delete );

		config = Config.parse(opt, "failed" ).initiliaze();
		assertEquals( Delete.failed, config.delete );

		config = Config.parse(opt, "all" ).initiliaze();
		assertEquals( Delete.all, config.delete );
	
		config = Config.parse(opt, "never" ).initiliaze();
		assertEquals( Delete.never, config.delete );
	
		config = Config.parse(opt, "xxx" ).initiliaze();
		assertEquals( 1, (int)config.exit );
	
		// default: delete all passed test
		config = Config.parse().initiliaze();
		assertEquals( Delete.passed, config.delete );
	
			
	}
	
	@Test 
	public void testStop() { 
		testStop("-S");
		testStop("-stop");
	}

	private void testStop(String opt) {

		
		Config config = Config.parse(opt, "first" ).initiliaze();
		assertEquals( Stop.first, config.stop );

		config = Config.parse(opt, "failed" ).initiliaze();
		assertEquals( Stop.failed, config.stop );

		config = Config.parse(opt, "error" ).initiliaze();
		assertEquals( Stop.error, config.stop);
	
		config = Config.parse(opt, "never" ).initiliaze();
		assertEquals( Stop.never, config.stop );
	
		config = Config.parse(opt, "xxx" ).initiliaze();
		assertEquals( 1, (int)config.exit );
	
		// default: stop on first failure 
		config = Config.parse().initiliaze();
		assertEquals( Stop.failed, config.stop );
		
	}
	
	@Test
	public void testConfFile() { 
		testConfFile("-c");
		testConfFile("--config-file");
	}

	private void testConfFile(String opt) {

		Config config = Config.parse(opt, "conf.file" ).initiliaze();
		assertEquals(new File(working, "conf.file"), config.confFile );
	
		config = Config.parse(opt, "./conf.file" ).initiliaze();
		assertEquals(new File(working, "conf.file"), config.confFile );

		config = Config.parse(opt, "/some/abs/path/conf.file" ).initiliaze();
		assertEquals(new File("/some/abs/path/conf.file"), config.confFile );

		config = Config.parse(opt, "~/conf.file" ).initiliaze();
		assertEquals(new File(home, "conf.file"), config.confFile );
		
	}
	
	@Test
	public void testPrintStdout() { 
		Config config = Config.parse("--print-stdout", "onerror" ).initiliaze();
		assertEquals( Print.onerror, config.reportStdOut );

		config = Config.parse("--print-stdout", "never" ).initiliaze();
		assertEquals( Print.never, config.reportStdOut );
	
		config = Config.parse("--print-stdout", "always" ).initiliaze();
		assertEquals( Print.always, config.reportStdOut );

		// default: never
		config = Config.parse().initiliaze();
		assertEquals( Print.never, config.reportStdOut );

	}
	
	

	@Test
	public void testPrintStErr() { 
		Config config = Config.parse("--print-stderr", "onerror" ).initiliaze();
		assertEquals( Print.onerror, config.reportStdErr );

		config = Config.parse("--print-stderr", "never" ).initiliaze();
		assertEquals( Print.never, config.reportStdErr );
	
		config = Config.parse("--print-stderr", "always" ).initiliaze();
		assertEquals( Print.always, config.reportStdErr );

		// default: never
		config = Config.parse().initiliaze();
		assertEquals( Print.never, config.reportStdErr );

	}
	
	@Test
	public void testTags() { 
		testTag("-t");
		testTag("--tag");
	}

	private void testTag(String opt) {
		Config config = Config.parse(opt, "uno,dos,tres" ).initiliaze();
		assertEquals( Arrays.asList("uno","dos","tres"), config.tags );

		config = Config.parse(opt, "alpha", opt, "beta" ).initiliaze();
		assertEquals( Arrays.asList("alpha","beta" ), config.tags );
	
	}

	
	@Test
	public void testVariable() { 
		testVariable("-V");
		testVariable("--var");
	}

	private void testVariable(String opt) {
		Config config = Config.parse(opt, "alpha=1", opt, "beta=dos" ).initiliaze();
		assertEquals( 2, config.vars.size() );
		assertEquals( "1", config.vars.get("alpha") );
		assertEquals( "dos", config.vars.get("beta") );
	}
	
}
