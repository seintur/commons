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

package commons.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class defines functionalities to deal with properties declared by beans.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Property {

    /**
     * Given an attribute name, retrieve its value in the specified object
     * first by invoking a getter method, and next if it fails, by getting the
     * field value with the reflection API.
     *
     * @param obj            the target object
     * @param attributeName  the attribute name
     * @return               the attribute value
     */
    public static Object getAttributeValue( Object obj, String attributeName )
    throws IllegalAccessException {

        char firstLetter = attributeName.charAt(0);
        String rest = attributeName.substring(1);
        String getterMethodName = "get" + Character.toUpperCase(firstLetter) + rest;

        /*
         * The attribute type is unknown.
         * We cannot call getMethod(String,Class[]).
         */
        Class<?> cl = obj.getClass();
        Method[] methods = cl.getMethods();
        
        int i;
        for ( i=0 ; i < methods.length ; i++ ) {
            if ( methods[i].getName().equals(getterMethodName) ) {
                /*
                 * Several getter methods may be present.
                 * Return the value given by the 1st working one.
                 * The call may failed if the getter requires
                 * some non-empty parameters.
                 */
                try {
                    return methods[i].invoke( obj, new Object[]{} );
                }
                catch( InvocationTargetException ite ) {}
                catch( IllegalAccessException iae ) {}
            }
        }

        /*
         * No getter method or no working getter method has been found.
         * Try getting the value of the attribute with the reflection API.
         */
        try {
            Field field = cl.getField( attributeName );
            return field.get(obj);
        }
        catch( NoSuchFieldException nsfe ) {}
        catch( IllegalAccessException iae ) {}

        /*
         * Everything failed.
         * Give up.
         */
        final String msg =
    		"Cannot get the value of the field " + attributeName;
        throw new IllegalAccessException(msg);
    }
}
