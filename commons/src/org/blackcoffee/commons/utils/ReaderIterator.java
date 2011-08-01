package org.blackcoffee.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

public class ReaderIterator implements Iterable<String> {

	private BufferedReader reader;
	
	private boolean autoClose;

	
	/**
	 * Public constructor accepting any reader. 
	 * 
	 * @param reader 
	 */
	public ReaderIterator( Reader reader, boolean close ) {
		this.reader = reader instanceof BufferedReader 
					? (BufferedReader) reader 
					: new BufferedReader(reader);
					
		this.autoClose = close;
	}
	
	public ReaderIterator( Reader reader ) {
		this(reader,true);
	}

	public Iterator<String> iterator() {
		return new Iterator<String>() {

			String nextLine = getLine();
			
			String getLine() {
				try {
					return reader.readLine();
				} catch( IOException e ) {
					throw new RuntimeException("Unable to read line on buffered reader",e);
				}
			}
			
			
			public boolean hasNext() {
				return nextLine != null;
			}

			public String next() {
				String result = nextLine;
				nextLine = getLine();
				if( nextLine == null && autoClose ) {
					IOUtils.closeQuietly(reader);
				}
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException("Remove operation is not supported on ReaderIterator");
			};
		};
	}


}