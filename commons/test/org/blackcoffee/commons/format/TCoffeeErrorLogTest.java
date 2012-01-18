package org.blackcoffee.commons.format;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import test.TestHelper;

public class TCoffeeErrorLogTest {

	
	@Test
	public void testWarnSummaryLog() throws IOException {
		
		
		TCoffeeErrorLog log = TCoffeeErrorLog.parse(TestHelper.file("/tcoffee-warn-summary.log"));
		
		assertNotNull( log.warnings );
		assertEquals( "SAP is not installed", log.warnings.get(0) );
		assertEquals( "TMalign will be used instead", log.warnings.get(1) );
		assertEquals( "tmalign is FASTER than SAP and *almost* as accurate", log.warnings.get(2) );
		
		assertEquals( 3, log.warnings.size() );
	} 	
	
}
