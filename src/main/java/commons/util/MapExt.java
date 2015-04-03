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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class holds maps related functionalities found
 * neither in java.util.Map nor in java.util.AbstractMap
 * (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MapExt {

    /**
     * Search for key strings that match the beginning of a given string.
     *
     * @param map            the source map
     * @param keyStrToMatch  the string
     * @return               the value or null
     */
    public static <K,V> V matchKeyWithString( Map<K,V> map, String keyStrToMatch ) {

    	for (Map.Entry<K,V> entry : map.entrySet()) {
            K key = entry.getKey();
            if ( key instanceof String && keyStrToMatch.startsWith((String)key) ) {
                return entry.getValue();
            }
        }

        return null;
    }

    
    /**
     * Search for keys implementing the common.util.Selector interface
     * that match the target parameter.
     * Return the associated values.
     *
     * @param src     the source map
     * @param target  the values used to select keys
     * @return        a collection of values associated with matching keys
     */
    public static <K,V> Collection<V> match( Map<K,V> src, Object[] target ) {

        /*
         * The result is stored in a LinkedList.
         * A multiset would have been enough if it were existing in the Java API.
         */
        Collection<V> dst = new LinkedList<>();
        return match( src, target, dst );
    }


    /**
     * Search for keys implementing the common.util.Selector interface
     * that match the target parameter.
     * Return the associated values.
     *
     * @param src     the source map
     * @param target  the values used to select keys
     * @param dst     the destination collection where values associated with matching keys are stored
     * @return        redundantly returns the destination collection
     */
    public static <K,V> Collection<V> match( Map<K,V> src, Object[] target, Collection<V> dst ) {

    	for (Map.Entry<K,V> entry : src.entrySet()) {
    		K key = entry.getKey();
            if ( key instanceof Selector ) {
            	Selector keySel = (Selector) key;
                if ( keySel.equals(target) ) {
                	V value = entry.getValue();
                	dst.add(value);
                }
            }
        }

        return dst;
    }


    /**
     * Search for keys implementing the common.util.Selector interface
     * that match the target parameter.
     * Return the associated values ordered according to
     * an integer attribute of the values (assumed to all provide this attribute).
     *
     * @param src                    the source map
     * @param target                 the values used to select keys
     * @param orderingAttributeName  the attribute used to order the collection
     * @return                       redundantly returns the destination collection
     */
    public static <K,V> Collection<V> matchOrderedBy(
        Map<K,V> src, Object[] target, String orderingAttributeName ) {

        /*
         * TreeSet is the only ordered collection
         * (i.e. implementing the OrderedSet interface), isn't it ?
         */
        Comparator<V> comp = new IntegerAttributeComparator<>(orderingAttributeName);
        Collection<V> ret = new TreeSet<>(comp);
        return match( src, target, ret );
    }


    /**
     * Search for keys where a given matcher method return <code>true</code>.
     * Return the associated values.
     *
     * @param src                          the source map
     * @param matcherMethodName            the matcher method name
     * @param matcherMethodParameterTypes  the parameters classes of the matcher method
     * @param target                       the values used when the matcher method is called
     * @return                             a collection of values associated with matching keys
     */
    public static <K,V> Collection<V> match(
        Map<K,V> src, String matcherMethodName,
        Class<?>[] matcherMethodParameterTypes, Object[] target ) {

        /*
         * The result is stored in a LinkedList.
         * A multiset would have been enough if it were existing in the Java API.
         */
        Collection<V> result = new LinkedList<>();

        for (Map.Entry<K,V> entry : src.entrySet()) {
			
            K key = entry.getKey();
            Class<?> cl = key.getClass();
            
            try {
            	Method method = cl.getMethod( matcherMethodName, matcherMethodParameterTypes );
                Object returnedValue = method.invoke( key, target );
                if ( returnedValue instanceof Boolean ) {
                	boolean ret = ((Boolean)returnedValue) . booleanValue();
                    if (ret) {
                    	V value = entry.getValue();
                    	result.add(value);
                    }
                }
            }
            catch( NoSuchMethodException nsme ) {
            	/*
                 * Raised by cl.getMethod()
                 * From a semantical point of view, not really an exception.
                 * The key class does not contain the searched method. Go on.
                 */
            }
            catch( InvocationTargetException | IllegalAccessException e ) {
                // Raised by method.invoke()
            }
        }

        return result;
    }

    /**
     * Perform a minus operation between the keys of two maps.
     * 
     * @param src  the source map
     * @param dst  the set whose elements are to be removed
     * @return     a new map containing the elements of the source map
     *             whose keys are not found in the set
     */
    public static <K,V> Map<K,V> subtract( Map<K,V> src, Set<K> dst ) {
        
        /*
         * Construct an empty map of the same class as src
         * or construct an empty HashMap
         * if the class of src does not provide an empty constructor. 
         */
        @SuppressWarnings("unchecked")
		Class<Map<K,V>> cl = (Class<Map<K,V>>) src.getClass();
        Map<K,V> result = null;
        try {
            result = cl.newInstance();
        }
        catch (InstantiationException e) {
            result = new HashMap<K,V>();
        }
        catch (IllegalAccessException e) {
            result = new HashMap<K,V>();
        }
        
        /*
         * Add to result the elements of src
         * whose keys are not found in dst.
         */
        for (Map.Entry<K,V> entry : src.entrySet()) {
            K key = entry.getKey();
            if ( ! dst.contains(key) )
                result.put(key,entry.getValue());
        }
        
        return result;
    }
}
