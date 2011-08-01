package org.blackcoffee;

/**
 * Some constants 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class BlackCoffee {

	final static String APPNAME = "black-coffee";
	
	final static String TEST_CASE_FILE_NAME = ".testcase";

	
	public final static String LOGO = 
		" _______  __               __     _______          ___   ___               \n" +
		"|   _   \\|  |.---.-..----.|  |--.|   _   |.-----..'  _|.'  _|.-----..-----.\n" +
		"|.  1   /|  ||  _  ||  __||    < |.  1___||  _  ||   _||   _||  -__||  -__|\n" +
		"|.  _   \\|__||___._||____||__|__||.  |___ |_____||__|  |__|  |_____||_____|\n" +
		"|:  1    \\                       |:  1   |                                 \n" +
		"|::.. .  /                       |::.. . |                                 \n" +
		"`-------'                        `-------'  ";
	
	
	static public void printHelp() {
		System.out.printf("Usage: %s [test/path]\n", APPNAME);
		System.out.printf(
				"Tests have to be defined in the specified file 'test/path'. \n\n" +
				"If the specified path is a directory, it have to contain a test definition file named '%s'.\n" +
				"If the path is omitted will be used the current directory by default.", TEST_CASE_FILE_NAME);
		
	}


	
	
}
