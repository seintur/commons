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
import java.util.Iterator;
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
     * Construct a new map (actually a hash map)
     * and initialize it with the given parameter.
     * The given parameter must be an array of dimension 2 arrays.
     * The 1st element of each element is a key and the 2nd one a value.
     */
    public static Map<Object,Object> create( Object[][] values ) {
        Map<Object,Object> ret = new HashMap<Object,Object>();
        for ( int i=0 ; i < values.length ; i++ ) {
            if ( values[i].length != 2 ) {
                throw new IllegalArgumentException(
                    "The parameter must be an array where each element is an array with exactly 2 elements");
            }
            ret.put( values[i][0], values[i][1] );
        }
        return ret;
    }

    /**
     * Construct a new map (actually a hash map)
     * and initialize it with the given entry.
     */
    public static Map<Object,Object> create( Object key, Object value ) {
        Map<Object,Object> ret = new HashMap<Object,Object>();
        ret.put(key,value);
        return ret;
    }

    
    /**
     * Search for key strings that match the beginning of a given string.
     *
     * @param map            the source map
     * @param keyStrToMatch  the string
     * @return               the value or null
     */
    public static Object matchKeyWithString( Map map, String keyStrToMatch ) {

        Set entrySet = map.entrySet();
        Iterator iterator = entrySet.iterator();

        while ( iterator.hasNext() ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
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
    public static Collection<Object> match( Map src, Object[] target ) {

        /**
         * The result is stored in a LinkedList.
         * A multiset would have been enough if it were existing in the Java API.
         */
        Collection<Object> dst = new LinkedList<Object>();
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
    public static Collection<Object> match( Map src, Object[] target, Collection<Object> dst ) {

        Set entrySet = src.entrySet();
        Iterator iterator = entrySet.iterator();

        while ( iterator.hasNext() ) {
            
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();

            if ( key instanceof Selector ) {
            	Selector keySel = (Selector) key;
                if ( keySel.equals(target) ) {
                	dst.add( entry.getValue() );
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
     * @param src                    sthe source map
     * @param target                 the values used to select keys
     * @param orderingAttributeName  the attribute used to order the collection
     * @return                       redundantly returns the destination collection
     */
    public static Collection<Object> matchOrderedBy(
        Map src, Object[] target, String orderingAttributeName ) {

        /**
         * TreeSet is the only ordered collection
         * (ie implementing the OrderedSet interface), isn't it ?
         */
        Comparator<Object> comp = new IntegerAttributeComparator(orderingAttributeName);
        Collection<Object> ret = new TreeSet<Object>(comp);
        return match( src, target, ret );
    }


    /**
     * Search for keys where a given matcher method return true.
     * Return the associated values.
     *
     * @param src                          the source map
     * @param matcherMethodName            the matcher method name
     * @param matcherMethodParameterTypes  the parameters classes of the matcher method
     * @param target                       the values used when the matcher method is called
     * @return                             a collection of values associated with matching keys
     */
    public static Collection<Object> match(
        Map src, String matcherMethodName,
        Class[] matcherMethodParameterTypes, Object[] target ) {

        /**
         * The result is stored in a LinkedList.
         * A multiset would have been enough if it were existing in the Java API.
         */
        Set entrySet = src.entrySet();
        Iterator iterator = entrySet.iterator();
        Collection<Object> result = new LinkedList<Object>();

        while ( iterator.hasNext() ) {
            
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();

            Class cl = key.getClass();
            try {
            	Method method = cl.getMethod( matcherMethodName, matcherMethodParameterTypes );
                Object returnedValue = method.invoke( key, target );
                if ( returnedValue instanceof Boolean ) {
                	boolean ret = ((Boolean)returnedValue) . booleanValue();
                    if (ret) {
                    	result.add( entry.getValue() );
                    }
                }
            }
            catch( NoSuchMethodException nsme ) {
            	/**
                 * Raised by cl.getMethod()
                 * From a semantical point of view, not really an exception.
                 * The key class does not contain the searched method. Go on.
                 */
            }
            catch( InvocationTargetException ite ) {
                /** Raised by method.invoke() */
            }
            catch( IllegalAccessException iae ) {
                /** Raised by method.invoke() */
            }
        }

        return result;
    }

    /**
     * Perform a minus operation between the keys of src and dst.
     * 
     * @param src  the source map
     * @param dst  the set whose elements are to be removed
     * @return     a new map containing the elements of src
     *             whose keys are not found in dst
     */
    public static Map subtract( Map src, Set dst ) {
        
        /**
         * Construct an empty map of the same class as src
         * or construct an empty HashMap
         * if the class of src does not provide an empty constructor. 
         */
        Map result = null;
        Class cl = src.getClass();
        try {
            result = (Map) cl.newInstance();
        } catch (InstantiationException e) {
            result = new HashMap<Object,Object>();
        } catch (IllegalAccessException e) {
            result = new HashMap<Object,Object>();
        }
        
        /**
         * Add to result the elements of src
         * whose keys are not found in dst.
         */
        for ( Iterator iter=src.entrySet().iterator() ; iter.hasNext() ; ) {
            Map.Entry element = (Map.Entry) iter.next();
            Object key = element.getKey();
            if ( ! dst.contains(key) )
                result.put(key,element.getValue());
        }
        
        return result;
    }
    
}
