package org.blackcoffee;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.blackcoffee.report.ConsoleReport;
import org.blackcoffee.report.HtmlReport;
import org.blackcoffee.report.ReportBuilder;
import org.blackcoffee.report.TextReport;

/**
 * Hold the configuration based on the users specified command line options 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class Config {

	private Options options;
	private CommandLine cmdLine;
	
	File testRoot;
	File testFile;
	File sandboxRoot = new File("./sandbox");

	ReportBuilder report; 
	
	enum Delete { passed, failed, never, all };
	
	enum Stop { first, failed, error, never  }
	
	Delete  delete;
	
	Stop stop;
	
	int[] range;
	
	boolean reportStdErr = true;
	boolean reportStdOut = false;
	
	List<KeyValue> environment = new ArrayList<KeyValue>();
	
	private Config() {} 
	
	public static Config parse( String[] args ) { 
		
		Config result = new Config();
		
		result.environment.addAll( getTCoffeeEnvForPath("/Users/ptommaso/tcoffee/r994/") );
		
		
		/*
		 * Create the options
		 */
		result. options = new Options()
			.addOption("o", true, "Print the report to the specified outfile (use '.html' suffix to create a HTML report)")
			.addOption("d", true, "Delete results (passed|failed|all|never)")
			.addOption("s", true, "Stop condition (first|failed|error|never)")
			;
		
	
		/*
		 * Parse the program arguments
		 */
		result. cmdLine=null;
		try {
			CommandLineParser parser = new PosixParser();
			result.cmdLine = parser.parse( result.options, args);
		} 
		catch (ParseException e) {
			System.err.print(e.getMessage());
			System.err.printf(". See %s -h for help.", BlackCoffee.APPNAME);
			System.exit(1);
		}		
		
		return result;
	}
	
	private static Collection<? extends KeyValue> getTCoffeeEnvForPath( String sBasePath ) {

		File root = new File(sBasePath);
		List<KeyValue> env = new ArrayList<KeyValue>();
		env.add( new KeyValue("DIR_4_TCOFFEE", root.getAbsolutePath() ) );
		env.add( new KeyValue("MAFFT_BINARIES", new File(root,"plugins/macosx/").getAbsolutePath() ) );
		env.add( new KeyValue("PERL5LIB", new File(root,"perl") .getAbsolutePath() ) );
		env.add( new KeyValue("DYLD_LIBRARY_PATH", "$DYLD_LIBRARY_PATH:" + new File(root,"gfortran").getAbsolutePath() ) );
		env.add( new KeyValue("TMP_4_TCOFFEE", new File(root,".tmp") .getAbsolutePath() ) );
		env.add( new KeyValue("LOCKDIR_4_TCOFFEE", new File(root,".lck") .getAbsolutePath() ) );
		env.add( new KeyValue("CACHE_4_TCOFFEE", new File(root,".cache" ).getAbsolutePath() ) );
		env.add( new KeyValue("EMAIL_4_TCOFFEE", "paolo.ditommaso@gmail.com") );
		env.add( new KeyValue("PATH", new File(root,"bin").getAbsolutePath() + ":$PATH" ) );
		
		return env;
	}

	public Config initiliaze( ) { 
		
		String args[] = cmdLine.getArgs();
		

		/*
		 * The first argument is mandatory 
		 */
		if( args == null || args.length == 0 ) { 
			testRoot = new File(".");
			testFile = new File(BlackCoffee.TEST_CASE_FILE_NAME);
			if( !testFile.exists() ) { 
				BlackCoffee.printHelp();
				System.exit(0);
			}
		
		}
		
		/*
		 * 1. detect the input test case 
		 */
		File path = new File(args[0]);
		if( path .exists() && path.isFile() ) { 
			testFile = path;
			testRoot = path.getParentFile();
		}
		else if( path.exists() && path.isDirectory() ) { 
			testRoot = path;
			testFile = new File(path, BlackCoffee.TEST_CASE_FILE_NAME);
			if( !testFile.exists() ) { 
				System.err.printf("Missing test configuration file: %s\n", testFile);
				System.exit(1);
			}
		}
		
		/*
		 * 2. the second option (not-mandatory) the test number or range 
		 */
		
		if( args != null && args.length>1 ) { 
			String sValue = args[1];
			String sNum1=null;
			String sNum2=null;
			int p;
			if( (p=sValue.indexOf(":")) != -1 ) { 
				sNum1 = sValue.substring(0,p);
				sNum2 = sValue.substring(p+1);
				if( StringUtils.isEmpty(sNum1) ) { sNum1 = "1"; } 
				if( StringUtils.isEmpty(sNum2) ) { sNum2 = "-1"; } 
			}
			else { 
				sNum1 = sValue;
			}
			
			if( NumberUtils.isNumber(sNum1) && NumberUtils.isNumber(sNum2) ) { 
				this.range = new int[2];
				range[0] = Integer.parseInt(sNum1);
				range[1] = Integer.parseInt(sNum2);
			}
			else if( NumberUtils.isNumber(sNum1) ){ 
				range = new int[1];
				range[0] = Integer.parseInt(sNum1);
			}
			else if( "last".equals(sNum1) ) {
				range = new int[1];
				range[0] = -1;
			}
			else { 
				System.err.printf("Invalid test number option (%s). It have to be an integer value or the a range in format n:m \n", sValue);
				System.exit(1);
			}
		}
		
		
		
		/*
		 * detect the output mode (-o) option
		 */	
		if( cmdLine.hasOption("o") ) { 
			File outFile = new File(cmdLine.getOptionValue("o"));
			
			if( outFile.isDirectory() ) { 
				System.err.printf("Cannot use a directory a output file: %s\n", outFile);
				System.exit(1);
			}
			
			report = ( outFile.getName().toLowerCase().endsWith(".html") ) 
				? new HtmlReport(outFile,this)
				: new TextReport(outFile,this);
		}
		else { 
			report = new ConsoleReport(this);
		}		
		
		/*
		 * Delete result option (-d): 
		 *   passed: (default) delete results for passed tests, keep the ones that have errors 
		 *   failed: delete result for tests with errors, keep the other 
		 *   none: do not delete nothing
		 *   all: delete all results 
		 */
		delete = Delete.passed;
		if( cmdLine.hasOption("d") ) { 
			String val = cmdLine.getOptionValue("d").toLowerCase();
			try { 
				delete = Delete.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				System.err.printf("Invalid value for option -d (%s)\n", val);
				System.exit(1);
			}
		}
		
		/*
		 * Stop on option (-s)
		 *   passed: stop on first test successfull 
		 *   failed: (default) stop on first test reporting a failure   
		 *   error: stop on first error 
		 *   none: never stop 
		 */
		stop = Stop.failed;
		if( cmdLine.hasOption("s") ) { 
			String val = cmdLine.getOptionValue("s").toLowerCase();
			try { 
				stop = Stop.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				System.err.printf("Invalid value for option -s (%s)\n", val);
				System.exit(1);
			}
		}
		
		
		/*
		 * Valgrind
		 */
		//TODO
		
		return this;
	}
	
	public boolean reportStdOut() {  return reportStdOut; } 

	public boolean reportStdErr() {  return reportStdErr; } 
}
