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

package commons.util;

/**
 * This class holds arrays related functionalities not found in
 * java.util.Arrays (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ArraysExt {

    /**
     * Given a binary mask on the range of indexes of a given array,
     * extract all the elements that correspond to a bit set to 1 in the mask.
     * E.g.: {"one","two","three","four"} with mask=6 returns {"two","three"}.
     *
     * @param src   the source array
     * @param mask  the mask
     * @return      the sub array of elements whose indexes match the mask
     */
    public static Object[] extractSubArray( Object[] src, int mask ) {

        int numberOfOnes = 0;
        for ( int current=1,i=0 ; i<src.length && current!=0 ; current<<=1,i++ ) {
            if ( (mask & current) != 0 )
                numberOfOnes++;
        }

        Object[] ret = new Object[ numberOfOnes ];
        for ( int current=1,i=0,j=0 ; i<src.length && current!=0 ; current<<=1,i++ ) {
            if ( (mask & current) != 0 )
                ret[j++] = src[i];
        }

        return ret;
    }

    /**
     * Cast an object array into a string array.
     * This method is needed because a simple syntactic cast (String[])
     * throws a runtime java.lang.ClassCastException.
     *
     * @param src  the source object array
     * @return     the corresponding string array
     */
    public static String[] toStringArray( Object[] src ) {
        String[] ret = new String[src.length];
        for ( int i=0 ; i < src.length ; i++ ) {
            ret[i] = (String) src[i];
        }
        return ret;
    }
}
