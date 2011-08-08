package org.blackcoffee.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class PathUtils {

	/** the user home directory */
	File home = new File( System.getProperty("user.home") );
	
	/** The current working directory */
	File current = new File( System.getProperty("user.dir") );

	
	/**
	 * Set the home folder to the specified path 
	 */
	public PathUtils home( String path ) { 
		this.home = new File( FilenameUtils.normalize(path) );
		return this;
	}

	/**
	 * Set the home folder to the specified path 
	 */
	public PathUtils home( File path ) { 
		return home(path.getPath());
	}
	
	/**
	 * Set the current working directory to the specified path 
	 */
	public PathUtils current( String path ) { 
		this.current = new File( FilenameUtils.normalize(path) );
		return this;
	}
	
	/**
	 * Set the current working directory to the specified path 
	 */
	public PathUtils current( File path ) { 
		return current(path.getPath());
	}
	
	/**
	 * Make the specified path absolute replacing the symbol '~' or '.' with the appropriate values
	 */
	public File absolute( String file ) { 
		if( file == null ) return null;
		
		File result;
		// check if is an absolute path 
		if( file.startsWith( File.separator ) ) { 
			result = new File(FilenameUtils.normalize(file));
		}
		
		// home directory 
		else if( file.equals("~") ) { 
			result = new File( System.getProperty("user.home"));
		}
		
		else if( file.startsWith("~" + File.separator) ) { 
			result = new File( System.getProperty("user.home"), file.substring(2)  );
		}

		else if( file.equals(".") || StringUtils.isBlank(file)) { 
			result = current;
		}
		else if( file.startsWith("." + File.separator) ) { 
			result = new File(current, file.substring(2));
		}
		else { 
			result = new File(current, file);
		}
		
		return result;
	}
	
	public File absolute( File path ) { 
		return absolute(path.getName());
	}
}
