package org.blackcoffee;

import java.io.File;

import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.Predicate;
import org.blackcoffee.utils.VarHolder;

/**
 * Defines a test assertion and some other information 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class TestAssertion {
	
	public String declaration;
	public Predicate predicate;
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
		predicate = new Predicate(declaration)  .parse(last);
		
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
			Object result = predicate.invoke(ctx);
			
			/* 
			 * when the evaluate terminate with a boolean,
			 * if must be
			 */
			if( result instanceof Boolean ) { 
				if(Boolean.FALSE.equals(result)) 
				{ 
					String message = predicate.lastTerm != null 
								? predicate.lastTerm.toString() 
								: predicate.declaration;
					throw new AssertionFailed(message);
				}
			}		
			
			return result;
		}
		//
		// TODO review this part 
		// maybe do not catch the above AssertionFailed exception
		catch( Throwable e ) { 
			throw new AssertionFailed(e, this);
		}
	}

}