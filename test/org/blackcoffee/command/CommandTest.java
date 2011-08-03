package org.blackcoffee.command;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

public class CommandTest {

	private Command cmd;

	@Test
	public void testCommandLine() { 
		String cmdline = "t_coffee sample.fasta -flag -mode expresso";
		
		cmd = new Command(cmdline);

		assertEquals( "t_coffee", cmd.program );
		assertTrue( cmd.options.containsKey("flag") );
		assertEquals( null, cmd.getOption("flag") );
		assertEquals( "expresso", cmd.getOption("mode") );
		assertEquals( "sample.fasta", cmd.args.get(0) );

		assertEquals( cmdline, cmd.toString());

	}
	
	@Test 
	public void testCommandLong() { 
		String cmdline = "t-coffee input.fa \n-mode=something -about -output html score ascii -multi_core=4 -flag";
		cmd = new Command(cmdline);

		
		assertEquals( "t-coffee", cmd.program );
		assertEquals( "input.fa", cmd.args.get(0) );	

		assertEquals( "something", cmd.getOption("mode") );

		assertTrue( cmd.hasOption("about") );
		assertEquals( null, cmd.getOption("about") );

		assertEquals( "html", cmd.getOptionsList("output").get(0));
		assertEquals( "score", cmd.getOptionsList("output").get(1));
		assertEquals( "ascii", cmd.getOptionsList("output").get(2));

		assertEquals( "4", cmd.getOption("multi_core") );
	
		assertTrue( cmd.hasOption("flag") );
		assertEquals( Collections.emptyList(), cmd.getOptionsList("flag") );
	
		assertEquals( cmdline, cmd.toString());

	}
	
	@Test 
	public void testCommandRepeat() { 
		String cmdline = "t-coffee -in file1 -in file2 -in file3";
		cmd = new Command(cmdline);

		
		assertEquals( "t-coffee", cmd.program );

		assertEquals( "file1", cmd.getOptionsList("in").get(0) );		
		assertEquals( "file2", cmd.getOptionsList("in").get(1) );		
		assertEquals( "file3", cmd.getOptionsList("in").get(2) );		

		assertEquals( cmdline, cmd.toString());
	}

	
	@Test 
	public void testCommandPipe() { 
		String cmdline = "t-coffee -in file1 -in file2 -in file3 | blah blah";
		cmd = new Command(cmdline);

		
		assertEquals( "t-coffee", cmd.program );

		assertEquals( "file1", cmd.getOptionsList("in").get(0) );		
		assertEquals( "file2", cmd.getOptionsList("in").get(1) );		
		assertEquals( "file3", cmd.getOptionsList("in").get(2) );		
		
		
		assertEquals( cmdline, cmd.toString());
	}	
	
	@Test 
	public void testArguments() { 

		String cmdline = "t-coffee arg1 arg2 arg3 -in file1 -method expresso";
		cmd = new Command(cmdline);

		
		assertEquals( "t-coffee", cmd.program );

		assertEquals( "file1", cmd.getOption("in") );		
		assertEquals( "expresso", cmd.getOption("method") );		

		assertEquals( "arg1", cmd.getArgument(0) );
		assertEquals( "arg2", cmd.getArgument(1) );
		assertEquals( "arg3", cmd.getArgument(2) );
		assertEquals( null, cmd.getArgument(3) );
		
		
		assertEquals( cmdline, cmd.toString());
	
	}
	
	
	@Test 
	public void testCmdComplex()  {
		String cmdline = "t_coffee -other_pg seq_reformat -in sproteases_small.aln -action +extract_seq_list 'sp|P29786|TRY3_AEDAE' 'sp|P35037|TRY3_ANOGA'";

		cmd = new Command(cmdline);

		
		assertEquals( "t_coffee", cmd.program );

		assertEquals( "seq_reformat", cmd.getOption("other_pg") );		
		assertEquals( "sproteases_small.aln", cmd.getOption("in") );		
		assertEquals( "+extract_seq_list", cmd.getOptionsList("action").get(0) );		
		assertEquals( "'sp|P29786|TRY3_AEDAE'", cmd.getOptionsList("action").get(1) );		
		assertEquals( "'sp|P35037|TRY3_ANOGA'", cmd.getOptionsList("action").get(2) );		
		
	
	}

	
	@Test 
	public void testCmdGreat() { 
		String cmdline = "t_coffee -other_pg seq_reformat -in 3d_sample4.aln -action +upper -output clustalw > 3d_sample4.cw_aln ";
		cmd = new Command(cmdline);

	}
}
