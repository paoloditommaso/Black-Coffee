package org.blackcoffee.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.TestCase;

public class TcoffeeCommand extends Command {

	
	public TcoffeeCommand(String cmdline) {
		super( "t_coffee" + (cmdline != null ? " " + cmdline : ""));
	}
	
	
	@Override
	public void configure(TestCase test) {
		super.configure(test);
		
		/*
		 * fetch all input file declared on known input options
		 */
		List<String> files = new ArrayList<String>();

		files.addAll( getOptionsList("aln") ); 
		files.addAll( getOptionsList("infile") ); 
		files.addAll( getOptionsList("in") ); 
		files.addAll( getOptionsList("in2") ); 
		files.addAll( getOptionsList("in3") ); 
		files.addAll( getOptionsList("code") ); 
		files.addAll( getOptionsList("decode") ); 
		files.addAll( getOptionsList("struc_in") ); 
		files.addAll( getOptionsList("struc_in_f") ); 
		files.addAll( getOptionsList("seq") ); 
		files.addAll( getOptionsList("pdb") );
		files.addAll( getOptionsList("lib") );
		files.addAll( getOptionsList("profile") );
		files.addAll( getOptionsList("profile1") );
		files.addAll( getOptionsList("profile2") );
		files.addAll( getOptionsList("template_file") );
		files.addAll( getOptionsList("parameters") );
		files.addAll( getOptionsList("pdb") );
		files.addAll( getOptionsList("usetree") );
		files.addAll( getOptionsList("lib_list") );
		
		files.addAll( getArgumentsList() );
		
		if( files.size()>0 ) { 
			List<Character> special = Arrays.asList('P','S','M','L','A','X','R');

			if( test.input == null ) { 
				test.input = new ArrayList<String>(files.size());
			}

			// remove the aboe special chars from the file name 
			for( String name : files )  { 
				if( special.contains(name.charAt(0)) ) { 
					name = name.substring(1);
				}
				test.addInputFile(name);
			}
			
		}
		
		/* 
		 * also add a tag when the method parameter is specified 
		 */
		if( StringUtils.isNotBlank(getOption("mode")) ) { 
			test.addTag( getOption("mode") );
		}
		
	}

}
