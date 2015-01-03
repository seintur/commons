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

/**
 * This class reads data from an array of arrays of bytes.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ByteArraysInputStream extends InputStream {

    /** The array of arrays where the bytes are to be read. */
    private byte[][] arrays;

    /** The index used to iterate on the array of arrays. */
    private int i = 0;

    /** The index used to iterate on each array of bytes. */
    private int j = 0;

    public ByteArraysInputStream( byte[][] arrays ) {
        this.arrays = arrays;
    }

    public int read() throws IOException {
        if ( i == arrays.length )  return -1;

        byte bb = arrays[i][j];
        int b = (bb<0) ? 256+bb : bb;

        /**
         * Compute the index of the next byte to read.
         * while loop in case the next array size is 0.
         */
        j++;
        while ( i < arrays.length && j == arrays[i].length ) {
            i++;
            j=0;
        }

        return b;
    }
}