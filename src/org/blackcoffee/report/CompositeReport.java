package org.blackcoffee.report;

import org.blackcoffee.TestCase;
import org.blackcoffee.TestResult;

public class CompositeReport extends ReportBuilder {

	ReportBuilder[] delegates; 
	
	public CompositeReport(ReportBuilder... reports) {
		delegates = reports != null ? reports : new ReportBuilder[]{};
	}

	@Override
	public void begin() {
		for( ReportBuilder report : delegates ) { 
			report.begin();
		}
	}

	@Override
	public void end() {
		for( ReportBuilder report : delegates ) { 
			report.end();
		}
	}

	@Override
	public void group(String header) {
		for( ReportBuilder report : delegates ) { 
			report.group(header);
		}
	}

	@Override
	public void groupEnd() {
		for( ReportBuilder report : delegates ) { 
			report.groupEnd();
		}
	}

	@Override
	public void test(TestCase test) {
		for( ReportBuilder report : delegates ) { 
			report.test(test);
		}
	}

	@Override
	public void testEnd(TestResult result) {
		for( ReportBuilder report : delegates ) { 
			report.testEnd(result);
		}

	}

	@Override
	public void print(String string) {
		for( ReportBuilder report : delegates ) { 
			report.print(string);
		}
	}

	public ReportBuilder[] getDelegates() {
		return delegates;
	}

}
