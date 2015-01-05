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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Class for filtering annotated code elements.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class AnnotatedElementFilter implements Filter<AnnotatedElement> {

	private String[] annotClassNames;
	
	public AnnotatedElementFilter( String... annotClassNames ) {
		this.annotClassNames = annotClassNames;
	}
	
	public boolean accept( AnnotatedElement value ) {
		Annotation[] annots = value.getAnnotations();
		for (String annotClassName : annotClassNames) {
			for (Annotation annot : annots) {
				String name = annot.annotationType().getName();
				if( name.equals(annotClassName) ) {
					return true;
				}
			}
		}
		return false;
	}
}
