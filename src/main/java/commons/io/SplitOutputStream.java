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

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class filters an output stream
 * until a given pattern of bytes has been found.
 * 
 * All bytes before the pattern (excluding the pattern itself) are written
 * to a given output stream.
 * All bytes after the pattern are written to another one.
 * The pattern is not written anywhere. 
 *
 * <p>
 * Warning: this class does not work if the first byte of the pattern
 * is not unique.
 * eg, search for abac in ababac:
 * <ul>
 * <li>aba is found and written back as the next byte is b (not c),</li>
 * <li>the search restarts at b (with bac) and fails,
 *     whereas it should have succeeded.</li>
 * </ul>
 * </p>
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class SplitOutputStream extends OutputStream {

    /** The pattern of bytes to find. */
    private byte[] find;
    
    /** The output stream where bytes before the pattern are written. */ 
    private OutputStream osBefore;

    /** The output stream where bytes after the pattern are written. */ 
    private OutputStream osAfter;
    
    /**
     * The index in find of bytes matching those found
     * so far in the output stream.
     */
    private int index;

    public SplitOutputStream(
            byte[] find,
            OutputStream osBefore,
            OutputStream osAfter ) {
        
        if ( find == null )
            throw new IllegalArgumentException(
                    "Parameter 1 (find) shouldn't be null");
        if ( find.length == 0 )
            throw new IllegalArgumentException(
                    "Parameter 1 (find) length shouldn't be 0");
        
        this.find = find;
        index = 0;
        
        this.osBefore = osBefore;
        this.osAfter = osAfter;
    }

    public SplitOutputStream(
            String find,
            OutputStream osBefore,
            OutputStream osAfter ) {
        this( find.getBytes(), osBefore, osAfter );
    }

    
    /**
     * @return  true if the pattern has been found.
     */
    public boolean found() {
        return ( index == find.length );
    }
    
    // The code of this method must never call
    // super.write(byte[]) or super.write(byte[],int,int).
    // These two methods rely on calls to write(int) which,
    // with FindAndReplaceOutputStream instances,
    // is (re)dispatched to this method.
    // Only call super.write(int).

    public void write( int b ) throws IOException {

        // All bytes have been found.
        if ( found() ) {
            osAfter.write(b);
            return;
        }
        
        // Only the 8 low-order bits of the int are written.
        byte octet = (byte) b;

        if ( octet == find[index] ) {
            // Another byte has been found.
            // Accumulate it.
            index++;
        }
        else {
            if ( index > 0 ) {
                /*
                 * Previous bytes matched the begining of find.
                 * Write them back.
                 *
                 * Caution: we should restart the search
                 * with bytes written back.
                 * eg, search for abac in ababac:
                 * - aba is found and written back,
                 * - the search restarts with bac and fails
                 * whereas it should have succeeded.
                 */
                osBefore.write(find,0,index);
                index = (octet==find[0]) ? 1 : 0;
            }

            osBefore.write(b);
        }
    }
}
