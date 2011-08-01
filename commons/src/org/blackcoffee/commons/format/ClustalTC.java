package org.blackcoffee.commons.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Clustal for TCoffee is a variation of Clustal format 
 * with a special header 
 * 
 * @author Paolo Di Tommaso
 *
 */
public class ClustalTC extends Clustal {

	String mode;
	String cpu;
	Integer score;
	Integer nseq;
	Integer len;
	String version;
	
	ClustalTC() { 
		super();
	}
	
	/**
	 * The sequences alphabet i.e. aminoacids, dna, rna, ..  
	 * 
	 * @see Alphabet
	 * 
	 * @param alpha
	 */
	public ClustalTC(Alphabet alpha) {
		super(alpha);
	}	
	
	private Pattern PATTERN = Pattern.compile(
			"CLUSTAL FORMAT for T-COFFEE ([^\\s]*) \\[http://www.tcoffee.org\\] \\[MODE:  \\], CPU=([^,]*), SCORE=([^,]*), Nseq=([^,]*), Len=(.*)$"
			);
	
	@Override 
	String parseHeader(String line) {
		if( StringUtils.isNotEmpty(line)) { 
			Matcher matcher = PATTERN.matcher(line);
			if( matcher.matches() ) { 
				
				this.version = matcher.group(1);
				this.cpu = matcher.group(2);
				this.score = toInt(matcher.group(3));
				this.nseq = toInt(matcher.group(4));
				this.len = toInt(matcher.group(5));

				return line;
			}
		}
		
		throw new FormatParseException("Missing Clustal For T-Coffee header declaration");

	}
	
	static Integer toInt( String str ) { 
		if( str == null ) return Integer.valueOf(0);
		
		return NumberUtils.toInt(str.trim(), 0);
	}
	

	/**
	 * @param file the file to be checked 
	 * @return <code>true</code> if the specified file a valid content in FASTA format 
	 */
	public static boolean isValid(File file, Alphabet alphabet) {
		try {
			Clustal clustal = new ClustalTC(alphabet);
			clustal.parse(file);
			return clustal.isValid();
		} 
		catch (FileNotFoundException e) {
			throw new FormatException("Specified file does not exists: %s", file);
		}
	}

	public static boolean isValid(String sequences, Alphabet alphabet) {
		Clustal clustal = new ClustalTC(alphabet);
		clustal.parse(sequences);
		return clustal.isValid();
	}	
	
	
}
