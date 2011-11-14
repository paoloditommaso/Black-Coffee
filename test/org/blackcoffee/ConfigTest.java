package org.blackcoffee;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.Config.Delete;
import org.blackcoffee.Config.Print;
import org.blackcoffee.Config.Stop;
import org.blackcoffee.report.CompositeReport;
import org.blackcoffee.report.ConsoleReport;
import org.blackcoffee.report.HtmlReport;
import org.blackcoffee.report.ReportBuilder;
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
		ReportBuilder report = ((CompositeReport)config.report).getDelegates()[0];
		assertEquals(new File(working, "file.html"), ((HtmlReport)report).getFile() );
		assertEquals( HtmlReport.class, report.getClass() );
		
		config = Config.parse(opt, "./file.html" ).initiliaze();
		report = ((CompositeReport)config.report).getDelegates()[0];
		assertEquals(new File(working, "file.html"), ((HtmlReport)report).getFile() );

		config = Config.parse(opt, "/some/abs/file.html" ).initiliaze();
		report = ((CompositeReport)config.report).getDelegates()[0];
		assertEquals(new File("/some/abs/file.html"), ((HtmlReport)report).getFile() );

		/* text file report */
		config = Config.parse(opt, "file.txt" ).initiliaze();
		report = ((CompositeReport)config.report).getDelegates()[0];
		assertEquals(new File(working, "file.txt"), ((TextReport)report).getFile() );

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
		testStop("-s");
		testStop("--stop");
	}

	private void testStop(String opt) {

		
		Config config;
		
		config = Config.parse(opt, "failed" ).initiliaze();
		assertEquals( Stop.failed, config.stop );

		config = Config.parse(opt, "error" ).initiliaze();
		assertEquals( Stop.error, config.stop);
	
		config = Config.parse(opt, "never" ).initiliaze();
		assertEquals( Stop.never, config.stop );
	
		
		config = Config.parse(opt, "99" ).initiliaze();
		assertEquals( Stop.count, config.stop );
		assertEquals( 99, config.stopCount );
		
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
	public void testPrintVAlgrind() { 
		Config config = Config.parse("--print-valgrind", "onerror" ).initiliaze();
		assertEquals( Print.onerror, config.reportValgrind );

		config = Config.parse("--print-valgrind", "never" ).initiliaze();
		assertEquals( Print.never, config.reportValgrind );
	
		config = Config.parse("--print-valgrind", "always" ).initiliaze();
		assertEquals( Print.always, config.reportValgrind );

		// default: never
		config = Config.parse().initiliaze();
		assertEquals( Print.never, config.reportValgrind );

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
	
	@Test
	public void testHtmlPathPrefix() { 
		Config config;
		
		config = Config.parse().initiliaze();
		assertEquals(null, config.htmlPathPrefix);

		config = Config.parse("--html-path-prefix", "/xxx").initiliaze();
		assertEquals("/xxx", config.htmlPathPrefix);
	
	}
	
	@Test
	public void testValgrind() { 
		Config config;
		
		config = Config.parse().initiliaze();
		assertFalse( config.valgrind );

		config = Config.parse("--valgrind").initiliaze();
		assertTrue( config.valgrind );
		assertTrue( StringUtils.isEmpty(config.valgrindOptions) );
		
		config = Config.parse("--valgrind", "no").initiliaze();
		assertFalse( config.valgrind );

		config = Config.parse("--valgrind", "false").initiliaze();
		assertFalse( config.valgrind );

		config = Config.parse("--valgrind", "yes").initiliaze();
		assertTrue( config.valgrind );
		assertNull( config.valgrindOptions );

		config = Config.parse("--valgrind", "true").initiliaze();
		assertTrue( config.valgrind );
		assertNull( config.valgrindOptions );

		
		config = Config.parse("--valgrind", "\"-this -that\"").initiliaze();
		assertTrue( config.valgrind );
		assertEquals( "-this -that", config.valgrindOptions );
	
	}
	
	@Test
	public void testRecurse() { 
		Config config;

		config = Config.parse("./testdata/recurse").initiliaze();
		assertFalse( config.recurseDir );
		assertEquals( 0, config.testFiles.size() );
		
		config = Config.parse("-R", "./testdata/recurse").initiliaze();
		
		assertTrue( config.recurseDir );
		assertEquals( 2, config.testFiles.size() );
	}
}
