package org.blackcoffee.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.blackcoffee.assertions.AbstractAssertion;
import org.blackcoffee.assertions.BoolAssertion;
import org.blackcoffee.assertions.NumberAssertion;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.utils.QuoteStringTokenizer;

/**
 * Models an condition/assertion predicate 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class Predicate {

	public String declaration;

	public static class Root { 
		Class<?> clazz;
		Constructor<?> constructor;
		String[] args;
		
		public boolean isContinuation() { return constructor == null; }
	}
	
	public Root root;

	public List<PredicateTerm> chain = new ArrayList<PredicateTerm>();

	public Class<?> lastResultType;
	
	public PredicateTerm lastTerm;

	public Predicate( String assertion ) {
		declaration = assertion.trim();
	} 
	
	public String toString() { 
		return "Predicate["+declaration+"]";
	}
	
	/**
	 * Invoke to parse the assertion condition 
	 * 
	 * @return this class itself 
	 */
	public Predicate parse() { 
		return parse(null);
	}
	
	public Predicate parse(final Class<?> lastType) { 
		
		QuoteStringTokenizer tokenizer = new QuoteStringTokenizer(declaration);
		
		/*
		 * 1. 
		 * - The first token have to reference a class.
		 * - if the token string contains a '.' it will be evaluated as a fully qualified class name 
		 *   otherwise as the class short name  
		 */
		String token = tokenizer.next();
		
		String clazzName = null;
		boolean isLiteral = false;
		
		/*
		 * check for boolean or number literals 
		 */
		if( NumberUtils.isNumber(token)) { 
			clazzName = NumberAssertion.class.getName();
			isLiteral = true;
		}
		else if( "false".equals(token.toLowerCase()) || "true".equals(token.toLowerCase()) ) { 
			clazzName = BoolAssertion.class.getName();
			isLiteral = true;
		}
		
		else if( !token.contains(".") && !"_".equals(token)  ) { 
			// get the fully qualified AbstactAssertion class name 
			// and replace the Abstact with the assertion class 'short' name 
			// so for example 'file' -> 'com...FileAssertion'
			clazzName = AbstractAssertion.class.getName().replace("Abstract", StringUtils.capitalize(token));
		}
		else { 
			clazzName = token;
		}
		
		
		try {
			/* 
			 * 2. 
			 * The assertion class must define one and only one constructor with zero or more 'string' 
			 * arguments. The arguments values will be fetched on the assertion string    
			 */
			root = new Root();
			
			
			if(	"_".equals(clazzName) ) { 
				root.clazz = lastType;
			}
			else { 
				root.clazz = Class.forName(clazzName);
				root.constructor = root.clazz.getConstructors()[0];
				
				if( isLiteral ) { 
					// in this case the class for literal have just one string constructor 
					// that takes the literal itself as constructor argument 
					root.args = new String[] { token } ;
				}
				else { 
					// else fetch the constructor as load as many tokens as the number of the 
					// constructor arguments 
					Class<?>[] constructorParamTypes = root.constructor.getParameterTypes();
					root.args = new String[ constructorParamTypes != null ? constructorParamTypes.length : 0 ];
					for( int i=0; i<root.args.length; i++ ) { 
						root.args[i] = tokenizer.next();
					}
				}
			}

			
			/*
			 * 4. find out the method 
			 */
			Class<?> _clazz = root.clazz;
			Field _field = null;
			Method _method = null;
			Object[] _methodArgs=null;
			while ( tokenizer.hasNext() ) { 
				token = tokenizer.next();
				boolean _hasNot = false;
				
				/* 
				 * check for term negation 
				 */
				if( "!".equals(token) || "not".equalsIgnoreCase(token) ) { 
					_hasNot = true;
					token = tokenizer.next();
				}
				
				/* 
				 * all comparison operator =, !=, >, >=,  <, <= 
				 * are converted to Comparable.compareTo method
				 */
				CompareOperator _op=null;
				if( Comparable.class.isAssignableFrom(_clazz) && (_op=CompareOperator.fromString(token)) != null ) { 
					token = "compareTo";
				}
				
				/*
				 * operator '=' is converted to standard equals method  
				 */
				if( token.startsWith("!") ) { 
					token = token.substring(1);
					_hasNot = !_hasNot;
				}

				if( "=".equals(token) ) { 
					token = "equals";
				}
				
				Method[] all = _clazz.getMethods();
				for( Method method : all ) {
					if( token.equals(method.getName()) ) { 
						_method = method;
						break;
					}
				}

				if( _method != null ) { 
					/*
					 * 5. get the args and invoke the method 
					 */
					Class<?>[] paramTypes;
					if( _op != null ) { 
						// when it is an operator the argument is always of the same type of the previous class
						// and it must have a string constructor
						paramTypes = new Class[] { _clazz };
						String val =  tokenizer.next();
						_methodArgs = new Object[] { wrap(_clazz, val) }; 
					}
					else { 
						// otherwise we expect as many parameters as the number of arguments of the method
						paramTypes = _method.getParameterTypes();
						_methodArgs = new Object[ paramTypes != null ? paramTypes.length : 0 ];
						for( int i=0; i<_methodArgs.length; i++ ) { 
							_methodArgs[i] = wrap(paramTypes[i], tokenizer.next());
						}		
					}
				}
				else { 
					/* 
					 * try to access any field with that name 
					 */
					_field = FieldUtils.getField(_clazz, token, true);
						
					if( _field == null ) { 
						throw new BlackCoffeeException("Unknown operation '%s' in assertion '%s' for type %s", token, declaration, _clazz);
					}
					
				}
				
				PredicateTerm item = new PredicateTerm();
				item.method = _method;
				item.args = _methodArgs;
				item.field = _field;
				item.compareToOperator = _op;
				item.not = _hasNot;
				chain.add(item);
				
				/*
				 * extract the method return type and use as the next 'base' class 
				 * to iterate the evaluation 
				 */
				Class<?> _returnType = _method != null ? _method.getReturnType() : _field.getType();
				
				if( !Void.class.equals(_returnType)) { 
					_clazz = normalizePrimitiveType(_returnType);
					if( _clazz.equals(String.class)) { 
						_clazz = StringWrapper.class;
					}

				};
			}
			
			if( "_".equals(clazzName) ) { 
				lastResultType = lastType;
			}
			else { 
				lastResultType = _clazz;
			}

			return this;
		} 
		catch( BlackCoffeeException e ) { 
			throw e;
		}
		catch (Exception e) {
			throw new BlackCoffeeException(e, "Invalid assertion definition for '%s'", clazzName);
		}
	}




	public Object invoke(AssertionContext ctx) { 
		Object result = null;
		Object obj;
		
		if( root.constructor != null ) { 
			/* create the assertion object */
			Object[] _args = PredicateTerm.resolveArgs(ctx.variables, root.args);
			try {
				obj = root.constructor.newInstance(_args);
			} 
			catch (Throwable e) {
				throw new BlackCoffeeException(e, "Cannot instantiate %s with argument: %s", root.clazz, Arrays.asList(_args));
			}				

		}
		else { 
			obj = ctx.previousAssertResult;
		}
		
		// clear the last evaluated term 
		lastTerm = null;
		for( PredicateTerm item : chain ) { 
			
			result = item.invoke(obj, ctx);
		
			/* 
			 * when the method does not return nothing 
			 * reuse the target 'obj' instance to chain invocation for next method (if any)
			 */
			if( result instanceof Void ) { 
				result = obj;
			}
			
			/*
			 * iterate the next method invocation passing the current result 
			 * as the next target instance
			 */
			else { 
				obj = result;
			}
		
			// keep track of the last evaluated term (used outside)
			lastTerm = item;
		}
		
		return obj;
	}	
	

	
	static Object createInstance( Class<?> clazz, Object... args ) throws Exception { 
		Class<?>[] types = new Class[args.length];
		
		int i=0; for( Object obj : args ) { 
			types[i++] = obj != null ? obj.getClass() : null;
		}
		
		Constructor<?> constructor = clazz.getDeclaredConstructor(types);
		constructor.setAccessible(true);
		return constructor.newInstance(args);
	}


	static Class<?> normalizePrimitiveType( Class<?> clazz ) { 
		if( !clazz.isPrimitive() ) { 
			return clazz; 
		}
	
		
		if( Byte.TYPE.equals( clazz ) ) { 
			return Byte.class;
		}
		else if( Short.TYPE.equals(clazz) ) { 
			return Short.class;
		}
		else if( Integer.TYPE.equals(clazz)) { 
			return Integer.class;
		}
		else if( Long.TYPE.equals(clazz)) { 
			return Long.class;
		}
		else if( Float.TYPE.equals(clazz)) { 
			return Float.class;
		}
		else if( Double.TYPE.equals(clazz)) { 
			return Double.class;
		}
		else if( Boolean.TYPE.equals(clazz)) { 
			return Boolean.class;
		}
		else if( Character.TYPE.equals(clazz)) { 
			return Character.class;
		}
		else if( Void.TYPE.equals(clazz) ) { 
			return Void.class;
		}
		throw new BlackCoffeeException("Unknown primitive type: %s", clazz);
 	}
	
	
	static Object wrap( Class<?> type, String value ) throws Exception { 
		if( value == null ) { return null; }
		
		if( String.class.isAssignableFrom(type) ) { 
			return value;
		}
		
		type = normalizePrimitiveType(type);
		
		return createInstance(type, value);
	}
}
