package org.blackcoffee.commons.format;


import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.blackcoffee.commons.format.Clustal.Block;
import org.junit.Test;

import test.TestHelper;

public class ClustalTCTest  {

	final String HEADER = "CLUSTAL FORMAT for T-COFFEE r802 [http://www.tcoffee.org] [MODE:  ], CPU=0.08 sec, SCORE=42, Nseq=5, Len=129";

	@Test
	public void testClustalHeader() { 
		final ClustalTC clustal = new ClustalTC();
		
		assertEquals( HEADER, clustal.parseHeader(HEADER) );
		assertEquals( "r802", clustal.version );
		assertTrue( StringUtils.isEmpty(clustal.mode) );
		assertEquals( "0.08 sec", clustal.cpu );
		assertEquals( 42, (int)clustal.score );
		assertEquals( 5, (int)clustal.nseq );
		assertEquals( 129, (int)clustal.len );


	
	}
	
	@Test 
	public void testParseBlock() throws IOException { 
		String seqs = 
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP";
		
		ClustalTC clustal = new ClustalTC();
		Block block = clustal.parseBlock( new StringReader(seqs) );
		
		assertEquals( 5, block.keyMaxLength );
		assertEquals( 3, block.list.size() );
		assertEquals( "1aboA", block.list.get(0).key );
		assertEquals( "1ycsB", block.list.get(1).key );
		assertEquals( "1pht", block.list.get(2).key );

		assertEquals( "NGQGWVPSNYITPVN------", block.list.get(0).value );
		assertEquals( "DKEGYVPRNLLGLYP------", block.list.get(1).value );
		assertEquals( "GERGDFPGTYVEYIGRKKISP", block.list.get(2).value );

	
		String result = 
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n";
		
		assertEquals( result, block.toString() );
	}

	@Test 
	public void testParseBlockExtended() throws IOException { 
		String seqs = 
			"1aboA  NGQGWVPSNYITPVN------ 20\n" +
			"1ycsB  DKEGYVPRNLLGLYP------ 20\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP 30\n" +
			"           ***:**";
		
		ClustalTC clustal = new ClustalTC();
		Block block = clustal.parseBlock( new StringReader(seqs) );
		
		assertEquals( 3, block.list.size() );
		assertEquals( "1aboA", block.list.get(0).key );
		assertEquals( "1ycsB", block.list.get(1).key );
		assertEquals( "1pht", block.list.get(2).key );

		assertEquals( "NGQGWVPSNYITPVN------", block.list.get(0).value );
		assertEquals( "DKEGYVPRNLLGLYP------", block.list.get(1).value );
		assertEquals( "GERGDFPGTYVEYIGRKKISP", block.list.get(2).value );
	}
	

	@Test 
	public void testParseBlockFail() throws IOException { 
		String seqs = 
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsBxxDKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n";
		
		ClustalTC clustal = new ClustalTC();
		try { 
			clustal.parseBlock( new StringReader(seqs) );
			fail();
		}
		catch( FormatParseException e ) { 
			// this exception have to be thrown
		}
	}
	
	@Test 
	public void testBlockGetKeys() throws IOException { 
			String seqs = 
				"1aboA  NGQGWVPSNYITPVN------\n" +
				"1ycsB  DKEGYVPRNLLGLYP------\n" +
				"1pht   GERGDFPGTYVEYIGRKKISP";
			
			ClustalTC clustal = new ClustalTC();
			Block block = clustal.parseBlock( new StringReader(seqs) ); 
			assertEquals( 3, block.getKeysList().size() );
			assertEquals( "1aboA", block.getKeysList().get(0) );
			assertEquals( "1ycsB", block.getKeysList().get(1) );
			assertEquals( "1pht", block.getKeysList().get(2) );
	}
	
