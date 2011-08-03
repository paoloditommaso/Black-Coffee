package org.blackcoffee.commons.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CmdLineUtils {

	

	/**
	 * Tokenize the command line in its single parts. Command line options have to start with one or more '-' characters 
	 * and value must be preceed by the '=' or blank character. For example 
	 * <pre>
	 * t_coffee input.fa -flag -mode=regular -output ascii html pdf 
	 * </pre> 
	 * 
	 * @param cmdLine
	 * @return
	 */
	static public List<String> cmdLineTokenizer( String cmdLine ) { 

		if( cmdLine == null ) { return Collections.emptyList(); };
		
		cmdLine = normalize(cmdLine);
		
		Pattern OPTION_SEPARATOR = Pattern.compile("[ \\t\\n\\x0B\\f\\r]-");
		Pattern BLANK_SEPARATOR = Pattern.compile("[ \\t\\n\\x0B\\f\\r]");
		
		List<String> result = new ArrayList<String>();
		
		Pattern separator;
		while( StringUtils.isNotEmpty(cmdLine)) { 
			String item;
			Matcher matcher;
			cmdLine = cmdLine.trim();
			
			
			separator = cmdLine.startsWith("-")  ? OPTION_SEPARATOR : BLANK_SEPARATOR;

			/* lookahead for the next option separator */
			if( (matcher=separator.matcher(cmdLine)).find() ) { 
				item = cmdLine.substring(0,matcher.start());
				cmdLine = cmdLine.substring(matcher.start()+1);
			}
			else { 
				item = cmdLine;
				cmdLine = null;
			}
			result.add(item);
		}
		
		return result;
		
	}

	
	/**
	 * replace any invalid dash character as separator 
	 */
	public static String normalize( String args ) { 
		return args != null ? args.replaceFirst(" [\\‐\\‒\\—\\―\\–]", " -") : null;		
	}	
}
