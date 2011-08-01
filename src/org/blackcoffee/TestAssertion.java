package org.blackcoffee;

import org.blackcoffee.assertions.AssertionFailed;
import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.AssertionPredicate;

/**
 * Defines a test assertion and some other information 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestAssertion {
	
	public String declaration;
	public AssertionPredicate predicate;
	public String message;
	public int line;
	
	public String toString() {
		return String.format("Assert[ %s : %d ]", declaration, line);
	}

	/**
	 * Compile the underlying predicate
	 */
	public void compile(Class<?> last) {
		predicate = new AssertionPredicate(declaration)  .parse(last);
		
	} 
	
	/**
	 * Verify that specified assertion is satisfied 
	 * 
	 * @param ctx the context on which the assertion will be applyed 
	 * 
	 */
	public Object verify(AssertionContext ctx) { 
		try { 
			return predicate.invoke(ctx);
		}
		catch( Throwable e ) { 
			// wrap in a AssertionFailed exception 
			throw new AssertionFailed(e, this);
		}
	}

}