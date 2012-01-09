package org.blackcoffee;

import java.net.URISyntaxException;

public class BlackMain {

	/**
	 * Application entry point 
	 * 
	 * @param args
	 * @throws URISyntaxException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws URISyntaxException, ClassNotFoundException {
		/*
		 * Show the banner  
		 * See http://ascii.mastervb.net/  using font 'cricket'
		 */
		
		System.out.println(BlackCoffee.LOGO);
		System.out.printf("Version: %s\n", BlackCoffee.APPVERSION); 
		System.out.println();
		
					
		/*
		 * LEt's go ! 
		 */
		try { 
			int result = new BlackCoffeeRunner(Config.parse(args).initiliaze()).execute();
			System.exit(result);
		}
		catch( Throwable e ) { 
			e.printStackTrace(System.err);
			System.exit(1);	// return a non-zero error
		}
	}	
}
