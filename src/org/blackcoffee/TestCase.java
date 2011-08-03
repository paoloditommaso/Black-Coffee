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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.blackcoffee.command.Command;
import org.blackcoffee.command.TcoffeeCommand;
import org.blackcoffee.commons.utils.Duration;
import org.blackcoffee.exception.ExitFailed;
import org.blackcoffee.utils.QuoteStringTokenizer;
import org.blackcoffee.utils.VarHolder;

/**
 * Defines a test case i.e. a test rule, plus a list of assertion 
 *
 */
public class TestCase { 
	
	public String label;
	public Duration timeout = Duration.parse("60s");
	public List<String> exports = new ArrayList<String>();
	public Command command; 
	public List<TestAssertion> assertions = new ArrayList<TestAssertion>();
	public VarHolder variables = new VarHolder();
	public List<String> input = null;
	public List<String> output = null;

	/** The folder where the test is executed */
	public File runPath;
	
	/** The folder which contain the input data */
	public File inputPath;		
	
	/** The exptected exit code */
	public Integer exit = 0;

	
	public List<Command> before;
	public List<Command> after;
	public boolean disabled;
	
	TestResult result;
	
	/** Progressive index number starting from 1 */
	public int index;
	
	/** The line count where the test is defined in the file */
	public int line;
	
	public TestCase( String test ) { 
		this.command = test.startsWith("t_coffee") 
		             ? new TcoffeeCommand(test.substring(8).trim())
					 : new Command(test);
	}
	
	public void addAssertion( String cond, int line, VarHolder variables ) { 
		
		TestAssertion item = new TestAssertion();
		item.line = line;
		item.variables = new VarHolder(variables);
	
		int p = cond.indexOf("#");
		if( p != -1 ) { 
			item. message = cond.substring(p+1) .replaceAll("^\\s*", "");
			item.declaration = cond.substring(0,p);
		}
		else { 
			item.declaration = cond;
		}
		
		assertions.add(item);
	}
	
	public String toString() { 
		return new StringBuilder() 
			.append("TestCase[ \n") 
			.append("env: " ) .append(exports) . append(",\n")
			.append("test: " ) .append(command) .append(",\n")
			.append("assert: ") .append(assertions) .append(",\n")
			.append("variables: ") .append(variables) .append("\n")
			.append("]") 
			.toString();
	}

	/**
	 * Compile the assertions predicates in this test case 
	 */
	public void compileAssertions() {
		Class<?> lastResultType = null;
		for( TestAssertion assertion : assertions ) { 
			assertion.compile( lastResultType );
			lastResultType  = assertion.predicate.lastResultType;
		}
	}

