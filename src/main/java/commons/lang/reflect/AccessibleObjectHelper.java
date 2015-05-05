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

package commons.lang.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility method for the {@link AccessibleObject} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class AccessibleObjectHelper {

    /**
     * Return a copy of the specified array where overridden methods and fields
     * have been removed.
     */
    public static AccessibleObject[] removeOverridden( AccessibleObject[] aos ) {
        List<AccessibleObject> res = new ArrayList<AccessibleObject>();
        res.addAll(Arrays.asList(aos));
        for (int i = 0; i < aos.length; i++) {
            for (int j = 0; j < aos.length; j++) {
                if(j==i) { continue; }
                boolean b = false;
                if( aos[i] instanceof Method && aos[j] instanceof Method ) {
                    b = MethodHelper.override((Method)aos[j],(Method)aos[i]);
                }
                else if( aos[i] instanceof Field && aos[j] instanceof Field ) {
                    b = FieldHelper.override((Field)aos[j],(Field)aos[i]);
                }
                if(b) {
                    res.remove(aos[i]);
                }
            }
        }
        return res.toArray(new AccessibleObject[res.size()]);
    }
}