	@Test
	public void testParse() throws IOException { 
		String seqs = 
			HEADER + "\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n";
		
		
		ClustalTC clustal = new ClustalTC();
		clustal.parse( new StringReader(seqs) );

		assertEquals( 2, clustal.blocks.size() );
		assertEquals( 3, clustal.sequences.size() );

		assertEquals( "1aboA", clustal.sequences.get(0).header );
		assertEquals( "1ycsB", clustal.sequences.get(1).header );
		assertEquals( "1pht", clustal.sequences.get(2).header );
	
		assertEquals( "NGQGWVPSNYITPVNNGQGWVPSNYITPVN", clustal.sequences.get(0).value );
		assertEquals( "DKEGYVPRNLLGLYPDKEGYVPRNLLGLYP", clustal.sequences.get(1).value );
		assertEquals( "GERGDFPGTYVEYIGRKKISPGERGDFPGTYVEYIGRKKISP", clustal.sequences.get(2).value );
	
	}
	
	@Test 
	public void testIsBlockTerminated() { 
		assertTrue( ClustalTC.isBlockTermination("   ") );
		assertTrue( ClustalTC.isBlockTermination("  .*. ") );
		assertFalse( ClustalTC.isBlockTermination("xxx") );
	}
	
	
	@Test 
	public void testIsNotValidFile() {
		assertFalse( ClustalTC.isValid( TestHelper.file("/clustal-sample.txt") , Alphabet.AminoAcid.INSTANCE) );
	}

	@Test 
	public void testIsValidFile() {
		assertTrue( ClustalTC.isValid( TestHelper.file("/sample.custal_for_tcoffee") , Alphabet.AminoAcid.INSTANCE) );
	}

	@Test 
	public void testParseFile() throws FileNotFoundException { 
		ClustalTC clustal = new ClustalTC(Alphabet.AminoAcid.INSTANCE);
		clustal.parse(TestHelper.file("/sample.custal_for_tcoffee"));
		
		/*
		 * the header is as the following 
		 * CLUSTAL FORMAT for T-COFFEE r802 [http://www.tcoffee.org] [MODE:  ], CPU=0.08 sec, SCORE=42, Nseq=5, Len=129  
		 */
		
		assertTrue( clustal.isValid() );
		assertEquals( "0.08 sec", clustal.cpu );
		assertEquals( 42, (int)clustal.score );
		assertEquals( 5, (int)clustal.nseq );
		assertEquals( 129, (int)clustal.len );
	}
	
	@Test 
	public void testDna() { 
		String seqs = 
			HEADER + "\n" +
			"\n" +
			"1aboA  ATCGCGATCATATCG------\n" +
			"1ycsB  CGATCGATCGATCGT------\n" +
			"1pht   ATCGATCGGCTAAAGCTATTA\n" +
			"\n" +
			"1aboA  ACATTCATTATCTAA------\n" +
			"1ycsB  CGAGCTAGCATATCT------\n" +
			"1pht   ATCAGCATGCAGCATGCGATT\n";
		
		assertTrue(ClustalTC.isValid(seqs, Alphabet.Dna.INSTANCE));
		assertFalse(ClustalTC.isValid(seqs, Alphabet.Rna.INSTANCE));
				
	}
	
	@Test 
	public void testProteins() { 
		String seqs = 
			HEADER + "\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n";
		
		assertTrue(ClustalTC.isValid(seqs, Alphabet.AminoAcid.INSTANCE));
		assertFalse(ClustalTC.isValid(seqs, Alphabet.Rna.INSTANCE));
		assertFalse(ClustalTC.isValid(seqs, Alphabet.Dna.INSTANCE));
		assertFalse(ClustalTC.isValid(seqs, Alphabet.NucleicAcid.INSTANCE));
				
	}	
	
	@Test 
	public void testToString() { 
		String seqs = 
			HEADER + "\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------ 20\n" +
			"1ycsB  DKEGYVPRNLLGLYP------ 30\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP 20\n" +
			"         .:.:::****            \n" +
			"\n\n\n\n" +
			"1aboA  NGQGWVPSNYITPVN------ 10\n" +
			"1ycsB  DKEGYVPRNLLGLYP------ 10\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP 4\n";
		

		ClustalTC clustal = new ClustalTC();
		clustal.parse(seqs);
		
		String result = 
			HEADER + "\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n" +
			"\n" +
			"1aboA  NGQGWVPSNYITPVN------\n" +
			"1ycsB  DKEGYVPRNLLGLYP------\n" +
			"1pht   GERGDFPGTYVEYIGRKKISP\n";
		
		
		System.out.println(clustal.toString());;
		assertEquals( result, clustal.toString() );
	}		
}
