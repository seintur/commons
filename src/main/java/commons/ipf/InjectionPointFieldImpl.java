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
import java.lang.reflect.Field;

import commons.reflect.FieldHelper;

/**
 * This class represents an injection point which is implemented as a field.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InjectionPointFieldImpl<A extends Annotation>
extends InjectionPointImpl<A> {

    private Field field;

    public InjectionPointFieldImpl( Field field, A annot ) {
    	super(annot);
    	this.field = field;
        field.setAccessible(true);  // Enable access to private fields
    }
    
    public void set( Object target, Object value )
    throws IllegalAccessException {
        field.set(target,value);
    }
    
    public Object get( Object target ) throws IllegalAccessException {
        Object value = field.get(target);
        return value;
    }
    
    public Field getField() {
    	return field;
    }

    public Class<?> getType() {
    	return field.getType();
    }
    
    public boolean override( InjectionPoint<?> other ) {
    	if( ! (other instanceof InjectionPointFieldImpl) ) {
    		return false;
    	}
    	Field otherfield = ((InjectionPointFieldImpl<?>)other).field;
    	boolean b = FieldHelper.override(field,otherfield);
    	return b;
    }
}
