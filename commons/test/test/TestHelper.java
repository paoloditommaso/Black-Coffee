package test;

import java.io.File;


/**
 * Test helper class 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestHelper {


	public static File file(String file) {
		return new File(TestHelper.class.getResource(file).getFile());
	}

	public static File sampleLog() {
		return file("/sample-tcoffee.log");
	}

	public static File sampleFasta() {
		return file("/sample.fasta");
	}
	
	public static File sampleClustal() {
		return file("/sample-clustalw.txt");
	}
	

	
	public static int randomHash() {
		return new Double(Math.random()).hashCode();
	}
	
	public static String randomHashString() {
		return Integer.toHexString(randomHash());
	}
	
	public static void sleep( long millis ) {
		try {
			Thread.currentThread().sleep(millis);
		} catch (InterruptedException e) { }
	}
	

}
