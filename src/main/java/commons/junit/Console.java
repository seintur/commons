/***
 * Commons
 * Copyright (C) 2015 University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Lionel.Seinturier@univ-lille1.fr
 *
 * Author: Lionel Seinturier
 */

package commons.junit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

/**
 * This class provides a console which stores and compares characters with an
 * expected content.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Console {

	// --------------------------------------------------------------------
	// Factory method for creating consoles
	// --------------------------------------------------------------------
	
	/**
	 * Factory method for {@link Console}s.
	 */
	public static Console getConsole( String key ) throws IOException {
		
		Console console = consoles.get(key);
		if( console == null ) {
			console = new Console(key);
			consoles.put(key,console);
		}
    	return console;
    }

	/**
	 * The map of registered consoles.
	 */
	private static Map<String,Console> consoles = new HashMap<>();
	
	
	// --------------------------------------------------------------------
	// Console instance
	// --------------------------------------------------------------------
	
	private String prefix;
	private File tmp;
    private PrintStream ps = System.err;
    
    /**
     * Initialize the console.
     * 
     * @param prefix  the name prefix for the temporary file.
     */
    private Console( String p ) throws IOException {
    	this.prefix = p;
        tmp = File.createTempFile(prefix,".txt");
        ps = new PrintStream(tmp);
    }
    
    /**
     * Store a character in the console.
     * 
     * @param c  the character
     */
    public void print( char c ) {
    	ps.print(c);
    }
    
    /**
     * Store a message in the console.
     * 
     * @param msg  the message
     */
    public void println( String msg ) {
        ps.println(msg);
    }
    
    /**
     * Dump the content of the console in the specified stream.
     * 
     * @param dst  the stream
     */
    public void dump( PrintStream dst ) throws IOException {
        FileReader fr = new FileReader(tmp);
        int b;
        while( (b=fr.read()) != -1 ) {
            char c = (char) b;
            dst.print(c);
        }
        fr.close();    	
    }
    
    /**
     * Check that the characters stored in the console are equals to the
     * specified array of strings.
     * 
     * @throws AssertionError  if this is not the case
     */
    public void assertEquals( String[] expecteds )
    throws AssertionError, IOException {
    	
    	/*
    	 * Create a temporary file with the expected content.
    	 */
        File tmpExpected = File.createTempFile(prefix,".txt");
        PrintStream ps = new PrintStream(tmpExpected);
        for (String expected : expecteds) {
            ps.println(expected);
        }
        ps.close();
        
        /*
         * Compare the characters stored in the console with the expected
         * content.
         */
        FileReader fr = new FileReader(tmp);
        FileReader frExpected = new FileReader(tmpExpected);
        int line=1,col=1,b;
        while( (b=fr.read()) != -1 ) {
            char c = (char) b;
            char e = (char) frExpected.read();
            if( c != e ) {
            	System.err.println("Output is: ");
            	dump(System.err);
            	System.err.println();
                System.err.println("Expected output is: ");
                for (String expected : expecteds) {
                    System.err.println(expected);
                }
                final String msg =
                	"Unexpected character '"+c+"' instead of '"+e+"' at line "+
                	line+", column "+col;
                Assert.fail(msg);
            }
            if( c == '\n' ) {
                line++;
                col=1;
            }
            else {
                col++;
            }
        }
        frExpected.close();
        fr.close();
        
        /*
         * Delete temporary files.
         */
        tmp.delete();
        tmpExpected.delete();    	
    }
    
    /**
     * Close the console and remove it from the map of registered consoles.
     */
    public void close() {
    	ps.close();
    	consoles.remove(prefix);
    }
}
