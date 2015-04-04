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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility method for the {@link Method} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MethodHelper {

	/**
	 * Check whether the specified method is a getter.
	 * 
	 * @param getter  the method to be checked
	 * @throws IllegalArgumentException
	 *      if <code>getter</code> is not a getter method
	 */
	public static void checkGetterMethod( Method getter )
	throws IllegalArgumentException {        
	
	    String name = getter.getName();
	    if( ! name.startsWith("get") ) {
	        final String msg =
	            "The name of a getter method should start with get: "+getter;
	        throw new IllegalArgumentException(msg);
	    }
	    Class<?>[] ptypes = getter.getParameterTypes();
	    if( ptypes.length != 0 ) {
	        final String msg =
	            "A getter method should not define any parameter: "+getter;
	        throw new IllegalArgumentException(msg);
	    }
	}

	/**
	 * Check whether the specified methods are a valid pair of setter/getter
	 * methods for the same property.
	 * 
	 * @throws IllegalArgumentException  if this is not the case
	 */
	public static void checkMatchingSetterGetterMethods(
		Method setter, Method getter )
	throws IllegalArgumentException {
		
		SetterMethodFilter.checkSetterMethod(setter);
		checkGetterMethod(getter);
		
		String setterPropName = SetterMethodFilter.getSetterPropertyName(setter);
		String getterPropName = getGetterPropertyName(getter);
		if( ! setterPropName.equals(getterPropName) ) {
			final String msg =
				"Property names differ: "+setterPropName+" vs "+getterPropName;
			throw new IllegalArgumentException(msg);
		}
		
		Class<?> setterPropType = SetterMethodFilter.getSetterPropertyType(setter);
		Class<?> getterPropType = getGetterPropertyType(getter);
		if( ! setterPropType.equals(getterPropType) ) {
			final String msg =
				"Property types differ: "+setterPropType+" vs "+getterPropType;
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Return the getter method corresponding to the specified setter.
	 * 
	 * @throws NoSuchMethodException  if the getter does not exist
	 */
	public static Method getGetterForSetter( Method setter )
	throws NoSuchMethodException {
		
		Class<?> cl = setter.getDeclaringClass();
		
		// First search in declared methods
		Method[] methods = cl.getDeclaredMethods();
		for (Method method : methods) {
			if( isMatchingSetterGetter(setter,method) ) {
				return method;
			}
		}
	
		// First search in declared methods
		methods = cl.getMethods();
		for (Method method : methods) {
			if( isMatchingSetterGetter(setter,method) ) {
				return method;
			}
		}
		
		throw new NoSuchMethodException();
	}

	/**
	 * Return the name of the property set by the specified getter method.
	 * 
	 * @param getter  a getter method
	 * @return        the name of the property set by <code>getter</code>
	 * @throws IllegalArgumentException
	 *      if <code>getter</code> is not a getter method
	 */
	public static String getGetterPropertyName( Method getter )
	throws IllegalArgumentException {
	    
	    checkGetterMethod(getter);
	    
	    String name = getter.getName();
	    name = name.substring(3);   // Skip get
	    name = name.substring(0,1).toLowerCase() + name.substring(1);
	    
	    return name;
	}

	/**
	 * Return the type of the property set by the specified getter method.
	 * 
	 * @param getter  a setter method
	 * @return        the type of the property set by <code>getter</code>
	 * @throws IllegalArgumentException
	 *      if <code>getter</code> is not a getter method
	 */
	public static Class<?> getGetterPropertyType( Method getter )
	throws IllegalArgumentException {
	    
	    checkGetterMethod(getter);
	    
	    Class<?> rtype = getter.getReturnType();
	    return rtype;
	}

	/**
	 * Return <code>true</code> if the specified method is a getter.
	 * 
	 * @param getter  the method to be checked
	 * @return  <code>true</code> if this is a getter
	 */
	public static boolean isGetterMethod( Method getter ) {
	    try {
	        checkGetterMethod(getter);
	        return true;
	    }
	    catch( IllegalArgumentException iae ) {
	        return false;
	    }
	}

	/**
	 * Return <code>true</code> if the specified pair of methods are valid
	 * setter/getter for the same property.
	 */
	public static boolean isMatchingSetterGetter(
		Method setter, Method getter ) {
		
		try {
			checkMatchingSetterGetterMethods(setter,getter);
			return true;
		}
		catch( IllegalArgumentException iae ) {
			return false;
		}
	}

	/**
	 * Strip modifiers, return type, class name and throw clause from the
	 * signature of the specified method.
	 * 
	 * For a method signature of the form
	 * 	 <code>public void org.ow2.frascati.tinfi.reflect.UtilTestCase$Target.init() throws java.lang.RuntimeException</code>
	 * this method returns
	 *   <code>init()</code>
	 */
	public static String getShortSignature( Method src ) {
		String signature = src.toString();
		int parenthesis = signature.indexOf('(');
		String s = signature.substring(0,parenthesis);
		int methoddot = s.lastIndexOf('.');
		String ret = signature.substring(methoddot+1);
		int thr = ret.indexOf(" throws");
		if( thr != -1 ) {
			ret = ret.substring(0,thr);
		}
		return ret;
	}

	/**
	 * Return <code>true</code> if the target and source methods share the same
	 * signature.
	 * 
	 * @param src     the source method
	 * @param target  the target method
	 */
	public static boolean sameSignature( Method src, Method target ) {
		String srcsig = getShortSignature(src);
		String targetsig = getShortSignature(target);
		boolean b = srcsig.equals(targetsig);
		return b;
	}

	/**
	 * Return <code>true</code> if the source method overrides the target
	 * method.
	 * 
	 * @param src     the source method
	 * @param target  the target method
	 */
	public static boolean override( Method src, Method target ) {
		
		boolean b = sameSignature(src,target);
		if(!b) {
			return false;
		}
		
		Class<?> srccl = src.getDeclaringClass();
		Class<?> targetcl = target.getDeclaringClass();
		
		if( targetcl.isAssignableFrom(srccl) ) {
			/*
			 * If targetcl is assignable from srccl, this means that src (the
			 * declaring class of) is a subtype of target (idem.) Then, since
			 * target and src share the same signature, src overrides target.
			 */
			return true;
		}
		
		return false;
	}

	/**
	 * Return a copy of the specified array where overridden methods have been
	 * removed.
	 */
	public static Method[] removeOverridden( Method[] methods ) {
		List<Method> res = new ArrayList<Method>();
		res.addAll(Arrays.asList(methods));
		for (int i = 0; i < methods.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				if(j==i) { continue; }
				boolean b = override(methods[j],methods[i]);
				if(b) {
					res.remove(methods[i]);
				}
			}
		}
		return res.toArray(new Method[res.size()]);
	}
}
