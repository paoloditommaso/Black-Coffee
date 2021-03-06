package org.blackcoffee.commons.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Parse and hold the information retuned in the T-Coffee error file 
 * 
 * @author Paolo Di Tommaso
 */
public class TCoffeeErrorLog {

	List<String> warnings = new ArrayList<String>();


	public List<String> getWarnings()  { 
		return warnings;
	}

	
	public TCoffeeErrorLog addWarning( String warn ) { 
		if( StringUtils.isNotBlank(warn) && !warnings.contains(warn)) {
			warnings.add(warn);
		}
		
		return this;
	}	
	
	public static TCoffeeErrorLog parse( File file ) throws IOException { 
		TCoffeeErrorLog result = new TCoffeeErrorLog();
		
		
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;
		/* parse the output items */
		boolean readingWarnSummary = false;
		while ((line = reader.readLine()) != null) {

			if( readingWarnSummary ) {
				if( !line.startsWith("**********************************************************************")) {
					result.addWarning( line.trim() );
				}
				else {
					readingWarnSummary = false;
				}
				continue;
			}
			
			if( line.startsWith("******************** WARNING: ****************************************") ) {
				readingWarnSummary = true;
				continue;
			}
			
			
			
			// check for warnings 
			String warn = TCoffeeResultLog.parseForWarning(line);
			if( StringUtils.isNotEmpty(warn)) { 
				if( warn.startsWith("WARNING:")) { 
					warn = warn.substring(8);
				}
				
				result.addWarning(warn.trim());
			}
		}

		return result;
	}
	
}
