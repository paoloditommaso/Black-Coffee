package org.blackcoffee.commons;

import org.blackcoffee.commons.format.ClustalTCTest;
import org.blackcoffee.commons.format.ClustalTest;
import org.blackcoffee.commons.format.FastaTest;
import org.blackcoffee.commons.format.TCoffeeResultLogTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FastaTest.class,
	ClustalTest.class,
	ClustalTCTest.class,
	
	TCoffeeResultLogTest.class
})

public class AllTests {


}