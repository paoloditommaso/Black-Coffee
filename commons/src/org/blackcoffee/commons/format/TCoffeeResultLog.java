package org.blackcoffee.commons.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Parse and holfd the T-Coffee result output data
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TCoffeeResultLog {

	static final Pattern RESULT_PATTERN = Pattern .compile("^\\s*\\#{4} File Type=(.+)Format=(.+)Name=(.+)$");

	static final Pattern WARNING_PATTERN = Pattern .compile("^\\d+ -- WARNING: (.*)$");
	
	
	List<String> warnings = new ArrayList<String>();
	ArrayList<FileItem> items = new ArrayList<FileItem>();
	
	
	public static class FileItem { 
		public String name;
		public String type;
		public String format;
	}
	
	public List<String> getWarnings()  { 
		return warnings;
	}
	
	public List<FileItem> getFileItems() { 
		return items;
	}
	
	TCoffeeResultLog addWarning( String warn ) { 
		warnings.add(warn);
		
		return this;
	}
	
	TCoffeeResultLog addFileItem( FileItem item ) { 
		items.add(item);
		
		return this;
	}
	
	public static TCoffeeResultLog parse(File file) throws IOException { 

		TCoffeeResultLog result = new TCoffeeResultLog();
		
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		/* consume until output section is found */
		while ((line = reader.readLine()) != null && 
				!"OUTPUT RESULTS".equals(line) && 
				!"Looking For Sequence Templates:".equals(line)
		) { /* empty */ }

		/* parse the output items */
		while ((line = reader.readLine()) != null) {
			FileItem item = parseForResultFileItem(line);
			if (item != null) {
				result.addFileItem(item);
			}
			
			// check for warnings 
			String warn = parseForWarning(line);
			if( StringUtils.isNotEmpty(warn)) { 
				if( warn.startsWith("WARNING:")) { 
					warn = warn.substring(8);
				}
				result.addWarning(warn.trim());
			}
		}
		/* .. and return the list */
		return result;
	}
	
	static FileItem parseForResultFileItem(String line) {
		Matcher matcher = RESULT_PATTERN.matcher(line);
		if (!matcher.matches()) {
			return null;
		}

		FileItem item = new FileItem();
		item. name = matcher.group(3).trim();
		item. type = matcher.group(1).trim();
		item. format = matcher.group(2).trim();		
		
		/* handle special exception */
		if( item. name == null || item. name.contains("NOT PRODUCED")) {
			return null;
		}
		
		return item;
	}	
	
	static String parseForWarning( String line ) { 
		Matcher matcher = WARNING_PATTERN.matcher(line);
		if (!matcher.matches()) {
			return null;
		}
		
		return matcher.group(1).trim();
	}

	
}
