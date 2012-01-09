package org.blackcoffee.commons.format;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.blackcoffee.commons.format.Alphabet.AminoAcid;
import org.blackcoffee.commons.format.Alphabet.Dna;
import org.blackcoffee.commons.format.Alphabet.NucleicAcid;
import org.blackcoffee.commons.format.Alphabet.Rna;
import org.junit.Test;

import test.TestHelper;

public class FastaTest {

	@Test
	public void testFasta() throws IOException {
		
		Fasta fasta = new Fasta(AminoAcid.INSTANCE);
		File file = TestHelper.file("/test.fasta");
		assertTrue(file.exists());
		fasta.parse(file);
		
		assertNotNull(fasta.sequences);
		//assertEquals(5, fasta.sequences.size());
		
		assertEquals("1aboA", fasta.sequences.get(0).header);
		assertEquals("NLFVALYDFVASGDNTLSITKGEKLRVLGYNHNGEWCEAQTKNGQGWVPSNYITPVN", fasta.sequences.get(0).value);
		assertEquals(">1aboA\nNLFVALYDFVASGDNTLSITKGEKLRVLGYNHNGEWCEAQTKNGQGWVPS\nNYITPVN\n", fasta.sequences.get(0).toString());
		
		assertEquals("1ycsB", fasta.sequences.get(1).header);
		assertEquals("KGVIYALWDYEPQNDDELPMKEGDCMTIIHREDEDEIEWWWARLNDKEGYVPRNLLGLYP", fasta.sequences.get(1).value);

		assertEquals("1pht", fasta.sequences.get(2).header);
		assertEquals("GYQYRALYDYKKEREEDIDLHLGDILTVNKGSLVALGFSDGQEARPEEIGWLNGYNETTGERGDFPGTYVEYIGRKKISP", fasta.sequences.get(2).value);

		assertEquals("1vie", fasta.sequences.get(3).header);
		assertEquals("DRVRKKSGAAWQGQIVGWYCTNLTPEGYAVESEAHPGSVQIYPVAALERIN", fasta.sequences.get(3).value);

		assertEquals("1ihvA", fasta.sequences.get(4).header);
		assertEquals("NFRVYYRDSRDPVWKGPAKLLWKGEGAVVIQDNSDIKVVPRRKAKIIRD", fasta.sequences.get(4).value);

	}
	
	@Test 
	public void testIsValidFile() {
		assertTrue( Fasta.isValid(TestHelper.file("/test.fasta"), AminoAcid.INSTANCE) );
	}

	@Test 
	public void testNotValidFile() {
		assertFalse( Fasta.isValid(TestHelper.sampleLog(), AminoAcid.INSTANCE) );
		assertFalse( Fasta.isValid(new File("XXX"), AminoAcid.INSTANCE ));
	}

	@Test
	public void testIsValidNucleicString() { 
		assertTrue( Fasta.isValid(">1aboA\nACGTGGCU", NucleicAcid.INSTANCE) );
		assertFalse( Fasta.isValid(">1aboA\nACGTGGCS", NucleicAcid.INSTANCE) );
	}

	@Test
	public void testIsValidDnaString() { 
		assertTrue( Fasta.isValid(">1aboA\nACGTACGT", Dna.INSTANCE) );
		assertFalse( Fasta.isValid(">1aboA\nACGTACGU", Dna.INSTANCE) );
	}

	@Test
	public void testIsValidRnaString() { 
		assertFalse( Fasta.isValid(">1aboA\nACGTACGT", Rna.INSTANCE) );
		assertTrue( Fasta.isValid(">1aboA\nACGUACGU", Rna.INSTANCE) );
	}	
	
	@Test
	public void testIsValidString() { 
		assertTrue( Fasta.isValid(">1aboA\nNLFVALYDFVASGDNTLSITKGEKLRVLGYNHNGEWCEAQTKNGQGWVPSNYITPVN", AminoAcid.INSTANCE) );
	}

