package org.blackcoffee;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.Predicate;
import org.blackcoffee.parser.StringWrapper;
import org.blackcoffee.utils.VarHolder;

/**
 * Defines a test assertion and some other information 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestCondition {
	
	public String declaration;
	public Predicate predicate;
	public int line;
	public VarHolder variables = new VarHolder();
	
	public String toString() {
		return String.format("Assert[\n" +
				"  declaration: %s,\n" +
				"  message: %s,\n" +
				"  line:%s\n" +
				" ]", declaration, line );
	}

	/**
	 * Compile the underlying predicate
	 */
	public void compile(Class<?> last) {
		predicate = new Predicate(declaration)  .parse(last);
		
	} 
	
	/**
	 * Verify that specified assertion is satisfied 
	 * 
	 * @param ctx the context on which the assertion will be applyed 
	 * 
	 */
	public boolean evaluate() { 
		// TODO review the assertion context path
		AssertionContext ctx = new AssertionContext(".");
		ctx.previousAssertResult = null;
		ctx.variables = variables;
		
		try { 
			Object result = predicate.invoke(ctx);
			
			/* it must return a boolean */
			if( result instanceof Boolean ) { 
				return (Boolean)result;
			}
			else if( result instanceof Number ) { 
				return !"0".equals(result.toString());
			}
			else if( result instanceof StringWrapper ) { 
				return !((StringWrapper) result) .isEmpty();
			}
			else if( result instanceof CharSequence ) { 
				return StringUtils.isNotEmpty(result.toString());
			}
			else { 
				return result != null;
			}
		}
		catch( Throwable e ) { 
			// wrap in a AssertionFailed exception 
			throw new BlackCoffeeException("Error evaluating condition: %s", predicate.declaration );
		}
	}

}