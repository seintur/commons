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
import java.util.function.Predicate;

/**
 * Class for filtering unannotated code elements.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class UnAnnotatedElementFilter implements Predicate<AnnotatedElement> {

    /**
     * Only accept the element if it does not define any annotation.
     */
    public boolean test( AnnotatedElement value ) {
        Annotation[] annots = value.getAnnotations();
        boolean b = annots.length == 0;
        return b;
    }
}