	@Test
	public void testIsValidLowerString() { 
		assertTrue( Fasta.isValid(">1aboA\nnlfvalydfvasgdntlsitkgeklrvlgynhngewceaqtkngqgwvpsnyitpvn", AminoAcid.INSTANCE) );
	}
	
	@Test
	public void testNotValidString() { 
		assertFalse( Fasta.isValid("XXXX", AminoAcid.INSTANCE) );
	}
	
	@Test 
	public void testFailValidation() { 
		String seq = "HsLarp6>\nMAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAETRGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENEREDLEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVRRNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLELNEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWALATPQKNGRVQEKVMEHLLKLFGTFGVISSVRILKPGRELPPDIRRISSRYSQVGTQECAIVEFEEVEAAIKAHEFMITESQGKENMKAVLIGMKPPKKKPAKDKNHDEEPTASIHLNKSLNKRVEELQYMGDESSANSSSDPESNPTSPMAGRRHAATNKLSPSGHQNLFLSPNASPCTSPWSSPLAQRKGVSRKSPLAEEGRLNCSTSPEIFRKCMDYSSDSSVTPSGSPWVRRRRQAEMGTQEKSPGTSPLLSRKMQTADGLPVGVLRLPRGPDNTRGFHGHERSRACV";
		assertFalse( Fasta.isValid(seq, AminoAcid.INSTANCE) );
		
	}

	@Test 
	public void testFileWithMultipleLines() { 
		assertTrue( Fasta.isValid( TestHelper.file("/test-fail-2.fasta"), AminoAcid.INSTANCE) );
		
	}

	@Test 
	public void testOkWithMultipleSequences() { 
		String seq = 
			">alpha\n" +
			"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
			"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
			"\n" +
			">beta\n" +
			"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
			"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
			"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n" +
			"\n" +
			"\n" +
			"\n" +
			">gamma\n" +
			"ATPQKNGRVQEKVMEHLLKLFGTFGVISSVRILKPGRELP";
		
		Fasta fasta = new Fasta(AminoAcid.INSTANCE);
		fasta.parse(seq);
		 
		assertTrue(fasta.isValid());
		assertEquals("alpha", fasta.sequences.get(0).header);
		assertEquals("MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAETRGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED", fasta.sequences.get(0).value);

		assertEquals("beta", fasta.sequences.get(1).header);
		assertEquals("LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVRRNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLELNEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL", fasta.sequences.get(1).value);

		assertEquals("gamma", fasta.sequences.get(2).header);
		assertEquals("ATPQKNGRVQEKVMEHLLKLFGTFGVISSVRILKPGRELP", fasta.sequences.get(2).value);
		
	}

	
	@Test 
	public void testOkWithBlanks() { 
		String seq = 
			">alpha\n" +
			"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n\n" +
			"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
			"\n" +
			">beta\n" +
			"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n\n\n" +
			"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
			"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n\n" +
			"\n" +
			"\n" +
			"\n" +
			">gamma\n" +
			"ATPQKNGRVQEKVMEHLLKLFGTFGVISSVRILKPGRELP";
		
		Fasta fasta = new Fasta(AminoAcid.INSTANCE);
		fasta.parse(seq);
		 
		assertTrue(fasta.isValid());
		assertEquals("alpha", fasta.sequences.get(0).header);
		assertEquals("MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAETRGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED", fasta.sequences.get(0).value);

		assertEquals("beta", fasta.sequences.get(1).header);
		assertEquals("LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVRRNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLELNEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL", fasta.sequences.get(1).value);

		assertEquals("gamma", fasta.sequences.get(2).header);
		assertEquals("ATPQKNGRVQEKVMEHLLKLFGTFGVISSVRILKPGRELP", fasta.sequences.get(2).value);
		
	}	
	
	
	@Test 
	public void testMissingName() { 
		String seq = 
			"> \n" +
			"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
			"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
			"\n" +
			">beta\n" +
			"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
			"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
			"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n";
		
		Fasta fasta = new Fasta(AminoAcid.INSTANCE);
		fasta.parse(seq);
		 
		assertFalse(fasta.isValid());
		
	}
	

