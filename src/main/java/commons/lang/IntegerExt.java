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

package commons.lang;

/**
 * This class holds integer related functionalities not found
 * java.lang.Integer (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class IntegerExt {

    /**
     * Create an array of Integer instances from a array of int values.
     * 
     * @param integers  an array of int values
     * @return          an array of Integer instances
     */
    public static Integer[] create( int[] integers ) {
        
        Integer[] ret = new Integer[ integers.length ];
        for ( int i=0 ; i < ret.length ; i++ ) {
            ret[i] = new Integer(integers[i]);
        }
        return ret;
    }

    /**
     * Return hexadecimal string representations of bytes stored in an array.
     *
     * @param b  the given byte array
     * @return   an array of hexadecimal string representation
     */
    public static String[] toHexString( byte[] b ) {
        String[] ret = new String[b.length];
        for ( int i=0 ; i < b.length ; i++ ) {
            ret[i] = Integer.toHexString(b[i]);
        }
        return ret;
    }

    /**
     * Return hexadecimal string representations of at most length bytes
     * stored in an array.
     *
     * @param b       the given byte array
     * @param length  the maximum number of elements
     * @return        an array of hexadecimal string representation
     */
    public static String[] toHexString( byte[] b, int length ) {
        String[] ret = new String[length];
        for ( int i=0 ; i<b.length && i<length ; i++ ) {
            ret[i] = Integer.toHexString(b[i]);
        }
        return ret;
    }

    /**
     * Return hexadecimal string representations of at most length bytes stored in an array
     * starting at at offset.
     *
     * @param b       the given byte array
     * @param offset  the offset
     * @param length  the maximum number of elements
     * @return        an array of hexadecimal string representation
     */
    public static String[] toHexString( byte[] b, int offset, int length ) {
        String[] ret = new String[length];
        for ( int i=0 ; offset+i<b.length && i<length ; i++ ) {
            ret[i] = Integer.toHexString(b[offset+i]);
        }
        return ret;
    }

}
