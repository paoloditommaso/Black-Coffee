package org.blackcoffee.assertions;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
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
		
		directory = ctx != null && ctx.path != null
			? new File(ctx.path,sDirectory)
			: new File(sDirectory);
			
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
			fail();
		}
	}

	
	/**
	 * Verify that the directory is empty
	 */
	@Assertion
	public void isEmpty() { 
		if( directory.exists() && directory.list().length > 0 ) { 
			fail();
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
			fail();
		}
	}
	
	
	
	
}
