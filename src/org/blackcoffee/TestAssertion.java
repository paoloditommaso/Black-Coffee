package org.blackcoffee;

import java.io.File;

import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.AssertionPredicate;
import org.blackcoffee.utils.VarHolder;

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
	public VarHolder variables = new VarHolder();
	
	public String toString() {
		return String.format("Assert[\n" +
				"  declaration: %s,\n" +
				"  message: %s,\n" +
				"  line:%s\n" +
				" ]", declaration, message, line );
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
	public Object verify(File runPath, Object previuosAssertionResult) { 
		AssertionContext ctx = new AssertionContext(runPath);
		ctx.previousAssertResult = previuosAssertionResult;
		ctx.variables = variables;
		
		try { 
			return predicate.invoke(ctx);
		}
		catch( Throwable e ) { 
			// wrap in a AssertionFailed exception 
			throw new AssertionFailed(e, this);
		}
	}

}