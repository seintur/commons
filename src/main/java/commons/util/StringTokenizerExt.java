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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class holds StringTokenizer related functionalities.
 * not found in java.util.StringTokenizerExt
 * (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class StringTokenizerExt extends StringTokenizer {

    public StringTokenizerExt( String str ) { super(str); }
    public StringTokenizerExt( String str, String delim ) { super(str,delim); }
    public StringTokenizerExt( String str, String delim, boolean returnDelims )
    { super(str,delim,returnDelims); }
    
    /**
     * Return all the tokens as an array of strings.
     */
    public String[] nextTokens() {

        List<String> ret = new LinkedList<String>();
        while( hasMoreElements() ) {
            ret.add(nextToken());
        }
        return ret.toArray(ref);
    }
    
    private static final String[] ref = new String[0];
}
