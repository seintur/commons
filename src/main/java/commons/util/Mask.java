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

import java.util.StringTokenizer;

/**
 * This class manages binary masks.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Mask {

    /**
     * Given a string of the form "99;99-99;99-" defining a set of integer ranges
     * return the corresponding binary mask assuming each integer represents
     * a position in the mask.
     * E.g.: "2-4;6;9-" with limit=11 returns 111 0010 1110
     *
     * @param src         the source string
     * @param limit       the end element for unending ranges (e.g. 56-)
     * @return            the corresponding binary mask
     */
    public static int getMask( String src, int limit )
	throws NumberFormatException {
        return getMask( src, ";", "-", limit );
    }

    /**
     * Given a string of the form "99;99-99;99-" defining a set of integer ranges
     * return the corresponding binary mask assuming each integer represents
     * a position in the mask.
     * E.g.: "2-4;6;9-" with limit=11 returns 111 0010 1110
     *
     * @param src         the source string
     * @param setDelim    the set elements delimiter (usually ;)
     * @param rangeDelim  the range delimiter (usually -)
     * @param limit       the end element for unending ranges (e.g. 56-)
     * @return            the corresponding binary mask
     */
    public static int getMask(
		String src, String setDelim, String rangeDelim, int limit )
    throws NumberFormatException {

        int mask = 0;
        StringTokenizer st = new StringTokenizer( src, setDelim );

        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( token, rangeDelim );
            if ( ! st2.hasMoreTokens() )  continue;

            String beginAsString = st2.nextToken();
            int begin = Integer.parseInt(beginAsString);
            int end = limit;
            if ( st2.hasMoreTokens() ) {
                end = Integer.parseInt( st2.nextToken() );
            }
            else if ( beginAsString.length() == token.length() ) {
                end = begin;
            }
            for ( int i=begin ; i <= end ; i++ ) {
                mask |= 1<<i;
            }
        }

        return mask;
    }
}
