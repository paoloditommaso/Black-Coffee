package org.blackcoffee.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.blackcoffee.assertions.AbstractAssertion;
import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.utils.VarHolder;

/**
 * Hold and evauale a single term composing the assertion predicate 
 * 
 * @author Paolo DI Tommaso
 *
 */
public class PredicateTerm { 
	
	public Method method;
	public Object[] args;
	public CompareOperator compareToOperator;
	public Field field;
	public boolean not;
	
	
	Object leftArgument;
	 
	 public Object invoke( Object target, AssertionContext ctx ) { 
		 leftArgument = target;
		 
		 /*
		  * initialize the method context 
		  */
		if( target instanceof AbstractAssertion ) { 
			((AbstractAssertion)target) .initialize(ctx);
		}
		 
		try {
			Object result;
			if( method != null ) { 
				result = method.invoke(target, resolveArgs(ctx.variables, args));
			}
			else if( field != null ) { 
				result = field.get(target);
			}
			else { 
				result = null;
			}
			
			/* 
			 * all strings are wrapped with the class 'StringWrapper' to add some helper methods 
			 */
			if( result instanceof String ) { 
				result = new StringWrapper((String)result);
			}
			
			
			/* 
			 * when the executed method was an operator evaluation 
			 * it is required to 'normalize" the result value
			 */
			else if( compareToOperator != null  ) { 
				result = compareToOperator.eval((Integer)result);
			}
			
		
			/*
			 * apply the 'not' operator is has specified 
			 */
			if( not && result instanceof Boolean ) { 
				Boolean val = (Boolean)result;
				return ! val.booleanValue();
			}
			
			return result;
			
		} 
		
		catch( InvocationTargetException e ) { 
			if( e.getTargetException() instanceof AssertionFailed ) {
				throw (AssertionFailed)e.getTargetException();
				
			}
			
			if( e.getTargetException() instanceof BlackCoffeeException ) { 
				throw (BlackCoffeeException)e.getTargetException();
			}
			
			throw new BlackCoffeeException(e, "Failed target assertion '%s'", method.getName());
		}
		catch (Exception e) {
			throw new BlackCoffeeException(e, "Cannot invoke assertion '%s'", method.getName());
		}
	 }

	 	

	static Object checkOperatorResult(CompareOperator op, Integer result) {

		return ( op != null ) ? op.eval(result) : result;
		
	}	 
	 
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		/* the not operator */
		if( not ) result.append("not ");

		/* left argument value */
		result
			.append( leftArgument != null ? wrapString(leftArgument) : "(?)" )
			.append( " ");
		
		/* operator or method or field */
		if( compareToOperator != null ) { 
			result.append( compareToOperator ) .append(" "); 
		}
		else if( method != null ){ 
			result.append(method.getName()) .append(" ");
		
		}
		else if( field != null ) { 
			result.append(field.getName()) .append(" ");
		}

		if( args != null && args.length == 1 ) { 
			result.append( wrapString(args[0]) );
		}
		else if( args != null ) { 
			result.append("(");
			int i=0; for( Object obj : args ) { 
				if( i++>0 ) result.append(", ");
				result.append(wrapString(obj));
			}
			result.append(") ");
		}
	
		
		return result.toString();
	} 
	
	
	static String wrapString( Object val ) { 
		if( val instanceof String || val instanceof StringWrapper ) { 
			return "'" + val.toString() + "'";
		}
		return String.valueOf(val);
	}
	
	static Object[] resolveArgs(VarHolder vars, Object... args ) { 

		Object[] result = new Object[args != null ? args.length : 0];
		int i=0;
		if( args != null ) for( Object obj: args ) { 
			if( obj instanceof StringWrapper ) { 
				result[i] = new StringWrapper( vars.resolve(obj.toString()) );
			}
			else if( obj instanceof String ) { 
				result[i] = vars.resolve(obj.toString());
			}
			else { 
				result[i] = obj;
			}

			i++;
		}
		
		return result;
		
	}
	
	
}


enum CompareOperator { 
	
	EQ  ("=")  { 
		@Override public Boolean eval(Integer value) {
		return value==0;
		} }  ,
	
	
	NE  ("!=") { 
		@Override public Boolean eval(Integer value) {
			return value != 0;
			} }  ,
			
	LT  ("<") { 
		@Override public Boolean eval(Integer value) {
			return value < 0;
			} }  ,
 
	LTE ("<=") { 
		@Override public Boolean eval(Integer value) {
			return value <= 0;
			} }  ,

	GT  (">") { 
		@Override public Boolean eval(Integer value) {
			return value > 0;
			} }  ,
 
	GTE (">=") { 
		@Override public Boolean eval(Integer value) {
			return value >= 0;
			} };
 

	private final String value; 
	
	CompareOperator(String op) { 
		this.value = op;
	}

	public String toString() { 
		return value;
	}
	
	static CompareOperator fromString( String op ) { 
		
		for( CompareOperator item: values() ) { 
			if( item.value.equals(op) ) { 
				return item;
			}
		}
		
		return null;
	}
	
	static CompareOperator strToOp(String str) {

		if( str == null || str.length()>2 ) { 
			return null;
		}
		
		if( "=".equals(str) ) { 
			return CompareOperator.EQ;
		}
	
		if( "!=".equals(str) ) { 
			return CompareOperator.NE;
		}
		
		if( "<".equals(str) ) { 
			return CompareOperator.LT;
		}
	
		if( "<=".equals(str) ) { 
			return CompareOperator.LTE;
		}
	
		if( ">".equals(str) ) { 
			return CompareOperator.GT;
		}
	
		if( ">=".equals(str) ) { 
			return CompareOperator.GTE;
		}
	
		return null;
	
	}	
	
	public abstract Boolean eval( Integer value ); 

} 