package org.blackcoffee.assertions;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.parser.AssertionContext;

/**
 * Defines the assertions that can be used to check a directory 
 * 
 * @author Paolo Di Tommaso
 * 
 *
 */
public class DirectoryAssertion extends AbstractAssertion {

	
	String sDirectory;
	
	File directory;
	
	public DirectoryAssertion ( String path ) { 
		this.sDirectory = path;
	}
	
	@Override
	public void initialize(AssertionContext ctx) {
		
		if( StringUtils.isBlank(sDirectory) ) { 
			throw new BlackCoffeeException("Missing path property for: ", DirectoryAssertion.class.getSimpleName() );
		}
		
		if( sDirectory.startsWith("/") ) { 
			directory = new File(sDirectory);
			return;
		}
		
		if( ctx != null && ctx.path != null ) { 
			directory = new File(ctx.path,sDirectory);
		}
		else { 
			directory = new File(sDirectory);
		}
			
	}
	
	@Override public String toString() { 
		return new StringBuilder() 
			.append("DirectoryAssertion[")
			.append(directory)
			.append("]")
			.toString();
	}
	
	/**
	 * Verify that the directory exists 
	 * 
	 */
	@Assertion
	public void exists() { 
		if( !directory.exists() ) { 
			fail("The directory '%s' is expected to exist, but it is not.", directory);
		}
	}

	
	/**
	 * Verify that the directory is empty
	 */
	@Assertion
	public void isEmpty() { 
		if( !directory.exists() ) { 
			System.err.printf("Warning the following path is supposed to exists: %s\n", directory ); 
		}
		
		if( directory.exists() && directory.list().length > 0 ) { 
			fail("The directory '%s' is expected to be empty, but it is not.", directory);
		}
	}
	
	
	/**
	 * Check if contains the specified file name 
	 * 
	 * @param path
	 */
	@Assertion
	public void contains( String wildcard ) { 
		
		FileFilter fileFilter = new WildcardFileFilter(wildcard);
		File[] files = directory.listFiles(fileFilter);
		
		if( files == null || files.length == 0 ) { 
			fail("The directory '%s' is expected to contain files that match wildcards %s, but it does not.", directory, wildcard);
		}
	}
	
	
	
	
}
