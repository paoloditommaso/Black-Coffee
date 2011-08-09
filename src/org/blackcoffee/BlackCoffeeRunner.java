package org.blackcoffee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.blackcoffee.Config.Delete;
import org.blackcoffee.Config.Stop;
import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.exception.ExitFailed;
import org.blackcoffee.report.ReportBuilder;
import org.blackcoffee.utils.PathUtils;

/**
 * The main starter class 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class BlackCoffeeRunner  {
	
	final Config config;
	
	final ReportBuilder report;
	
	boolean hasError = false; 
	
	public BlackCoffeeRunner(Config config) {
		this.config = config;
		this.report = config.report;
		
		/*
		 * check if there are errors
		 */
		if( config.exit == null ) { 
			// its ok 
		}
		else if( config.exit == 0 ) { 
			System.out.print( config.errorString.toString() );
			System.exit(0);
		}
		else { 
			System.err.print( config.errorString.toString() );
			System.exit(config.exit);
		}

		/*
		 * check that the files exisst 
		 */
		for( File file : config.testFiles ) { 
			if( !file.exists() ) { 
				System.err.printf("Missing test configuration file: %s\n", config.testFiles);
				System.exit(1);
			}
		}
		
		if( config.testFiles.isEmpty() ) { 
			config.printUsage();

			System.err.printf("You don't provide any testcase to run. Check that you have specified a path containing at least a '.testcase' or '.testsuite' file.\n\n");
			System.out.println( config.errorString ); // <-- prints the usage string produced by the aboe printUsage
			System.exit(1);
		}

				
	}


	public int execute() { 
		
		report.begin();

		try { 
			List<TestSuite> all = new ArrayList<TestSuite>( config.testFiles.size() );
			
			/*
			 * cofigure and compile all 
			 */
			for ( File file : config.testFiles ) 
			{ 
				all.add( loadAndCompile(file) );
			}
			
			for( TestSuite suite: all ) 
			{ 
				report.printHeader( "Test: " + suite.testFile.getAbsolutePath() );
				execute( suite );
			}
			
			return hasError ? 1 : 0;
			
		}
		finally { 
			report.end();
		}
	}
	
	
	TestSuite loadAndCompile(File testFile) {

		/* 
		 * 1. top level configuration: 
		 * - specified by the command line 
		 * - OR in the current directory 
		 */
		File firstConfig;
		if( config.confFile != null ) { 
			// try to use the configuration file specified on the command line .. 
			firstConfig = config.confFile;	
		}
		else { 
			// .. otherwise fallback to the one in the current directory 
			firstConfig = new PathUtils() .absolute(".testconf");
		}
		
		TestSuite confSuite = (firstConfig.exists()) ? new TestSuiteReader().read(firstConfig) : null;
		
		/* 
		 * 2. second level configuration: 
		 * - a '.testconf' file in the test folder 
		 */
		File secondConfig = new PathUtils() .current( testFile.getParentFile() ) .absolute(".testconf");
		
		confSuite = ( secondConfig.exists() && secondConfig.equals(firstConfig) )
				  ? new TestSuiteReader(confSuite).read(secondConfig) 
				  : confSuite;
		
		// read the test suite 
		TestSuite suite = new TestSuiteReader(confSuite).read(testFile);
		suite.configure(config, testFile);
		
		// compile the assertions 
		suite.compile();

		return suite;
	}


	/**
	 * Execute all teh tests defined as subdirectory in the current path
	 */
	void execute(TestSuite suite) {

		int count = 0;
		Range range = rangeFor(suite);
		
		for( int index=range.first; index <= range.last; index++ ) {
			count++;
			TestCase test = suite.tests.get(index);

			// check tag 
			if( !matchTag(test, config.tags) ) { 
				continue;
			}
			
			/* run the test */
			TestResult result = execute(test);

			// when there is at least an error raise this flag
			hasError = hasError || result.status != TestStatus.PASSED;
			
			// check the result and decide if continue with the next iteration
			if( config.stop == Stop.never ) { continue; } 
			else if( config.stop == Stop.first ) { break; } 
			else if( test.disabled ) { continue; }
			else if( config.stop == Stop.error && result.status == TestStatus.ERROR ) { break; } 
			else if( config.stop == Stop.failed && (result.status != TestStatus.PASSED ) ) { break; } 
		}
		
		if( count == 0 ) { 
			report.print("Nothing to test!");
		}
	}
	
	static boolean matchTag(TestCase test, List<String> configTags) {
		if( configTags == null || configTags.size() == 0 ) { 
			return true; // the test have to be executed when not tag is specified ->  true by defualt
		}
		
		for( String tag : configTags ) { 
			boolean not = false;
			if( tag.startsWith("!") ) { 
				not = true;
				tag = tag.substring(1);
			}
			
			
			if( not ) { 
				if( test.tags==null || !test.tags.contains(tag) ) { 
					return true;
				}
				
			} else  {
				if( test.tags != null && test.tags.contains(tag) ) { 
					return true;
				}
			}
		}
		
		return false;
	}


	/**
	 * Execute the test as specified in the path 
	 * 
	 * @param dir the directory containing the test data 
	 * @return 
	 * @throws ConfigurationException 
	 */
	TestResult execute( TestCase testCase ) { 

		report.printTest(testCase);
		
		try { 
			
			if( !testCase.disabled ) { 
				testCase.disabled = !(testCase.condition == null || (testCase.condition != null && testCase.condition.evaluate())); 
			}
					
			if( !testCase. disabled ) { 
				
				// 1. test configuration 
				testCase.prepareData();
				
				// 2. run the test 
				testCase.run();
				
				// 3. validate results 
				testCase.verify();
				
				// 4. set the final status
				testCase.result().status = TestStatus.PASSED;
				
			}
			else { 
				testCase.result().status = TestStatus.SKIPPED;
			}
			
		}
		catch( Throwable e ) { 

			TestResult result = testCase.result();
			if( e instanceof AssertionFailed ) { 
				result.failure = (AssertionFailed) e;
				result.cause = result.failure.getCause();
			}
			else { 
				result.cause = e;
			}

			/* 
			 * program terminted with an exit code different from the expected one (zero by defuault)
			 */
			if( result.cause instanceof ExitFailed ) { 
				result.status = TestStatus.FAILED;
			}
			/* 
			 * test result with an assertion error 
			 */
			else if( result.cause instanceof AssertionFailed ) {
				result.status = TestStatus.FAILED;
			}

			/* 
			 * Timeout exception 
			 */
			else if( result.cause instanceof TimeoutException ) { 
				result.status = TestStatus.TIMEOUT;
			}
			
			/*
			 * Unexpected test error 
			 */
			else { 
				result.status = TestStatus.ERROR;
			}
			
		
		}
		finally {
			report.printResult(testCase.result());
		} 
		
		/* 
		 * 6. clean the result ? 
		 * 
		 */
		deleteResult(testCase.result());
		
		return testCase.result();
	}

	
	void deleteResult(TestResult result) {

		if( config.delete == Delete.never ) { 
			return;
		}
		
		
		if( config.delete == Delete.all || 
			(config.delete == Delete.passed && result.status==TestStatus.PASSED) ||
			(config.delete == Delete.failed && result.status!=TestStatus.PASSED) )  // <-- note: Not PASSED result i.e. include error and timeout result  
		{ 
			FileUtils.deleteQuietly(result.test.runPath);
		}

	}
	
	
	Range rangeFor( TestSuite suite ) { 
		Range range = new Range();
		
		if( config.range == null || config.range.length==0) { 
			range.first = 1;
			range.last = suite.tests.size();
		}
		
		else if( config.range.length==1 ) { 
			/* 
			 * In the first element of the range array there is the test index number to execute
			 * If the index number = -1 => execute the last one 
			 */
			range.first = config.range[0]; 
			if( range.first == -1 )  { 
				range.first = suite.tests.size();
			}
			range.last = range.first;
		}
		else { 
			range.first = config.range[0];
			range.last = Math.min(config.range[1], suite.size());
		}	
		
		return range;
	}
	
	static class Range { 
		int first;
		int last;
	}

}
