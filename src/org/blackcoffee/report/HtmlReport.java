package org.blackcoffee.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.blackcoffee.Config;
import org.blackcoffee.TestCase;
import org.blackcoffee.TestResult;
import org.blackcoffee.TestStatus;
import org.blackcoffee.exception.BlackCoffeeException;

public class HtmlReport extends ReportBuilder {

	File file;


	private StrSubstitutor substitutor;


	private HashMap<String, String> variables;

	public HtmlReport(File file, Config config) {
		super(newFileStream(file), config);
		this.file = file;
	}

	public File getFile() { return file; }
	
	@Override
	public void begin() {
		
		Map<String,String> vars = new HashMap<String,String>();
		vars.put("DocumentTitle", "Black-Coffee report");
		vars.put("CSS", getTemplateCss());
		StrSubstitutor strSubstitutor = new StrSubstitutor(vars);
		
		out.print( strSubstitutor.replace(HEAD) );
		out.printf("<body>\n");
		out.printf("<h1>Tests result</h1>\n");
	}

	@Override
	public void end() {
		out.println("</body>");
		out.println("</html>");
		
	}

	@Override
	public void group(String header) {
		out.printf("<h2>%s</h2>\n", StringUtils.isNotBlank(header) ? header : "Test group");
		out.println("<ul>");
	}
	
	public void groupEnd() {
		out.println("</ul>");
	}
	
	

	@Override
	public void test( TestCase test ) {

		/*
		 * compose the final test element html fragment
		 */
		variables = new HashMap<String, String>();
		substitutor = new StrSubstitutor(variables);

		variables.put("Status", ""); // <-- empty by defualt
		
		// the main row label 
		String label = StringUtils.isNotEmpty(test.label) ? test.label : test.command.toString() ;
		label = String.format("(%s) %s", test.index, label);
		variables.put("Label", label);
		
	}

	@Override
	public void testEnd(TestResult result) {
	
		StringBuilder html = new StringBuilder();
		try { 
			safeTestEnd(result, html);
		}
		catch( Exception e ) { 
			html.append(row("Report Error", "<div class='sourceLine'><span>" + ExceptionUtils.getFullStackTrace(e) + "</span></div>\n" ));
		}
		finally { 
			/* 
			 * define the status class 
			 */
			if( result == null || result.status.notPassed() ) { 
				variables.put("Status", "failed");
			}
			else if( result != null && result.status == TestStatus.PASSED ){ 
				variables.put("Status", "passed");
			}
			
			/* 
			 * add the html fargment
			 */
			variables .put("Result", html.toString());			
		}
		
		/* replace the variables */
		out.println(substitutor.replace(TEST_ITEM));
	}
	
	
	private void safeTestEnd(TestResult result, StringBuilder html) {
		
		if( result.path() != null ) { 
			html.append( row("ID", result.path().getName() ) );
		}

		html.append( row("Status", result.status.toString() ) );
		html.append( row("Time",  TestStatus.SKIPPED.equals(result.status) ? "-" : asDuration(result.elapsed)) );
		html.append( row("Line",  result.test.line) );

		/*
		 * the test command 
		 */
		html.append(row("Command", result.test.command.toString() ));
				
		/* 
		 * condition 
		 */
		if( result.test.condition != null ) { 
			html.append(row("If", result.test.condition.declaration ));
		}
		
		/* 
		 * the path containing the result 
		 */
		if( result.path() != null ) { 
			String path = config.sandboxPath.getAbsolutePath();
			if( StringUtils.isNotEmpty(config.htmlPathPrefix)) {
				path = config.htmlPathPrefix;
			}
			if( !path.endsWith("/") ) { 
				path += "/";
			}
			path += result.path().getName();
			String link = String.format("<a href='%s' style='text-decoration: underline;'>%s</a>", path, "Click to view result files" );
			html.append(row("Result", link ));
		}


		/* 
		 * test errors 
		 */
		if( TestStatus.FAILED.equals(result.status) || TestStatus.ERROR.equals(result.status) || TestStatus.TIMEOUT.equals(result.status) ) { 
			
			if( result.failure != null && result.failure.assertion != null ) { 
				html.append(row("Assertion", result.failure.assertion.declaration ));
			}

			if( result.failure != null && result.failure!=null && result.failure.assertion.message != null ) { 
				html.append(row("Message", result.failure.assertion.message ));
			}
			
			if( result.cause!=null && result.cause instanceof BlackCoffeeException ) { 
				html.append(row("Cause", result.cause.getMessage() != null ? result.cause.getMessage() : result.cause.toString() )); 
			}
			else if( result.cause!=null ) { 
				html.append(row("Cause", ExceptionUtils.getFullStackTrace(result.cause) )); 
			}
			
			/*
			 * std out content 
			 */
			File file = new File(result.path(),".stdout");
			String content = "(missing)";
			if( file.exists() ) {
				content = readFileToString(file);
				if( StringUtils.isBlank(content) ) { 
					content = "(empty)";
				}
				html.append(row("Std-Out", "<div class='sourceLine'><span>" + content + "</span></div>\n" ));
			}
			
			/*
			 * std err content 
			 */
			file = new File(result.path(),".stderr");
			if( file.exists() ) {
				content = readFileToString(file);
				if( StringUtils.isBlank(content) ) { 
					content = "(empty)";
				}
				html.append(row("Std-Err", "<div class='sourceLine'><span>" + readFileToString(file) + "</span></div>\n" ));
			}
			
			/*
			 * valgrind report 
			 */

			file = new File(result.path(),".valgrind.log");
			if( file.exists() ) {
				content = readFileToString(file);
				if( StringUtils.isBlank(content) ) { 
					content = "(empty)";
				}
				html.append(row("Valgrind", "<div class='sourceLine'><span>" + readFileToString(file) + "</span></div>\n" ));
			}	
		}	


	}

