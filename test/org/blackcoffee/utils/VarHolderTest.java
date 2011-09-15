package org.blackcoffee.utils;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class VarHolderTest {

	private VarHolder holder;

	@Test
	public void testCreateFromString() { 
		
		holder = new VarHolder("x=1, y=2");
		assertEquals( "1", holder.get("x") );
		assertEquals( "2", holder.get("y") );
		
	}
	
	
	@Test
	public void testCreateFromMap() { 

		Map<String,String> map = new TreeMap<String,String>();
		map.put("a", "1");
		map.put("b", "2");
		
		holder = new VarHolder(map);
		assertEquals( "1", holder.get("a") );
		assertEquals( "2", holder.get("b") );

		
	}
	
	@Test
	public void testResolve() { 
		
		holder = new VarHolder("x=1, y=2, name=pablo");
		assertEquals( "1", holder.resolve("${x}"));
		assertEquals( "2", holder.resolve("${y}"));
		assertEquals( "${z}", holder.resolve("${z}") ); 

		assertEquals( "Hola pablo", holder.resolve("Hola ${name}") ); 
		assertEquals( "Hola $name", holder.resolve("Hola $name") ); 

		assertEquals( "this 1 + 2 = pablo", holder.resolve("this ${x} + ${y} = ${name}"));
	}
	
	@Test
	public void testValue() { 
		
		holder = new VarHolder("x=1, y=2, w=99, z=${w}, name=pablo");
		assertEquals( "1", holder.value("x"));
		assertEquals( "2", holder.value("y"));
		assertEquals( "99", holder.value("z") ); 

		assertEquals( "pablo", holder.value("name") ); 

	}
	
	@Test
	public void testElvisOprator() { 

		holder = new VarHolder("x=1, y=2, w=99, name=pablo");
		assertEquals( "pablo", holder.resolve("${name}"));
		
		assertEquals( "pablo", holder.resolve("${name?:gino}"));

		assertEquals( "gino", holder.resolve("${xxx?:gino}"));
		
		assertEquals( "gino", holder.resolve("${xxx ?: gino}"));
	}
}
