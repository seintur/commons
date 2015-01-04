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

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class filters an output stream by replacing a block of bytes delimited
 * by two arrays of bytes (begin and end), by a third one (replace).
 * begin and end arrays are also replaced.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class FindBlockAndReplaceOutputStream extends FilterOutputStream {

    protected OutputStream os;
    protected byte[] begin;
    protected byte[] end;
    protected byte[] replace;

    private FindAndReplaceOutputStream beginos;
    private FindAndReplaceOutputStream endos;

    /** True if we are between begin (included) and find (included). */
    private boolean found;

    /**
     * Used to accumulate bytes once begin has been found.
     * If end is found these bytes are discarded.
     * If, once close() is called, begin has been found and end not,
     * bytes stored in baos are written to the output stream.
     */
    private ByteArrayOutputStream baos;

    public FindBlockAndReplaceOutputStream(
            OutputStream os, byte[] begin, byte[] end, byte[] replace ) {
        
        super(os);

        found = false;
        baos = new ByteArrayOutputStream();

        this.os = os;
        this.begin = begin;
        this.end = end;
        this.replace = replace;

        this.beginos = new FindAndReplaceOutputStream(os,begin,null);
        this.endos = new FindAndReplaceOutputStream(baos,end,null);
    }

    public FindBlockAndReplaceOutputStream(
            OutputStream os, String begin, String end, String replace ) {
        
        this( os, begin.getBytes(), end.getBytes(), replace.getBytes() );
    }

    @Override
    public void close() throws IOException {
        if (found) {
            // begin has been found, but not end.
            // Write to the output stream begin and bytes stored in baos.
            os.write(begin);
            os.write( baos.toByteArray() );
        }
        os.close();
    }

    @Override
    public void flush() throws IOException {
        beginos.flush();
        endos.flush();
    }


    // The code of this method must never call super.write(byte[]) or
    // super.write(byte[],int,int).
    // This two methods rely on calls to write(int)
    // which, with FindAndReplaceOutputStream instances,
    // is (re)dispatched to this method.
    // Only call super.write(int).
    @Override
    public void write( int b ) throws IOException {
        if ( !found ) {
            // begin not found
            // Search for it and dump (begin itself not dumped if found)
            found = beginos.writeAndFind(b);
        }
        else {
            // begin found.
            // Search for end.
            // found stays true until end is found.
            // Once end has been found, write replace.
            found = ! endos.writeAndFind(b);
            if (!found) {
                // end has been found, write replace
                beginos.write(replace);
                baos.reset();
            }
        }
    }
}
