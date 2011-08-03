package org.blackcoffee.report;

import java.io.File;

import org.blackcoffee.Config;
import org.blackcoffee.TestCase;
import org.blackcoffee.TestResult;

public class HtmlReport extends ReportBuilder {

	public HtmlReport(File file, Config config) {
		super(newFileStream(file), config);
	}

	@Override
	public void begin() {
		out.println("<html>");
		out.println("<head>");
		out.println("<title>BlackCoffe Test Report</title>");
		out.println("</head>");
		out.println("<body>");
	}

	@Override
	public void end() {
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		
	}

	@Override
	public void printHeader(String header) {
		out.print("<h1>");
		out.print(header); 
		out.print("</h1>");
		out.println("<table>");
		out.println("<tr><th>");
	}

	@Override
	public void printTest(TestCase test ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printResult(TestResult resul) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print(String string) {
		// TODO Auto-generated method stub
		
	}

}
