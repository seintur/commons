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

package commons.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility method for the {@link Annotation} interface.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class AnnotationHelper {

	/**
	 * Return the value of the parameter associated with the specified
	 * annotation.
	 * 
	 * @param annot  the annotation
	 * @param name   the parameter name
	 * @return
	 * 		the parameter value or <code>null</code> if the annotation is
	 * 		<code>null</code> or if the annotation does not define the specified
	 * 		parameter name 
	 */
	public static <T> T getAnnotationParamValue(
		Annotation annot, String name ) {
		
		if( annot == null ) {
			return null;
		}
		
		Class<?> annotcl = annot.getClass();
		try {
			Method meth = annotcl.getMethod(name);
			@SuppressWarnings("unchecked")
	    	T value = (T) meth.invoke(annot);
	    	return value;
		}
		catch (NoSuchMethodException e) {
			return null;
		}
		catch (IllegalAccessException e) {
			return null;
		}
		catch (InvocationTargetException e) {
			return null;
		}
	}
}
