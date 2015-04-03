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

package commons.ipf;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import commons.reflect.SetterMethodFilter;
import commons.reflect.Util;

/**
 * This class represents an injection point which is implemented as a setter
 * method.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InjectionPointMethodImpl<A extends Annotation>
extends InjectionPointImpl<A> {

	private Method setter;
	private Method getter;
	
	/**
     * @throws IllegalArgumentException
     *      if the specified argument is not a setter method
     */
    public InjectionPointMethodImpl( Method setter, A annot )
    throws IllegalArgumentException {
        
    	super(annot);
    	
        SetterMethodFilter.checkSetterMethod(setter);
        this.setter = setter;
        setter.setAccessible(true);  // Enable access to private methods
    }
    
	/**
     * @throws IllegalArgumentException
     *      if the specified argument is not a setter method
     */
    public InjectionPointMethodImpl( Method setter, Method getter, A annot )
    throws IllegalArgumentException {
        
        super(annot);
    	
    	Util.checkMatchingSetterGetterMethods(setter,getter);
        this.setter = setter;
        this.getter = getter;
        setter.setAccessible(true);  // Enable access to private methods
        getter.setAccessible(true);  // Enable access to private methods
    }
    
    public void set( Object target, Object value )
    throws IllegalAccessException, InvocationTargetException {
        setter.invoke(target,value);
    }
    
    public Object get( Object target )
    throws
    	IllegalAccessException, InvocationTargetException,
    	UnsupportedOperationException {
    	
    	if( getter == null ) {
            final String msg = "Cannot get the value with a setter method";
            throw new UnsupportedOperationException(msg);
    	}
    	
    	Object value = getter.invoke(target);
    	return value;
    }
    
    public Method getGetterMethod() {
    	return getter;
    }

    public Method getSetterMethod() {
    	return setter;
    }

    public Class<?> getType() {
    	Class<?>[] ptypes = setter.getParameterTypes();
    	return ptypes[0];
    }

    public boolean override( InjectionPoint<?> other ) {
    	if( ! (other instanceof InjectionPointMethodImpl) ) {
    		return false;
    	}
    	Method othersetter = ((InjectionPointMethodImpl<?>)other).setter;
    	boolean b = Util.override(setter,othersetter);
    	return b;
    }
}
