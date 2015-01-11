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

package commons.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class filters an output stream by replacing a given pattern of bytes
 * by another one.
 *
 * <p>
 * Warning: this class does not work if the first byte of the pattern
 * is not unique.
 * E.g. search for abac in ababac:
 * <ul>
 * <li>aba is found and written back as the next byte is b (not c),</li>
 * <li>the search restarts at b (with bac) and fails.</li>
 * </ul>
 * </p>
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class FindAndReplaceOutputStream extends FilterOutputStream {

    private byte[] find;
    private byte[] replace;

    /**
     * The index in find of bytes matching those found
     * so far in the output stream.
     */
    private int index;

    public FindAndReplaceOutputStream(
            OutputStream os, byte[] find, byte[] replace ) {
        
        super(os);

        this.find = find;
        this.replace = replace;
        index = 0;
    }

    public FindAndReplaceOutputStream(
            OutputStream os, String find, String replace ) {
        
        this( os, find.getBytes(), replace.getBytes() );
    }

    /**
     * Construct a chain of FindAndReplaceOutputStream instances.
     * Each element in the chain performs a different find&replace operation.
     *
     * @param os       the output stream to filter
     * @param find     an array containing the strings to find
     * @param replace  an array containing the replacement strings
     * @return         a FindAndReplaceOutputStream instance performing
     *                 all the find&replace operations.
     */
    public static FindAndReplaceOutputStream create(
            OutputStream os, String[] find, String[] replace ) 
        throws IllegalArgumentException {

        if ( find.length == 0 ) {
            throw new IllegalArgumentException(
                    "2nd parameter must be an array of length at least 1");
        }
        if ( find.length != replace.length ) {
            throw new IllegalArgumentException(
                 "2nd and 3rd parameters must be an array of the same length");
        }

        FindAndReplaceOutputStream[] faroses =
            new FindAndReplaceOutputStream[ find.length ];
        faroses[faroses.length - 1] =
            new FindAndReplaceOutputStream(
                os, find[faroses.length-1], replace[faroses.length-1]
            );

        for ( int i=faroses.length-2 ; i >= 0 ; i-- ) {
            faroses[i] =
                new FindAndReplaceOutputStream(faroses[i+1],find[i],replace[i]);
        }

        return faroses[0];
    }

    @Override
    public void close() throws IOException {
        if ( index > 0 ) {
            super.write(find,0,index);
        }
        super.close();
    }

    
//  Useless: this seems to be what
//  super.write(byte[]) or super.write(byte[],int,int) do
//  public void write( byte[] b ) throws IOException {
//      for ( int i=0 ; i < b.length ; i++ ) {
//          write(b[i]);
//      }
//  }
//
//  public void write( byte[] b, int off, int len ) throws IOException {
//      for ( int i=off ; i<b.length && i<len ; i++ ) {
//          write(b[i]);
//      }
//  }

    
    // The code of this method must never call
    // super.write(byte[]) or super.write(byte[],int,int).
    // These two methods rely on calls to write(int) which,
    // with FindAndReplaceOutputStream instances,
    // is (re)dispatched to this method.
    // Only call super.write(int).

    public void write( int b ) throws IOException {
        if ( writeAndFind(b) ) {
            // find has been found
            // Write replace
            for ( int i=0 ; i < replace.length ; i++ ) {
                super.write(replace[i]);
            }
        }
    }

    // The code of this method must never call
    // super.write(byte[]) or super.write(byte[],int,int).
    // These two methods rely on calls to write(int) which,
    // with FindAndReplaceOutputStream instances,
    // is (re)dispatched to this method.
    // Only call super.write(int).

    /**
     * @param b  the byte to write
     * @return   true if find has been found
     */
    public boolean writeAndFind( int b ) throws IOException {

        if ( find.length == 0 ) {
            // Nothing is to be found.
            // This is rather dumb, but we return false,
            // unless true should be more "logical"
            // (nothing to find is always found).
            super.write(b);
            return false;
        }

        // Only the 8 low-order bits of the int are written.
        byte octet = (byte) b;

        if ( octet == find[index] ) {
            // Another byte has been found.
            // Accumulate it.
            index++;
            if ( index == find.length ) {
                // All bytes have been found.
                index = 0;
                return true;
            }
        }
        else {
            if ( index > 0 ) {
                /**
                 * Previous bytes matched the begining of find.
                 * Write them if needed.
                 *
                 * Caution: we should restart the search
                 * with bytes written back.
                 * eg, search for abac in ababac.
                 * - aba is found and written back.
                 * - the search is restarted with bac and fails
                 * whereas it should have succeeded.
                 */
                for ( int i=0 ; i < index ; i++ ) {
                    super.write(find[i]);
                }
                index = 0;
            }

            if ( octet == find[0] ) {
                // The 1st byte has been found
                if ( find.length == 1 ) {
                    // The array of bytes to find contains only 1 element
                    return true;
                }
                index = 1;
            }
            else {
                super.write(b);
            }
        }

        return false;
    }
}
