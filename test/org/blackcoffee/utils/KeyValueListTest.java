package org.blackcoffee.utils;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.blackcoffee.utils.KeyValueList;
import org.junit.Test;

public class KeyValueListTest {

	private KeyValueList holder;

	@Test
	public void testCreateFromString() { 
		
		holder = new KeyValueList("x=1, y=2");
		assertEquals( "1", holder.get("x").value );
		assertEquals( "2", holder.get("y").value );
		
	}
	
	
	@Test
	public void testCreateFromMap() { 

		Map<String,String> map = new TreeMap<String,String>();
		map.put("a", "1");
		map.put("b", "2");
		
		holder = new KeyValueList(map);
		assertEquals( "1", holder.get("a").value );
		assertEquals( "2", holder.get("b").value );
		
		assertEquals( "1", holder.get(0).value );
		assertEquals( "2", holder.get(1).value );
		
	}
	
	@Test
	public void testResolve() { 
		
		holder = new KeyValueList("x=1, y=2, name=pablo");
		assertEquals( "1", holder.resolve("${x}"));
		assertEquals( "2", holder.resolve("${y}"));
		assertEquals( "${z}", holder.resolve("${z}") ); 

		assertEquals( "Hola pablo", holder.resolve("Hola ${name}") ); 
		assertEquals( "Hola $name", holder.resolve("Hola $name") ); 

	}
	
	@Test
	public void testValue() { 
		
		holder = new KeyValueList("x=1, y=2, w=99, z=${w}, name=pablo");
		assertEquals( "1", holder.value("x"));
		assertEquals( "2", holder.value("y"));
		assertEquals( "99", holder.value("z") ); 

		assertEquals( "pablo", holder.value("name") ); 

	}
}
