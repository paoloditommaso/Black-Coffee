package org.blackcoffee.command;

import static org.junit.Assert.*;

import org.blackcoffee.TestCase;
import org.junit.Before;
import org.junit.Test;

public class TcoffeeCommandTest {

	private TcoffeeCommand cmd;

	private TestCase test ;
	
	@Before
	public void before() { 
		test = new TestCase(null);
	}
	
	@Test 
	public void testTCoffeeCommand() { 
		String test = "file.fasta -mode expresso" ;
		cmd = new TcoffeeCommand(test);
		
		assertEquals( "t_coffee", cmd.program );
		assertEquals( "file.fasta", cmd.getArgument(0) );
		assertEquals( "expresso", cmd.getOption("mode"));
		
	}
	

	
	@Test 
	public void testBeforeRun() { 
		cmd = new TcoffeeCommand("sample_seq1.fasta  ");
		cmd.configure(test);
		assertTrue( test.input.contains("sample_seq1.fasta") );
	}	


	@Test 
	public void testBeforeRun2() { 
		cmd = new TcoffeeCommand("-aln=sproteases_small.cw_aln, sproteases_small.muscle, sproteases_small.tc_aln -outfile=combined_aln.aln");
		cmd.configure(test);
		
		assertTrue( test.input.contains("sproteases_small.cw_aln") );
		assertTrue( test.input.contains("sproteases_small.muscle") );
		assertTrue( test.input.contains("sproteases_small.tc_aln") );
	}
	
	

	
	@Test 
	public void testBeforeRun3() { 
		cmd = new TcoffeeCommand(
				"-other_pg seq_reformat -in sproteases_small.aln -output msf > sproteases_small.msf ");
		cmd.configure(test);
		
		assertTrue( test.input.contains("sproteases_small.aln") );
	}

	
	@Test 
	public void testBeforeRun4() { 
		cmd = new TcoffeeCommand(
				"-other_pg seq_reformat -decode sproteases_large.code_name -in sproteases_large.coded.fasta");
		cmd.configure(test);
		
		assertTrue( test.input.contains("sproteases_large.code_name") );
		assertTrue( test.input.contains("sproteases_large.coded.fasta"));
	}
	 
 
	
	@Test 
	public void testBeforeRun5() { 
		cmd = new TcoffeeCommand(
				"-in=sample_aln6.aln -struc_in=sample_lib5.tc_lib -output=color_html -out=x.html");
		cmd.configure(test);
		
		assertTrue( test.input.contains("sample_aln6.aln") );
		assertTrue( test.input.contains("sample_lib5.tc_lib"));
	}
}