	String row( String label, Object value ) { 
		Map<String, String> var = new HashMap<String, String>();
		var.put("Label", label);
		var.put("Value", value != null ? value.toString() : "-");
		
		return new StrSubstitutor(var) .replace(TEST_ROW);
	}

	@Override
	public void print(String string) {
		out.printf("<p>%s</p>\n", string);
	}
	
	
	String getTemplateCss() { 
		InputStream in = getClass().getResourceAsStream("report.css");
		String result;
		try {
			result = IOUtils.toString(new InputStreamReader(in));
		} 
		catch (IOException e) {
			throw new BlackCoffeeException(e, "Cannot read report template");
		}
		return result;
	}
	
	
	static String HEAD = 
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> \n" + 	
			"<html>\n" +
			"<head> \n" +
			"<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js\" ></script>\n" +
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
			"<title>${DocumentTitle}</title>\n" +
			"<style type=\"text/css\">\n${CSS}\n</style>\n" +
			"<script type=\"text/javascript\">\n" +
			"$(document).ready(function() { \n" +
			"$('div.test a') .click(function () { $(this).next().toggle(); }) \n" +
			"}); \n" +
			"</script> \n" + 
			"</head> \n ";

	
	static String TEST_ITEM = 
			"<li>\n" +  
			"<div class='test ${Status}\' > \n" +  
			"<span class=\"touch\">~</span><a href='javascript:void(0)'>${Label}</a> \n"+ 
	        "<div class='testResult'>\n" +
	        "<table><tbody>\n" +
	        "${Result}" +
	        "</tbody></table>\n" +
	        "</div> \n " + 
	        "</div></li>\n" ;
	
	static String TEST_ROW = 
			"<tr>\n" + 
			"<td width='10%' valign='top'>${Label}</td>\n" +
			"<td valign='top'>${Value}</td>\n" +
			"</tr>\n"; 

   
}