	final public void prepare() { 
		
		result = result();
		
		// 1. define the run.path variable
		variables .put("run.path", runPath.getAbsolutePath());
		
		// 2. configure the 'test' command
		command.configure(this);

		// 3. configure the 'before' commands
		if( before != null ) for( Command cmd : before ) { 
			cmd.configure(this);
		}

		// 4. configure the after commands 
		if( after != null ) for( Command cmd : after ) { 
			cmd.configure(this);
		}
		
		// 5. copy the inputt files 
		try {
			copyInputData();
		} 
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Verify that all assertions are satisfied 
	 * 
	 * @param path directory that define the execution context for this test i.e. usually the directory 
	 * that contains all the files required/produced by the command under tests 
	 */
	public void verify() {
		
		Object previousAssertResult=null;
		for( TestAssertion assertion : assertions ) { 
			
			/* 
			 * verify the assertion 
			 */
			Object value = assertion.verify(runPath, previousAssertResult);
			
			/* 
			 * save the assertion result value in the context, 
			 * so that can be reused if required 
			 */
			if( !assertion.predicate.root.isContinuation() ) { 
				previousAssertResult = value;
			}
		}
	}

	/**
	 * Invoke to execute the test 
	 * 
	 * @throws ExecuteException
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void run() throws ExecuteException, IOException, TimeoutException {

		long start = System.currentTimeMillis();
		try { 
			/* execute the test command */
			result.exitCode = runTest();

			/* verify the returned exit code match the exptectd one */
			if( exit != null && exit != result.exitCode ) { 
				throw new ExitFailed("Test terminated with with exit code: %s (was expected %s)", result.exitCode, exit);
			}
		}
		finally { 
			result.elapsed = System.currentTimeMillis() - start;
		}

	}
	
	int runTest() throws ExecuteException, IOException, TimeoutException {

		/*
		 * define the srdout / stderr streams to save the program output 
		 */
		OutputStream stdout = new BufferedOutputStream ( new FileOutputStream(new File(runPath,".stdout")) );
		OutputStream stderr = new BufferedOutputStream ( new FileOutputStream(new File(runPath,".stderr")) );
		
		try  {
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(runPath);
			executor.setStreamHandler(new PumpStreamHandler(stdout, stderr));
			executor.setWatchdog(new ExecuteWatchdog(this.timeout.millis()));
			
			/* 
			 * define the environment / script file 
			 */
			
			PrintWriter script = new PrintWriter(new FileWriter( new File(runPath, ".run") ));
			if( this.exports != null ) for( String key : this.exports ) { 

				String val = variables .value(key);	
				
				script.append("export ") 
					.append(key)
					.append( "=") 
					.append("\"") .append(val) .println("\"");
		
			}
			
			script.println();
			
			/* the before commands */
			if( before != null ) for( Command cmd : before ){ 
				script.println(cmd.toString());
			}
			
			/* the main test command */
			script.println(command.toString());

			/* after command declaration */
			if( after != null ) for( Command cmd : after ){ 
				script.println(cmd.toString());
			}
	
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
				FileUtils.writeStringToFile(new File(runPath, ".exitcode"), String.valueOf(result));
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
	
	public void addInputFile( String fileName ) { 
		if( fileName == null ) return;
		
		if( input == null ) { 
			input = new ArrayList<String>();
		}

		QuoteStringTokenizer values = new QuoteStringTokenizer(fileName, ' ', ',');
		for( String str : values ) { 
			if( !input.contains(str) ) { 
				input.add(str);
			}
		}
		
	}
	
	public void addBeforeCommand(String cmdline) { 
		if( before == null ) { 
			before = new ArrayList<Command>();
		}
		
		before.add( new Command(cmdline) );
	}
	
	public void addAfterCommand( String cmdline ) { 
		if( after == null ) { 
			after = new ArrayList<Command>();
		}

		after.add( new Command(cmdline) );
	}
	
	public TestResult result() { 
		if( result == null ) { 
			result = new TestResult();
			result.test = this;
		}
		
		return result;
	}
	
	
	/*
	 * copy everything to the target directory where the test will run 
	 */
	void copyInputData() throws IOException {

		/*
		 * when the 'in' attributes is define only the declared files are copied 
		 */
		if( input != null && input.size()>0) { 
			File parent = inputPath.isDirectory() ? inputPath : inputPath.getParentFile();
			for( String sItem : input ) { 
				File fItem = sItem.startsWith("/") ? new File(sItem) : new File(parent, sItem);
				copyFile(fItem, runPath);
			}
			return;
		}
		
		/*
		 * otherwise if is a file copy just that file 
		 */
		if( inputPath.isFile() ) { 
			copyFile(inputPath, runPath);
			return;
		}
		
		/* 
		 * otherwise it is a directory (i hope ..) copy all the content 
		 */
		File[] all = inputPath.listFiles();
		if(all!=null) for( File item : all ) if( !BlackCoffee.TEST_CASE_FILE_NAME.equals(item.getName()) ) { 
			copyFile(item, runPath);
		}
		
	}
	
	/*
	 * copy a single file to the destination folder 
	 */
	void copyFile( File item, File targetPath ) throws IOException { 

		if( !item.exists() ) { 
			result.text.printf("~ Warning: missing input file '%s' \n", item);
		}
		String cmd = String.format("ln -s %s %s", item.getAbsolutePath(), item.getName());
		Runtime.getRuntime().exec(cmd, null, targetPath);
		
	}

}