	@Test 
	public void testWrongFormat() { 
		String seq = 
			"alpha>\n" +
			"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
			"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
			"\n" +
			">beta\n" +
			"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
			"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
			"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n";
		
		Fasta fasta = new Fasta(AminoAcid.INSTANCE);
		fasta.parse(seq);
		 
		assertFalse(fasta.isValid());

		seq = 
			">alpha\n" +
			"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
			"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
			"\n" +
			"beta>\n" +
			"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
			"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
			"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n";	
		
		fasta = new Fasta(AminoAcid.INSTANCE);
		fasta.parse(seq);
		 
		assertFalse(fasta.isValid());
		
	}	
	
	@Test 
	public void testWrongInput() { 
		File  file = TestHelper.file("/input-2368513839367721000.txt");
		assertNotNull(file);
		assertFalse(Fasta.isValid(file, AminoAcid.INSTANCE));
	}
	
	@Test public void testWrongInout2() { 
		assertFalse( Fasta.isValid(TestHelper.file("/input-5321591033811707368.txt"), AminoAcid.INSTANCE) );
	}
	
	@Test 
	public void testFileRead() throws FileNotFoundException { 
		File  file = TestHelper.file("/sample-proteins.fa");
		Fasta fasta = Fasta.read(file);
		assertTrue( fasta.isValid() );
		
	}
	
	@Test 
	public void testRNA() { 
		assertFalse(Fasta.isValid(TestHelper.file("/sample-proteins.fa"), NucleicAcid.INSTANCE));
		assertTrue(Fasta.isValid(TestHelper.file("/sample-rna.fa"), NucleicAcid.INSTANCE));
	}
	
	@Test
	public void testHaderWithAnyCharacters() { 
			String seq = 
				">x t y  y dsa  d sfs | SA D\n" +
				"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
				"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
				"\n" +
				">beta\n" +
				"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
				"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
				"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n";
			
			Fasta fasta = new Fasta(AminoAcid.INSTANCE);
			fasta.parse(seq);
			 
			assertTrue(fasta.isValid());
			assertEquals("x t y  y dsa  d sfs | SA D", fasta.sequences.get(0).header);
			assertEquals("beta", fasta.sequences.get(1).header);
			
	}
	
	@Test
	public void testNormalization() { 
			String seq = 
				">alpha\n" +
				"MAQSGGEARPGPKTAVQIRV AIQEAEDVDELEDEEEGAET\n" +
				"RGAGDPARYLSPGWGSASEE EPSRGHSGTTASGGENERED\n" +
				"\n" +
				">beta\n" +
				"LEQEWKPPDEELIKKLVDQI EFYFSDENLEKDAFLLKHVR\n" +
				"RNKLGYVSVKLLTSFKKVKH LTRDWRTTAHALKYSVVLEL\n" +
				"NEDHRKVRRTTPVPLFPNEN LPSKMLLVYDLYLSPKLWAL\n";
			
			Fasta fasta = new Fasta(AminoAcid.INSTANCE);
			fasta.parse(seq);
			 
			assertTrue(fasta.isValid());
			
			
			String normalized = 
				">alpha\n" +
				"MAQSGGEARPGPKTAVQIRVAIQEAEDVDELEDEEEGAET\n" +
				"RGAGDPARYLSPGWGSASEEEPSRGHSGTTASGGENERED\n" +
				">beta\n" +
				"LEQEWKPPDEELIKKLVDQIEFYFSDENLEKDAFLLKHVR\n" +
				"RNKLGYVSVKLLTSFKKVKHLTRDWRTTAHALKYSVVLEL\n" +
				"NEDHRKVRRTTPVPLFPNENLPSKMLLVYDLYLSPKLWAL\n";

			assertEquals( normalized, fasta.toString() );
	}
}
