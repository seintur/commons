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

package commons.util.function;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility methods for filtering arrays and collections.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Filters {

    /**
     * Return the elements from src which match the specified filter.
     * 
     * @param <T>     the type of the elements
     * @param src     the source elements
     * @param filter  the filter
     * @return        the elements from src which match the filter
     */
    public static <F,T extends F> T[] filter( T[] src, Predicate<F> filter ) {
        
        List<T> result = new ArrayList<T>();
        for (T t : src) {
            boolean b = filter.test(t);
            if(b) {
                result.add(t);
            }
        }
        
        Class<?> cl = src.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        T[] r = (T[]) Array.newInstance(cl,result.size());
        r = result.toArray(r);
        
        return r;
    }

    /**
     * Return the elements from src which match the specified filter.
     * 
     * @param <T>     the type of the elements
     * @param src     the source elements
     * @param filter  the filter
     * @return        the elements from src which match the filter
     */
    public static <F,T extends F> List<T> filter( List<T> src, Predicate<F> filter ) {
        
        List<T> result = new ArrayList<T>();
        for (T t : src) {
            boolean b = filter.test(t);
            if(b) {
                result.add(t);
            }
        }
        
        return result;
    }
}
