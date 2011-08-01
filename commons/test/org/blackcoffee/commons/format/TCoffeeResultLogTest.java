package org.blackcoffee.commons.format;

import static org.junit.Assert.*;

import org.blackcoffee.commons.format.TCoffeeResultLog.FileItem;
import org.junit.Test;

public class TCoffeeResultLogTest {


	@Test
	public void testParseResultItem() { 
        String TEST = " #### File Type=        MSA Format= clustalw_aln Name= tcoffee.clustalw_aln";

        /* run parsing method */
        FileItem item = TCoffeeResultLog.parseForResultFileItem(TEST);

        assertNotNull(item);
        assertEquals( "MSA", item.type );
        assertEquals( "clustalw_aln", item.format );
        assertEquals( "tcoffee.clustalw_aln", item.name );


        assertNull(TCoffeeResultLog.parseForResultFileItem("#### Invalid format"));
		
	}
	

}
