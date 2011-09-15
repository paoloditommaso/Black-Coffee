package org.blackcoffee;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.blackcoffee.report.CompositeReport;
import org.blackcoffee.report.ConsoleReport;
import org.blackcoffee.report.HtmlReport;
import org.blackcoffee.report.ReportBuilder;
import org.blackcoffee.report.TextReport;
import org.blackcoffee.utils.KeyValue;
import org.blackcoffee.utils.PathUtils;
import org.blackcoffee.utils.VarHolder;

/**
 * Hold the configuration based on the users specified command line options 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class Config {

	public enum Delete { passed, failed, never, all };
	
	public enum Stop { failed, error, never  }
	
	public enum Print { onerror, never, always }
	
	private Options options;
	private CommandLine cmdLine;
	
	private PathUtils pathUtils = new PathUtils();
	
	/** the list of testcase file to process */
	public List<File> testFiles = new ArrayList<File>();
	
	/** the directory that will contain the result files */
	public File sandboxPath = pathUtils.absolute("./sandbox");
	
	/** the direcory that contains the input files */
	public File inputPath;
	
	/** the file that contains the configuration */
	public File confFile; 
	
	public ReportBuilder report; 
	
	public Delete  delete;
	
	public Stop stop;
	
	public int[] range;
	
	public Print reportStdErr = Print.never;
	
	public Print reportStdOut = Print.never;
	
	public Print reportValgrind = Print.never;
	
	
	public VarHolder vars = new VarHolder();
	
	public List<String> tags ;
	
	public StringWriter errorString = new StringWriter(); 
	
	public PrintWriter error = new PrintWriter(errorString);
	
	public Integer exit;
	
	public String htmlPathPrefix;	
	
	public boolean valgrind = false;
	
	public String valgrindOptions;
	
	public boolean recurseDir;
	
	private Config() {} 
	
	/**
	 * Parse the program arguments creating the app configuration options 
	 * 
	 * @param args the arrays of argument as provided by the Java main method
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Config parse( String... args ) { 
		
		Config result = new Config();
		
		
		/*
		 * Create the options
		 */
		result. options = new Options()
			.addOption("i", "input-dir", true, "Input directory, the folder which content will be used as input data")
			.addOption("o", "output-file", true, "Print the report to the specified outfile (use '.html' suffix to create a HTML report)")
			.addOption("d", "delete", true, "Delete results (passed|failed|all|never)")
			.addOption("s", "stop", true, "Stop condition (failed|error|never)")
			.addOption("V", "var", true, "Define a test variable using the syntax key=value")
			.addOption("h", "help", false, "Print the command line help")
			.addOption("r", "range", true, "Run only the test in the specified range (n:m)")
			.addOption("c", "config-file", true, "Specify the configuration file to load")
			.addOption("R", false, "Recurse subdirectory containing tests")
			
			.addOption( OptionBuilder
						.withLongOpt("tag")
						.hasArgs()
						.withDescription("Run only the test with the specified tag (one or more)")
						.withValueSeparator(',')
						.create("t")
					)	
			.addOption( OptionBuilder
						.withLongOpt("print-stdout")
						.hasArg()
						.withDescription("Print out the test stdour. Valid options: onerror|alwyas|never")
						.create()
					)

			.addOption( OptionBuilder
					.withLongOpt("print-stderr")
					.hasArg()
					.withDescription("Print the test stderr. Valid options: onerror|alwyas|never")
					.create()
				)

		
			.addOption( OptionBuilder
					.withLongOpt("print-valgrind")
					.hasArg()
					.withDescription("Print the valgrind report. Valid options: onerror|alwyas|never")
					.create()
				)		
			.addOption( OptionBuilder
					.withLongOpt("sandbox-dir")
					.hasArg()
					.withDescription("Sandbox directory, the folder temporary files will be created")
					.create()
					) 

			.addOption( OptionBuilder
					.withLongOpt("html-path-prefix")
					.hasArg()
					.withDescription("The path to prepend to result link")
					.create()
					) 
					
			.addOption( OptionBuilder
					.withLongOpt("valgrind")
					.hasOptionalArg()
					.withDescription("Run the test command using Valgrind (yes|no|command line options). Default 'no'")
					.create()
					
					)
					;
		
	
		/*
		 * Parse the program arguments
		 */
		result. cmdLine=null;
		try {
			CommandLineParser parser = new GnuParser();
			result.cmdLine = parser.parse( result.options, args);
		} 
		catch (ParseException e) {
			result.error.print(e.getMessage());
			result.error.printf(". See %s -h for help.", BlackCoffee.APPNAME);
			result.exit = 1;
		}		
		
		return result;
	}
	
	/**
	 * Look for test files (.testcase | .testsuite) on the specified path with the followinh strategy: 
	 * If the path is a file return that file 
	 * If the file is a directory return all files in that directory named '.testcase' or '.testsuite'
	 * 
	 * @param path
	 * @return
	 */
	List<File> getTestFile( File path ) { 
		List<File> result = new ArrayList<File>();
		
		if( path .isFile() ) { 
			result.add(path);
			return result;
		}
		
		if( path.isDirectory() && path.exists() ) { 
			File[] list = path.listFiles();

			if( list != null ) for( File file : list ) { 
				if( file.isFile() && (file.getName().endsWith(".testcase") || file.getName().endsWith(".testsuite")))  { 
					result.add(file);
				}
			}
			
			if(result.isEmpty() && recurseDir) { 
				// try descend subdirectory 
				if( list != null ) for( File file : list ) { 
					if( file.isDirectory() ) { 
						result.addAll( getTestFile(file) );
					}
				}
			}
		}
		
		return result;
	}
	
	Config exit( int exitcode ) { 
		exit = exitcode;
		return this;
	}
	
	/**
	 * Initialize the config object using the parsed data 
	 * 
	 * @return the object itself 
	 */
	public Config initiliaze( ) { 
		
		if( exit != null ) return this;
		
		if( cmdLine.hasOption("h") ) { 
			printUsage();
			return exit(0);
		}
		
		/*
		 * recurse subdisrectory
		 */
		recurseDir = cmdLine.hasOption('R');

		/*
		 * If any files is specified use the current path 
		 */
		final String[] args = cmdLine.getArgs();
		
		if( args == null || args.length == 0 ) { 
			//  by default look into the current dir  
			testFiles.addAll(getTestFile( pathUtils.absolute(".") )); 
		} 
		else for( String file : args ){
			// get all the test files 
			testFiles.addAll(getTestFile( pathUtils.absolute(file) )); 
		}

		/*
		 * config file 
		 */
		if( cmdLine .hasOption("c") ) { 
			confFile = pathUtils.absolute(cmdLine.getOptionValue("c"));
			if( !confFile.exists() ) { 
				error.printf("The specified configuration file does not exists: %s\n", confFile);
				return exit(1);
			}
		}		
		
		/*
		 * (not-mandatory) the test number or range 
		 */
		if( cmdLine.hasOption('r') ) { 
			
			String sValue = cmdLine.getOptionValue('r');
			String sNum1=null;
			String sNum2=null;
			int p;
			if( (p=sValue.indexOf(":")) != -1 ) { 
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
				error.printf("Invalid test number option (%s). It have to be an integer value or the a range in format n:m \n", sValue);
				return exit(1);
			}
		}
		
		/*
		 * input data path 
		 */
		if( cmdLine .hasOption("i") ) { 
			inputPath = pathUtils.absolute(cmdLine.getOptionValue("i"));
			if( !inputPath.exists() ) { 
				error.printf("The specified input directory does not exists: %s\n", inputPath);
				return exit(1);
			}
		}
		
		/*
		 * sandbox path 
		 */
		if( cmdLine .hasOption("sandbox-dir") ) { 
			sandboxPath =  pathUtils.absolute(cmdLine.getOptionValue("sandbox-dir")) ;
			if( !sandboxPath.exists() && !sandboxPath.mkdirs() ) { 
				error.printf("Cannot create sandbox path: %s\n", sandboxPath);
				return exit(1);
			}
			
			if( sandboxPath.isFile() ) { 
				error.printf("Cannot use a file as sandbox path. You should specify a directory path instead of: %s\n", sandboxPath);
				return exit(1);
			}
		}		
		
		
		/*
		 * detect the output mode (-o) option
		 */	
		report = new ConsoleReport(this); // <-- by defualt output to console
		
		if( cmdLine.hasOption("o") ) { 
			File outFile = pathUtils.absolute(cmdLine.getOptionValue("o"));
			
			if( outFile.isDirectory() ) { 
				error.printf("Cannot use a directory as output file: %s\n", outFile);
				return exit(1);
			}
			
			ReportBuilder outReport = ( outFile.getName().toLowerCase().endsWith(".html") ) 
					? new HtmlReport(outFile,this)
					: new TextReport(outFile,this);
					
			report = new CompositeReport(outReport, report);
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
				error.printf("Invalid value for option -d (%s)\n", val);
				return exit(1);
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
		if( cmdLine.hasOption("stop") ) { 
			String val = cmdLine.getOptionValue("stop").toLowerCase();
			try { 
				stop = Stop.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				error.printf("Invalid value for option -s (%s)\n", val);
				return exit(1);
			}
		}
		
		/*
		 * add variable 
		 */
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
		 * tag option
		 */
		if( cmdLine.hasOption("t")) { 
			tags = new ArrayList<String>( Arrays.asList(cmdLine.getOptionValues('t')) );
		}
 		
		
		/*
		 * print-stdout
		 */
		if( cmdLine .hasOption("print-stdout") ) { 
			String val = cmdLine.getOptionValue("print-stdout").toLowerCase();
			try { 
				reportStdOut = Print.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				error.printf("Invalid value for option --print-stdout (%s)\n", val);
				return exit(1);
			}			
		}

		/*
		 * print-stderr
		 */
		if( cmdLine .hasOption("print-stderr") ) { 
			String val = cmdLine.getOptionValue("print-stderr").toLowerCase();
			try { 
				reportStdErr = Print.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				error.printf("Invalid value for option --print-stderr (%s)\n", val);
				return exit(1);
			}			
		}
		
		/*
		 * print-stderr
		 */
		if( cmdLine .hasOption("print-valgrind") ) { 
			String val = cmdLine.getOptionValue("print-valgrind").toLowerCase();
			try { 
				reportValgrind = Print.valueOf(val);
			}
			catch( IllegalArgumentException e ) { 
				error.printf("Invalid value for option --print-valgrind (%s)\n", val);
				return exit(1);
			}			
		}		
		
		if( cmdLine.hasOption("html-path-prefix") ) { 
			this.htmlPathPrefix = cmdLine.getOptionValue("html-path-prefix");
		}
		
		/*
		 * Valgrind
		 */
		String sValgrind = cmdLine.getOptionValue("valgrind");
		if( cmdLine.hasOption("valgrind") && !"no".equalsIgnoreCase(sValgrind) && !"false".equals(sValgrind)) { 
			this.valgrind = true;
			if( !"yes".equalsIgnoreCase(sValgrind) && !"true".equals(sValgrind) ) { 
				this.valgrindOptions = sValgrind;
			}
		}

		
		return this;
	}
	
	void printUsage() {
		String syntax = String.format("%s [options] [path]\n\n", BlackCoffee.APPNAME);
		String message = String.format(
				"Tests have to be defined in the specified 'path'. " +
				"If the specified path is a directory, it have to contain a test definition file named '%s'. " +
				"If the path is omitted will be used the current directory by default." +
				"The options available are:",
				BlackCoffee.TEST_CASE_FILE_NAME);
		
		HelpFormatter helpFormatter = new HelpFormatter( );
	    helpFormatter.setWidth( 120 );
	    helpFormatter.printHelp( error, 120, syntax, message, options, 2, 2, "" );
	}


}
