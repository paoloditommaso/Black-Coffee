package org.blackcoffee;

public class BlackMain {

	/**
	 * Application entry point 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		/*
		 * Show the banner  
		 * See http://ascii.mastervb.net/  using font 'cricket'
		 */
		
		System.out.println(BlackCoffee.LOGO);
		System.out.println();
		
					
		/*
		 * LEt's go ! 
		 */
		new BlackCoffeeRunner(Config.parse(args).initiliaze()).execute();
	}	
}
