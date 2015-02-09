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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import commons.reflect.Util;

/**
 * Default abstract implementation of the {@link InjectionPoint} interface.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public abstract class InjectionPointImpl<A extends Annotation>
implements InjectionPoint<A> {

    private A annot;

    public InjectionPointImpl( A annot ) {
    	this.annot = annot;
    }
	
    public A getAnnotation() {
    	return annot;
    }

	/**
	 * Factory method for injection points.
	 * 
	 * @param ao     a setter method or field
	 * @param annot  the annotation
	 * @return       the corresponding injection point
	 */
	public static <A extends Annotation> InjectionPoint<A> getInjectionPoint(
		AccessibleObject ao, A annot ) {
		
		InjectionPoint<A> ip = null;
		
		if( ao instanceof Method ) {
	        Method setter = (Method) ao;
	        try {
				Method getter = Util.getGetterForSetter(setter);
	            ip = new InjectionPointMethodImpl<>(setter,getter,annot);
			}
	        catch (NoSuchMethodException e) {
	            ip = new InjectionPointMethodImpl<>(setter,annot);
			}
	    }
	    else {
	        Field field = (Field) ao;
	        ip = new InjectionPointFieldImpl<>(field,annot);
	        return ip;
	    }
	
		return ip;
	}    
}
