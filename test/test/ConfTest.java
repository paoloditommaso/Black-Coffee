package test;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;


public class ConfTest {

	@Test 
	public void testConf() throws ConfigurationException { 
		File file = new File("./tests/.testcase");
		assertTrue(  file.exists() );
		
	}


}
