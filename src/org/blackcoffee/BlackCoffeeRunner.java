package org.blackcoffee;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.blackcoffee.Config.Delete;
import org.blackcoffee.Config.Stop;
import org.blackcoffee.assertions.AssertionFailed;
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


	/**
	 * Execute all teh tests defined as subdirectory in the current path
	 * 
	 */
	void execute() {

		// 1. read the test suite 
		TestSuite suite = new TestSuiteReader().read(config.testFile);
		
		// 2. compile the assertions 
		suite.compile();
		
		// 3. open the report
		report.begin();
		report.printHeader();
		
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
				TestResult result = execute( config.testRoot, test );

				// check 
				if( config.stop == Stop.never ) { continue; } 
				else if( config.stop == Stop.first ) { break; } 
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
	TestResult execute( File inputPath, TestCase testCase ) { 
		// 1. create the sandbox directory 
		File target = getUniquePath();

		report.printTest(testCase);
		
		TestResult result = new TestResult();
		result.path = target;
		
		try { 
			// 2. copy input files 
			copyInputData( inputPath, target );
			
			long start = System.currentTimeMillis();
			try { 
				result.exitCode = runTest(testCase, target);
			}
			finally { 
				result.elapsed = System.currentTimeMillis() - start;
			}
			
			// 5. validate results 
			testCase.verify(target);

			result.status = TestStatus.PASSED;
			
		}
		catch( Throwable e ) { 

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
			report.printResult(result);
		} 
		
		/* 
		 * 6. clean the result ? 
		 * 
		 */
		deleteResult(result, target);
		
		return result;
	}

	private void deleteResult(TestResult result, File path) {

		if( config.delete == Delete.never ) { 
			return;
		}
		
		
		if( config.delete == Delete.all || 
			(config.delete == Delete.passed && result.status==TestStatus.PASSED) ||
			(config.delete == Delete.failed && result.status!=TestStatus.PASSED) )  // <-- note: Not PASSED result i.e. include error and timeout result  
		{ 
			FileUtils.deleteQuietly(path);
		}

	}


	int runTest( TestCase test, File path ) throws ExecuteException, IOException, TimeoutException {

		
		OutputStream stdout = new BufferedOutputStream ( new FileOutputStream(new File(path,".stdout")) );
		OutputStream stderr = new BufferedOutputStream ( new FileOutputStream(new File(path,".stderr")) );
		
		try  {
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(path);
			executor.setStreamHandler(new PumpStreamHandler(stdout, stderr));
			executor.setWatchdog(new ExecuteWatchdog(test.timeout));
			
			/* 
			 * define the environment / script file 
			 * 1. the 'global' config enviroment 
			 * 2. + add the test level config environment
			 */
			List<KeyValue> env = new ArrayList<KeyValue>( config.environment );
			env.addAll( test.env );
			
			PrintWriter script = new PrintWriter(new FileWriter( new File(path, ".run") ));
			for( KeyValue item : env ) { 
				if( item.value != null ) { 
					script.append("export ") 
						.append(item.key)
						.append( "=") 
						.append("\"") .append(item.value) .println("\"");
				}
				else { 
					script.append("unset ") .append(item.key) .println("");
				}
			}
			
			script.println();
			script.println( test.command );
			IOUtils.closeQuietly(script);
			
			
			CommandLine cmd = CommandLine.parse("bash .run");
			int result = Integer.MAX_VALUE;
			try { 
				 result = executor.execute(cmd);		
			}
			catch( ExecuteException e ) { 
				result = e.getExitValue();
			}
			finally { 
				// save the exitcode 
				FileUtils.writeStringToFile(new File(path, ".exitcode"), String.valueOf(result));
			}
			
			if( executor.getWatchdog().killedProcess() ) { 
				throw new TimeoutException();
			}

			return result;
		}
		finally { 
			IOUtils.closeQuietly(stdout);
			IOUtils.closeQuietly(stderr);
		}
	}

	/*
	 * copy everything to the target directory where the test will run 
	 */
	void copyInputData(File path, File target) throws IOException {

		File[] all = path.listFiles();
		if(all!=null) for( File item : all ) if( !BlackCoffee.TEST_CASE_FILE_NAME.equals(item.getName()) ) { 
		
			String cmd = String.format("ln -s %s %s", item.getAbsolutePath(), item.getName());
			Runtime.getRuntime().exec(cmd, null, target);
			
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
			path = new File(config.sandboxRoot, Integer.toHexString(d.hashCode()));
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
