package org.blackcoffee.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.TestCase;
import org.blackcoffee.commons.utils.CmdLineUtils;
import org.blackcoffee.utils.VarHolder;

/**
 * Models a generic test command 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class Command {

	protected String cmdline;
	protected String program;
	
	protected Map<String,List<String>> options = new TreeMap<String, List<String>>(); 
	
	protected List<String> args = new ArrayList<String>();
	
	protected VarHolder variables;
	
	public Command( String cmdline ) { 
		this.cmdline = cmdline;
		
		List<String> tokns = CmdLineUtils.cmdLineTokenizer(cmdline);
		this.program = tokns.size() > 0 ? tokns.get(0) : "";
		
		for( int i=1; i<tokns.size(); i++  ) { 
			String elem = tokns.get(i);
			
			if( elem.startsWith("-") ) { 
				addOption(elem);
			}
			else { 
				addArgument(elem);
			}
			
		}
	}
	
	public List<String> getArgumentsList() { 
		return args;
	}
	
	private void addOption(String elem) {
		/* remove minus at beginning */
		while( elem.startsWith("-") ) { 
			elem = elem.substring(1);
		}

		String key;
		String value;

		/*
		 * idenfify the 'key' and 'value' part
		 */
		int p = elem.indexOf("=");
		if( p == -1 ) { 
			p = elem.indexOf(' ');
		}
		if( p == -1 ) { 
			key = elem;
			value = null;
		}
		else { 
			key = elem.substring(0,p);
			value = elem.substring(p+1);
		}
		
		/*
		 * get the current value
		 */
		List<String> list = options.get(key);
		if( list == null ) { 
			list = new ArrayList<String>();
			options.put(key,list);
		}
		
		if( value == null ) { 
			// it is a flag (w/o any value): just return
			return;
		}
		
		// if contains commas ',' split by it owtherwise by ' '
		String[] parts = value.indexOf(",")!=-1 ? value.split(",") : value.split(" ");
		if( parts != null ) for( String str : parts ) { 
			if( StringUtils.isNotBlank(str)) { 
				list.add(str.trim());
			}
		}
	}

	private void addArgument( String value ) { 
		// if contains commas ',' split by it owtherwise by ' '
		String[] parts = value.indexOf(",")!=-1 ? value.split(",") : value.split(" ");
		if( parts != null ) for( String str : parts ) { 
			if( StringUtils.isNotBlank(str)) { 
				args.add(str.trim());
			}
		}		
	}
	
	/**
	 * Get a single option on the command line 
	 * 
	 * @param key the option key 
	 * @return the option value or <code>null</code> if the option does not exist
	 */
	public String getOption( String key ) { 
		List<String> result = options.get(key);
		return result != null && result.size()>0 ? result.get(0) : null;
	}
	
	/**
	 * A list of values for the specified options, think option like -in file1, file2, file3
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getOptionsList( String key ) { 
		return (List<String>) (options.get(key) != null ? options.get(key) : Collections.emptyList());
	}
	

	public String toString() { 
		return variables != null ? variables.resolve(cmdline) : cmdline;
	}

	/**
	 * Check is a specified key exists in the cmd line options
	 */
	public boolean hasOption(String key) {
		return options.containsKey(key);
	}
	
	/**
	 * Retrieve the p-th argumented 
	 * 
	 */
	public String getArgument( int p ) { 
		return args != null && p < args.size() ? args.get(p) : null;
	}
	
	
	public void configure( TestCase test ) { 
		this.variables = test.variables;
	}
	
}
