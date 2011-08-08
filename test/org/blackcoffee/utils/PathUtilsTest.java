package org.blackcoffee.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class PathUtilsTest {
	
	@Test 
	public void testAbsolute() { 
		
		File home = new File(System.getProperty("user.home"));
		File base = new File(System.getProperty("user.dir"));
		
		assertEquals( new File("/path/to/file.txt"), new PathUtils().absolute("/path/to/file.txt"));

		assertEquals( home, new PathUtils().absolute("~"));
		assertEquals( home, new PathUtils().absolute("~/"));
		assertEquals( new File(home,"file.txt"), new PathUtils().absolute("~/file.txt"));

		assertEquals( base, new PathUtils().absolute("."));
		assertEquals( base, new PathUtils().absolute(""));
		assertEquals( base, new PathUtils().absolute("./"));
		assertEquals( new File(base,"file.txt"), new PathUtils().absolute("./file.txt"));
		assertEquals( new File(base,".."), new PathUtils().absolute(".."));

		
		File current = new File("/some/path");
		assertEquals( current, new PathUtils().current(current).absolute("."));
		assertEquals( current, new PathUtils().current(current).absolute(""));
		assertEquals( current, new PathUtils().current(current).absolute("./"));
		assertEquals( new File(current,"file.txt"), new PathUtils().current(current).absolute("./file.txt"));
		assertEquals( new File(current,".."), new PathUtils().current(current).absolute(".."));

		
		
	}

}
