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

/**
 * This interface represents a point in the implementation of a class where
 * a value can be injected. The value can represent either a reference or a
 * property. The injection point is either a field or a setter method.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public interface InjectionPoint<A extends Annotation> {

    /**
     * Set the value by dynamically setting the field or invoking the setter
     * method on the specified object.
     */
    public void set( Object target, Object value )
    throws IllegalAccessException, InvocationTargetException;
    
    /**
     * Return the value by dynamically getting the field or invoking the getter
     * method on the specified object.
     */
    public Object get( Object target )
    throws IllegalAccessException, InvocationTargetException;
    
    /**
     * Return the annotation associated with the current injection point.
     */
    public A getAnnotation();
    
    /**
     * Return the type of the current injection point.
     */
    public Class<?> getType();
    
    /**
     * Return <code>true</code> if the specified injection point overrides the
     * current injection point.
     */
    public boolean override( InjectionPoint<?> other );
}
