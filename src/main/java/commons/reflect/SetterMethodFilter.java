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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class for filtering setter methods.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class SetterMethodFilter implements Filter<Method> {

	public boolean accept( Method method ) {
		boolean b = isSetterMethod(method);
		return b;
	}

	/**
	 * Return <code>true</code> if the specified method is a setter.
	 */
	public static boolean isSetterMethod( Method setter ) {
	    try {
	        checkSetterMethod(setter);
	        return true;
	    }
	    catch( IllegalArgumentException iae ) {
	        return false;
	    }
	}
	
	/**
	 * Dynamically invoke the specified setter method on the specified object
	 * with the specified value.
	 * 
	 * @param content  the object
	 * @param setter   the setter method
	 * @param value    the value
	 */
	public static void invokeSetter( Object content, Method setter, Object value )
	throws IllegalAccessException, InvocationTargetException {
		
		// Check whether this is a setter
		boolean b = isSetterMethod(setter);
		if(!b) {
			return;
		}
        
	    Class<?>[] ptypes = setter.getParameterTypes();
		if( value == null ) {
			// Null value cannot be injected on primitive types
			if( ! ptypes[0].isPrimitive() ) {
	            setter.invoke(content,value);
			}
        }
        else {
        	// Check that the parameter is assignable from the value
	        Class<?> cl = value.getClass();
		    if( ptypes[0].isAssignableFrom(cl) ) {
                setter.invoke(content,value);
		    }
        }		
	}

	/**
	 * Check whether the specified method is a setter.
	 * 
	 * @param setter  the method to be checked
	 * @throws IllegalArgumentException
	 *      if <code>setter</code> is not a setter method
	 */
	public static void checkSetterMethod( Method setter )
	throws IllegalArgumentException {        
	
	    String name = setter.getName();
	    if( ! name.startsWith("set") ) {
	        String msg =
	            "The name of a setter method should start with set: "+setter;
	        throw new IllegalArgumentException(msg);
	    }
	    Class<?> rtype = setter.getReturnType();
	    if( ! rtype.equals(void.class) ) {
	        String msg = "A setter method should return void: "+setter;
	        throw new IllegalArgumentException(msg);
	    }
	    Class<?>[] ptypes = setter.getParameterTypes();
	    if( ptypes.length != 1 ) {
	        String msg =
	            "A setter method should define only one parameter: "+setter;
	        throw new IllegalArgumentException(msg);
	    }
	}

	/**
	 * Check whether the specified method is a setter for the specified
	 * property.
	 * 
	 * @param setter    the method to be checked
	 * @param proptype  the property type
	 * @throws IllegalArgumentException
	 *      if the method is not a setter for the specified property
	 */
	public static void checkSetterMethod( Method setter, Class<?> proptype )
	throws IllegalArgumentException {
	
	    Class<?> type = getSetterPropertyType(setter);
	    
	    if( ! proptype.isAssignableFrom(type) ) {
	        String msg =
	            "Method "+setter+
	            " is not a setter method for property type "+proptype.getName();
	        throw new IllegalArgumentException(msg);
	    }
	}

	/**
	 * Return the type of the property set by the specified setter method.
	 * 
	 * @param setter  a setter method
	 * @return        the type of the property set by <code>setter</code>
	 * @throws IllegalArgumentException
	 *      if <code>setter</code> is not a setter method
	 */
	public static Class<?> getSetterPropertyType( Method setter )
	throws IllegalArgumentException {
	    
	    checkSetterMethod(setter);
	    
	    Class<?>[] ptypes = setter.getParameterTypes();
	    return ptypes[0];
	}

	/**
	 * Return the name of the property set by the specified setter method.
	 * 
	 * @param setter  a setter method
	 * @return        the name of the property set by <code>setter</code>
	 * @throws IllegalArgumentException
	 *      if <code>setter</code> is not a setter method
	 */
	public static String getSetterPropertyName( Method setter )
	throws IllegalArgumentException {
	    
	    checkSetterMethod(setter);
	    
	    String name = setter.getName();
	    name = name.substring(3);   // Skip set
	    name = name.substring(0,1).toLowerCase() + name.substring(1);
	    
	    return name;
	}
}
