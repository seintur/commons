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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class provides methods for accessing <name,value> pairs stored in a
 * string. Pairs are separated by a delimiter string such as the colon
 * character. A name is separated from its value by a separator string such as
 * the equal character. Both the delimiter and the separator can be specified
 * when instantiating this class.
 * 
 * An example of a parameters string which can be parsed by this class follows:
 *   src=foo/bar:dst=bin
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ParametersString implements Iterable<String> {
    
    private List<String> keys = new ArrayList<String>();
    private Map<String,String> values = new HashMap<String, String>();
    
    public void parse( String str, String delim, String separator ) {
        
        StringTokenizer st = new StringTokenizer(str,delim);
        while( st.hasMoreTokens() ) {
            String token = st.nextToken();
            int last = token.lastIndexOf(separator);
            if( last == -1 ) {
                // No value, only a key
                keys.add(token);
                values.put(token,null);
            }
            else {
                String key = token.substring(0,last);
                String value =
                    last+1 == token.length() ? "" : token.substring(last+1);
                keys.add(key);
                values.put(key, value);
            }
        }
    }

    public boolean containsKey( String key ) {
        return values.containsKey(key);
    }
    
    public String get( String key ) {
        return values.get(key);
    }
    
    /** Return the key stored at the specified index in the parameters string. */
    public String getKeyAt( int i ) {
        return keys.get(i);
    }
    
    /** Return the number of parameters stored in the parameters string. */
    public int size() {
        return keys.size();
    }

    public Iterator<String> iterator() {
        return keys.iterator();
    }
    
    public void put( String key, String value ) {
        keys.add(key);
        values.put(key, value);
    }
}
