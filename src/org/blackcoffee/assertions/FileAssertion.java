package org.blackcoffee.assertions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.blackcoffee.commons.format.Alphabet;
import org.blackcoffee.commons.format.Clustal;
import org.blackcoffee.commons.format.ClustalTC;
import org.blackcoffee.commons.format.Fasta;
import org.blackcoffee.exception.AssertionFailed;
import org.blackcoffee.exception.BlackCoffeeException;
import org.blackcoffee.parser.AssertionContext;
import org.blackcoffee.parser.StringWrapper;

/**
 * Defines the valid file assertions than can be defined in the assertions section of the test file
 * 
 * @author Paolo Di Tommaso
 *
 */
public class FileAssertion extends AbstractStringAssertion {

	File file;

	String filename;
	
	StringWrapper fContent;
	
	
	public FileAssertion( String filename ) { 
		this.filename = filename;
	}
	
	@Override
	public void initialize(AssertionContext ctx) {
		
		if( StringUtils.isEmpty(filename) ) { 
			throw new BlackCoffeeException("Missing 'filename' attribute for class: ", FileAssertion.class.getSimpleName());
		}
		
		if( filename.startsWith("/") ) { 
			file = new File(filename);
			return;
		}
		
		file = ctx != null && ctx.path != null 
			? new File(ctx.path,filename)
			: new File(filename);
			
	}	
	
	@Override public String toString() { 
		return new StringBuilder() 
			.append("FileAssertion[")
			.append(file)
			.append("]")
			.toString();
	}
	
	public StringWrapper content() {
		if( fContent == null ) try {
			fContent = new StringWrapper(FileUtils.readFileToString(file));
		} 
		catch (IOException e) {
			throw new AssertionFailed("Unable to read the file: %s", file);
		}
		
		return fContent;
	} 
	
	
	/**
	 * Verify that the file under test exists, otherwise the test will fail.
	 */
	public Boolean exists() { 
		return file.exists();
	}

	
	public Long size() { 
		return file.exists() ? file.length() : 0;
	}
	
	@Override
	public Integer length() {
		return size() .intValue();
	} 
	
	public ClustalTC isClustal4tcoffee() throws FileNotFoundException { 
		ClustalTC result = new ClustalTC(Alphabet.AminoAcid.INSTANCE);
		result.parse(file);
		
		if( !result.isValid() ) { 
			fail();
		}
		
		return result;
	}
	
	public Clustal isClustal() throws FileNotFoundException {
		Clustal result = new Clustal(Alphabet.AminoAcid.INSTANCE);
		result.parse(file);
		
		if( !result.isValid() ) { 
			fail();
		}
		
		return result;
		
	}
	
	public Fasta isFasta() throws FileNotFoundException {

		Fasta result = new Fasta(Alphabet.AminoAcid.INSTANCE);
		result.parse(file);
		
		if( !result.isValid() ) { 
			fail();
		}
		
		return result;
		
	}

}
