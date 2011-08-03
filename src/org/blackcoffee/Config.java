package org.blackcoffee;

import java.io.File;
import java.util.ArrayList;
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
import org.blackcoffee.utils.KeyValue;
import org.blackcoffee.utils.VarHolder;

/**
 * Hold the configuration based on the users specified command line options 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class Config {

	private Options options;
	private CommandLine cmdLine;
	
	List<File> testFiles = new ArrayList<File>();
	File sandboxPath = new File("./sandbox");
	File inputPath = new File(".");
	
	ReportBuilder report; 
	
	enum Delete { passed, failed, never, all };
	
	enum Stop { first, failed, error, never  }
	
	Delete  delete;
	
	Stop stop;
	
	int[] range;
	
	boolean reportStdErr = true;
	boolean reportStdOut = false;
	
	VarHolder vars = new VarHolder();
	
	private Config() {} 
	
	public static Config parse( String[] args ) { 
		
		Config result = new Config();
		
		
		/*
		 * Create the options
		 */
		result. options = new Options()
			.addOption("i", true, "Input directory, the folder which content will be used as input data")
			.addOption("o", true, "Print the report to the specified outfile (use '.html' suffix to create a HTML report)")
			.addOption("d", true, "Delete results (passed|failed|all|never)")
			.addOption("s", true, "Sandbox directory, the folder temporary files will be created")
			.addOption("S", true, "Stop condition (first|failed|error|never)")
			.addOption("V", true, "Define a test variable using the syntax key=value")
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
	
	List<File> getTestFile( File path ) { 
		List<File> result = new ArrayList<File>();
		
		if( path .isFile() ) { 
			result.add(path);
			return result;
		}
		
		if( path.isDirectory() && path.exists() ) { 
			File[] list = path.listFiles();
			for( File file : list ) { 
				if( file.isFile() && (file.getName().endsWith(".testcase") || file.getName().endsWith(".testsuite")))  { 
					result.add(file);
				}
			}
		}
		
		return result;
	}
	
	
	public Config initiliaze( ) { 
		
		String args[] = cmdLine.getArgs();
		

		/*
		 * If any files is specified use the current path 
		 */
		if( args == null || args.length == 0 ) { 

			testFiles.addAll( getTestFile(new File(".")) );
			
			if( testFiles.isEmpty() ) { 
				BlackCoffee.printHelp();
				System.exit(0);
			}
		
		}
		/*
		 * 1. detect the input test case 
		 */
		else { 
			testFiles.addAll( getTestFile(new File(args[0])) );
		}


		/*
		 * check that the files exisst 
		 */
		for( File file : testFiles ) { 
			if( !file.exists() ) { 
				System.err.printf("Missing test configuration file: %s\n", testFiles);
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
			if( (p=sValue.indexOf("-")) != -1 ) { 
				sNum1 = sValue.substring(0,p);
				sNum2 = sValue.substring(p+1);
				if( StringUtils.isEmpty(sNum1) ) { sNum1 = "1"; } 
				if( StringUtils.isEmpty(sNum2) ) { sNum2 = String.valueOf(Integer.MAX_VALUE); } 
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
		 * input data path 
		 */
		if( cmdLine .hasOption("i") ) { 
			inputPath = new File(cmdLine.getOptionValue("i"));
			if( !inputPath.exists() ) { 
				System.err.printf("The specified input directory does not exists: %s\n", inputPath);
				System.exit(1);
			}
		}
		
		if( cmdLine .hasOption("s") ) { 
			sandboxPath = new File(cmdLine.getOptionValue("s"));
			if( !sandboxPath.exists() && !sandboxPath.mkdirs() ) { 
				System.err.printf("Cannot create sandbox path: %s\n", sandboxPath);
				System.exit(1);
			}
			
			if( sandboxPath.isFile() ) { 
				System.err.printf("Cannot use a file as sandbox path. You should specify a directory path instead of: %s\n", sandboxPath);
				System.exit(1);
			}
		}		
		
		
		/*
		 * detect the output mode (-o) option
		 */	
		if( cmdLine.hasOption("o") ) { 
			File outFile = new File(cmdLine.getOptionValue("o"));
			
			if( outFile.isDirectory() ) { 
				System.err.printf("Cannot use a directory as output file: %s\n", outFile);
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
		if( cmdLine.hasOption("S") ) { 
			String val = cmdLine.getOptionValue("S").toLowerCase();
			try { 
				stop = Stop.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				System.err.printf("Invalid value for option -s (%s)\n", val);
				System.exit(1);
			}
		}
		
		if( cmdLine.hasOption("V") ) { 
			String[] vars = cmdLine.getOptionValues("V");
			for( String item : vars ) { 
				KeyValue pair = KeyValue.parse(item);
				if( pair != null ) { 
					this.vars.put( pair.key , pair.value );
				}
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
