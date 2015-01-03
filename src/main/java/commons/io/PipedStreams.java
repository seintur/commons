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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class reads data from input streams (one after the other)
 * and writes back data to an output stream.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class PipedStreams {

    /**
     * Read bytes from an input stream and dump them into an output stream.
     *
     * @param is  the input stream
     * @param os  the output stream
     * @return    the number of bytes dumped
     */
    public static int dump( InputStream is, OutputStream os )
    throws IOException {
        
        int b,len;
        for ( len=0 ; (b=is.read()) != -1 ; len++ ) {
            os.write(b);
        }
        return len;
    }

    /**
     * Read bytes from an input stream and dump them into an output stream.
     * A buffer is provided to dump bytes from the input to the output.
     *
     * @param is      the input stream
     * @param os      the output stream
     * @param buffer  a buffer to perform the dump
     * @return        the number of bytes dumped
     */
    public static int dump( InputStream is, OutputStream os, byte[] buffer )
    throws IOException {
        
        int b,len;
        for ( len=0 ; (b=is.read(buffer)) != -1 ; len+=b ) {
            os.write(buffer,0,b);
        }
        return len;
    }

    /**
     * Read bytes from input streams and dump them into an output stream.
     *
     * @param is  an array of input streams
     * @param os  the output stream
     * @return    the number of bytes dumped
     */
    public static int dump( InputStream[] is, OutputStream os )
    throws IOException {
        
        int i,len;
        for ( i=0,len=0 ; i < is.length ; i++ ) {
            len += dump(is[i],os);
        }
        return len;
    }
}
