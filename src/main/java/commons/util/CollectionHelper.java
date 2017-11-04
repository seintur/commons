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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import commons.lang.reflect.Property;

/**
 * Utility methods for the {@link Collection} interface.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class CollectionHelper {    

    /**
     * Return an empty collection whose implementation is of the same class as
     * the specified collection. If the specified collection cannot be
     * instantiated, return an empty {@link ArrayList}.
     * 
     * @param src  the source collection whose class is to used as a template
     * @return     a new collection of the same class as the source collection
     */
    public static <E> Collection<E> createOfSameClass( Collection<E> src ) {
        @SuppressWarnings("unchecked")
        Class<Collection<E>> cl = (Class<Collection<E>>) src.getClass();
        try {
            Collection<E> ret = cl.getConstructor().newInstance();
            return ret;
        }
        catch (	NoSuchMethodException | InstantiationException |
        			InvocationTargetException | IllegalAccessException e ) {
            Collection<E> ret = new ArrayList<>();
            return ret;
        }
    }
    
    /**
     * Iterate on all the elements of a collection and extract the value of an
     * attribute. The values are returned as a collection.
     * 
     * @param src            the source collection
     * @param attributeName  the attribute name
     * @return               a collection with attribute values
     */
    public static <E> Collection<Object> getAttributes(
        Collection<E> src, String attributeName ) {

        /*
         * LinkedList is the lighter implementation of
         * the Collection interface, isn't it?
         */
        Collection<Object> ret = new LinkedList<>();
        return getAttributes( src, attributeName, ret );
    }

    /**
     * Iterate on all the elements of a collection and extract the value of an
     * attribute. The values are returned as a collection. The collection is
     * ordered according to the value of another attribute (assumed to be an
     * integer).
     * 
     * @param src                    the source collection
     * @param attributeName          the attribute name
     * @param orderingAttributeName  the attribute used to order the collection
     * @return                       a collection with attribute values
     */
    public static <E> Collection<Object> getAttributesOrderedBy(
        Collection<E> src, String attributeName, String orderingAttributeName ) {

        /*
         * TreeSet is the only ordered collection
         * (i.e. implementing the OrderedSet interface), isn't it?
         */
        Comparator<Object> comp =
            new IntegerAttributeComparator<>(orderingAttributeName);
        Collection<Object> ret = new TreeSet<>(comp);
        return getAttributes( src, attributeName, ret );
    }

    /**
     * Iterate on all the elements of a collection and extract the value of an
     * attribute. The values are returned as a collection. The values are
     * extracted first by invoking a getter method, and if it fails, by getting
     * the value of the field with the reflection API.
     * 
     * @param src            the source collection
     * @param attributeName  the attribute name
     * @param dst            the destination collection
     *                       where attribute values are stored
     * @return               redundantly returns the destination collection
     */
    public static <E> Collection<Object> getAttributes(
        Collection<E> src, String attributeName, Collection<Object> dst ) {

        if ( attributeName.length() == 0 ) {
            final String msg = "Parameter attributeName must be non-empty";
            throw new IllegalArgumentException(msg);
        }

        Iterator<E> iterator = src.iterator();
        while ( iterator.hasNext() ) {
            
            E element = iterator.next();
            try {
                Object value =
                    Property.getAttributeValue( element, attributeName );
                dst.add( value );
            }
            catch( IllegalAccessException iae ) {
                /*
                 * The attribute value cannot be fetched.
                 * Ignore the element.
                 */
            }
        }

        return dst;
    } 
    
    /**
     * Traverse a graph structure according to a specified path and
     * return the leaf objects of the path.
     *
     * This method is primarily used when dealing with classes generated
     * from UML diagrams. In such diagrams, 0..* or 1..* relations are
     * translated into fields of type Collection. This method iterates on
     * all the levels of a path specified with getter method names.
     * The leaf objects of the path are returned.
     * 
     * @param src          the root object
     * @param methodNames  getter method names defining the path
     * @return             the leaf objects of the path
     */
    public static Set<Object> recursiveGet( Object src, String[] methodNames )
    throws
        NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {
        
        Set<Object> ret = new HashSet<>();
        recursiveGet( src, Arrays.asList(methodNames), new HashSet<>(), ret );
        return ret;
    }
    
    /**
     * Recursive method implementing public recursiveGet.
     * Define two more parameters:
     * - visited to prevent infinite loops in the traversal,
     * - leaf to store leaf objects.
     * 
     * @param src          the root object
     * @param methodNames  getter method names defining the path
     * @param visited      already visited objects
     * @param leaf         the leaf objects of the path
     */
    private static void recursiveGet(
        Object src, List<String> methodNames, Set<Object> visited,
        Set<Object> leaf )
    throws
        NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {
            
        /*
         * Invoke the first method whose name is in methodNames.
         * It is supposed to return either a collection
         * or a single element.
         */
        String methodName = methodNames.get(0);
        Class<?> cl = src.getClass();
        Method method = cl.getMethod(methodName);
        Object ret = method.invoke(src);
        
        /*
         * Add the current object to the set of visited objects.
         * When methodNames contains only one element,
         * the leaf of the path is reached,
         * and ret contains the elements to return in leaf.
         */
        visited.add(src);
        if ( methodNames.size() == 1 ) {
            if ( ret instanceof Collection ) {
                leaf.addAll( (Collection<?>) ret );
            }
            else
                leaf.add(ret);
        }
        else {
            /*
             * Compute a new list of method names without the first one.
             * Iterate on the ret and
             * recursively call recursiveGet
             * if the element has not already been visited.
             */
            List<String> trailingMethodNames = new LinkedList<>(methodNames);
            trailingMethodNames.remove(0);
            if ( ret instanceof Collection ) {
                Collection<?> elements = (Collection<?>) ret;
                for (Object element : elements) {
                    if ( ! visited.contains(element) )
                        recursiveGet(element,trailingMethodNames,visited,leaf);
                }
            }
            else {
                if ( ! visited.contains(ret) )
                    recursiveGet(ret,trailingMethodNames,visited,leaf);
            }
        }
    }
}
