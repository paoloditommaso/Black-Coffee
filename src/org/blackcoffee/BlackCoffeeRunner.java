package org.blackcoffee;

import java.io.File;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.blackcoffee.Config.Delete;
import org.blackcoffee.Config.Stop;
import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.report.ReportBuilder;

/**
 * The main starter class 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class BlackCoffeeRunner  {
	
	final Config config;
	
	final ReportBuilder report;
	
	public BlackCoffeeRunner(Config config) {
		this.config = config;
		this.report = config.report;
	}


	public void execute() { 
		
		report.begin();

		try { 
			for ( File file : config.testFiles ) { 
				execute( file );
			}
		}
		finally { 
			report.end();
		}
		
	}
	
	/**
	 * Execute all teh tests defined as subdirectory in the current path
	 */
	void execute(File testFile) {

		report.printHeader( "Test: " + testFile.getAbsolutePath() );
		
		// 1. read the test suite 
		TestSuite suite = new TestSuiteReader().read(testFile);
		suite.testFile = testFile;
		suite.configure(config);
		
		// 2. compile the assertions 
		suite.compile();
		
		try { 
			// 4. run the tests 
			
			int count = 0;
			
			int since;
			int until;
			
			
			if( config.range == null || config.range.length==0) { 
				since = 1;
				until = suite.tests.size();
			}
			
			else if( config.range.length==1 ) { 
				/* 
				 * In the first element of the range array there is the test index number to execute
				 * If the index number = -1 => execute the last one 
				 */
				since = config.range[0]; 
				if( since == -1 )  { 
					since = suite.tests.size();
				}
				until = since;
			}
			else { 
				since = config.range[0];
				until = Math.min(config.range[1], suite.size());
			}
			
			for( int index=since; index <= until; index++ ) {
				count++;
				TestCase test = suite.tests.get(index);

				/* run the test */
				TestResult result = execute(test);

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
		finally { 
			report.end();
		}
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
			if( !testCase. disabled ) { 
				// 1. create the sandbox directory 
				testCase.runPath = getUniquePath();
				
				// 2. test configuration 
				testCase.prepare(); 
				
				// 3. run the test 
				testCase.run();
				
				// 4. validate results 
				testCase.verify();
				
				// 5. set the final status
				testCase.result().status = TestStatus.PASSED;
				
			}
			else { 
				testCase.result().status = TestStatus.DISABLED;
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
			
			if( result.cause instanceof AssertionFailed ) {
				// assertion failure 
				result.status = TestStatus.FAILED;
			}
			else if( result.cause instanceof TimeoutException ) { 
				// general error 
				result.status = TestStatus.TIMEOUT;
			}
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

	private void deleteResult(TestResult result) {

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



	/**
	 * Creare a unique path 
	 * 
	 * @return
	 */
	File getUniquePath() { 
		
		File path;
		do { 
			Double d = Math.random();
			path = new File(config.sandboxPath, Integer.toHexString(d.hashCode()));
			if( path.exists() ) { 
				// try again ..
				continue;
			}
			
			if( !path.mkdirs() ) { 
				throw new BlackCoffeeException("Unable to create path: %s", path);
			}
			
			break;
		}
		while(true);
		
		return path;
	}


	

}
