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

package commons.web;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class filters an input stream to detect a given delimiter.
 * This input stream says the underlying stream ends (i.e. read() return -1)
 * when either the end of the underlying stream is really met
 * or the given delimiter is met.
 *
 * Warning: this class does not work if the first byte of the pattern is not unique.
 * e.g., search for abac in ababac. aba is found and written back.
 * The search is restarted with bac and fails whereas it should have succeeded.
 * This case is avoided by throwing an IllagalArgumentException in the constructor.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class DelimitedInputStream extends FilterInputStream {

    /**
     * The separator delimiting the end of the part is stored
     * as an array of ints rather than bytes.
     * When reading data from the stream, an int is returned.
     * Comparing values greater than 127 leads to problems:
     * the int value is interpreted correctly whereas the byte value
     * is considered negative (the 8th bit correspond to the sign).
     */
    private int[] separator;

    /** The index in separator of the next byte to find in the buffer. */
    private int index = 0;


    public DelimitedInputStream( InputStream in, byte[] separator ) throws IllegalArgumentException {
        super(in);

        for ( int i=1 ; i < separator.length ; i++ ) {
        	if ( separator[i] == separator[0] ) {
        		throw new IllegalArgumentException(
                    "byte (" + Byte.toString(separator[0]) +
                    ") at index 0 must be unique in separator" );
        	}
        }

        this.separator = new int[ separator.length ];
        for ( int i=0 ; i < separator.length ; i++ ) {
        	this.separator[i] = (separator[i]<0) ? 256+separator[i] : separator[i];
        }
    }


    /** True once the separator has been met. */
    private boolean separatormet = false;

    /** To return bytes accumulated in separator (up to index). */
    private int backIndex = 0;

    /**
     * 1st non matching byte read from the input stream
     * after the detection of some separator bytes.
     * -1 if none.
     */
    private int firstNonMatching = -1;

    /**
     * Read an byte from the underlying input stream
     * until either the end of stream is found
     * or the seperator is met.
     *
     * @return  -1 if the end of stream or the separator is met,
     *          the read byte in other cases
     */
    public int read() throws IOException {

        while (true) {
        	
            /**
             * Test whether the separator is met.
             * The separator is either followed by 0xd 0xa
             * or by -- 0xd 0xa (end of stream).
             */
            if (separatormet) {
                int b1 = in.read();
                int b2 = in.read();
                if ( b1=='-' && b2=='-' ) {
                    /** Just skip the end of line. */
                    in.read();
                    in.read();
                }
                return -1;
            }

            if ( firstNonMatching != -1 ) {
            	if ( backIndex < index ) {
                    int ret = separator[backIndex];
            		backIndex++;
                    return ret;
            	}
                int ret = firstNonMatching;
                firstNonMatching = -1;
            	backIndex = 0;
                index = 0;
                return ret;
            }
            
            int b = in.read();
            
            if ( b == -1 ) {
                /**
                 * The end of the stream has been met.
                 * Test whether bytes have been accumulated in the buffer
                 * before returning -1
                 */
                if ( backIndex < index ) {
                    int ret = separator[backIndex];
                    backIndex++;
                    return ret;
                }
                backIndex = 0;
                index = 0;
                return -1;
            }

            if ( b == separator[index] ) {
            	index++;
                if ( index == separator.length ) {
                	separatormet = true;
                }
            }
            else {
                if ( index == 0 ) {
                    return b;
                }
                firstNonMatching = b;
            }
        }

    }


    /**
     * Redefine the inherited close().
     * We don't want to close the underlying input stream
     * when this instance is closed.
     */
    public void close() throws IOException {}
}
