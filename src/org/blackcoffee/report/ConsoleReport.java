package org.blackcoffee.report;

import org.blackcoffee.Config;


public class ConsoleReport extends TextReport {

	public ConsoleReport(Config config) {
		super(System.out, config);
	}

	@Override
	public void begin() {
		// override to prevent to print the app logo 
	}




}
