package org.blackcoffee.assertions;

import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.StringWrapper;

/**
 * Assertion based on environment property evaluation 
 * 
 * @author Paolo Di Tommaso
 *
 */

public class EnvAssertion extends AbstractStringAssertion {

	AssertionContext ctx;
	private String varname;
	
	public EnvAssertion( String var ) { 
		this.varname = var;
	}
	
	@Override
	public void initialize(AssertionContext context) {
		ctx = context;
	}

	@Override
	protected StringWrapper content() {
		String result;
		
		if( ctx.variables.containsKey(varname) ){ 
			 result = ctx.variables.value(varname);
		}
		else if( System.getenv().containsKey(varname) ){ 
			result = System.getenv().get(varname);
		}
		else { 
			result = System.getProperty(varname);
		}
		
		return new StringWrapper( result != null ? result : "" );
	}
	
	public String toString() { 
		return "Env["+ varname +"]";
	}

}
