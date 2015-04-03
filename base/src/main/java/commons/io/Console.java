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
 * Contributor: Victor Noel
 */

package commons.io;

import java.io.CharArrayWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a console that stores and compares characters with an
 * expected content.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @author Victor Noel <victor.noel@linagora.fr>
 */
public class Console extends PrintWriter {

	// --------------------------------------------------------------------
	// Factory method for creating consoles
	// --------------------------------------------------------------------
	
	/**
	 * Factory method for {@link Console}s.
	 */
	public static Console getConsole( String key ) {		
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
	private static Map<String,Console> consoles = new HashMap<String,Console>();
	
	
	// --------------------------------------------------------------------
	// Console instance
	// --------------------------------------------------------------------
	
	private String prefix;
    
    /**
     * Initialize the console.
     * 
     * @param prefix  the name prefix for the temporary file.
     */
    private Console( String p ) {
    	super( new CharArrayWriter(64) );
    	this.prefix = p;
    }
    
    /**
     * Dump the content of the console in the specified stream.
     */
    public void dump( PrintStream dst ) {
    	char[] array = ((CharArrayWriter) out).toCharArray();
    	dst.print(array);
    }
    
    /**
     * Check that the characters stored in the console are equals to the
     * specified array of strings.
     * 
     * @throws IllegalArgumentException
     * 		if the console does not contain the expected strings
     */
    public void assertEquals( String[] expecteds )
    throws IllegalArgumentException {
    	
    	int idx = 0;
    	char[] array = ((CharArrayWriter) out).toCharArray();
    	
    	// Always terminate the content of the console with a newline character
    	if( array[array.length - 1] != '\n' ) {
    		char[] dest = new char[array.length + 1];
    		System.arraycopy(array,0,dest,0,array.length);
    		dest[array.length] = '\n';
    		array = dest;
    	}
    	
    	for (int line=0; line < expecteds.length; line++) {
			for (int col = 0; col < expecteds[line].length() + 1; col++) {

				// Check whether there is fewer characters in the console than expected
				if( idx >= array.length ) {
		            dumpFails(expecteds);
		            final String msg =
		        		"Unexpected end of log at line "+(line+1)+", column"+
		        		(col+1);
		            throw new IllegalArgumentException(msg);
				}
				
				char expected = 
					col == expecteds[line].length() ?
					'\n' : expecteds[line].charAt(col);
				char actual = array[idx];
				
				// Check whether the current character in the console differs
				// from the expected one 
				if( actual != expected ) {
	            	dumpFails(expecteds);
	                final String msg =
	                	"Unexpected character '"+actual+"' instead of '"+
            			expected+"' at line "+(line+1)+", column "+(col+1);
	                throw new IllegalArgumentException(msg);
				}
				
				idx++;
			}
		}
    	
    	// Check whether there is more character in the console than expected 
    	if( idx < array.length ) {
            dumpFails(expecteds);
            final String msg =
        		"Extra characters in log at line "+expecteds.length+", column"+
				expecteds[expecteds.length-1].length();
            throw new IllegalArgumentException(msg);
    	}
    }

    private void dumpFails( String[] expecteds ) {
        System.err.println("Output is: ");
        dump(System.err);
        System.err.println();
        System.err.println("Expected output is: ");
        for (String expected : expecteds) {
            System.err.println(expected);
        }
    }
    
    /**
     * Remove it from the map of registered consoles.
     */
    @Override
    public void close() {
    	// super.close() sets out to null. Do not call it since we want to 
    	// retain the content for later use with assertEquals(String[]).
    	consoles.remove(prefix);
    }
}
