package org.blackcoffee.report;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.blackcoffee.BlackCoffee;
import org.blackcoffee.Config;
import org.blackcoffee.TestCase;
import org.blackcoffee.TestResult;
import org.blackcoffee.TestStatus;

public class TextReport extends ReportBuilder {

	public TextReport(File out, Config config) {
		super(newFileStream(out), config);
	}
	
	public TextReport( OutputStream out, Config config) { 
		super(out, config);
	}

	@Override
	public void begin() {
		out.println( BlackCoffee.LOGO );
	}

	@Override
	public void end() {

	}

	@Override
	public void printHeader() {

	}

	@Override
	public void printTest(TestCase test) {
		
		int MAX = 60;
		
		String label = test.label;
		if( label == null ) label = test.command;

		label = StringUtils.left(label, MAX-3);
		label += " ..";
		
		out.printf("~ (%s) ", test.index);
		out.print( StringUtils.rightPad( label, MAX, "." ) );

	}

	@Override
	public void printResult(TestResult result) {

		out.print( StringUtils.leftPad( asDuration(result.elapsed) , 8) );
		out.print( " " );
		out.print( StringUtils.rightPad(result.status.toString(), 10));
		out.println();
		
		if( result.status != TestStatus.PASSED ) { 
			String sPath;
			try {
				sPath = result.path.getCanonicalPath();
			} catch (IOException e) {
				sPath = result.path.getAbsolutePath() ;
			} 

			if( result.failure != null && result.failure.assertion != null ) { 
				out.printf( "  assertion: %s\n", result.failure.assertion.declaration );
			}

			if( result.failure != null && result.failure!=null && result.failure.assertion.message != null ) { 
				out.printf( "  message: %s\n", result.failure.assertion.message );
			}
			
			if( result.failure != null ) { 
				out.printf( "  line   : %s\n", result.failure.assertion.line );
			}
	
			if( result.cause!=null  ) { 
				out.printf( "  cause  : %s\n", result.cause.getMessage() != null ? result.cause.getMessage() : result.cause.toString() ); 
			}
			
			out.printf( "  result : %s\n", sPath );
			
			File file;
			if( config.reportStdOut() && (file=new File(result.path,".stdout")).exists() ) { 
				out.printf( "  stdout : \n%s\n", readFileToString(file) );
				
			}
			
			if( config.reportStdErr() && (file=new File(result.path,".stderr")).exists() ) { 
				out.printf( "  stderr : \n%s\n", readFileToString(file) );
			}
			
			
			out.println();
		}

	}

	private Object readFileToString(File file) {
		try { 
			return FileUtils.readFileToString(file);
		}
		catch( IOException e ) { 
			return String.format("<<cannot read %s>>", file);
		}
	}

	@Override
	public void print(String string) {
		out.print("~ ");
		out.println(string);
	}

}
