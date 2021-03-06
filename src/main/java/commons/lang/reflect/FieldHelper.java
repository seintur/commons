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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides helper methods for the {@link Field} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class FieldHelper {

    /**
     * Return <code>true</code> if the source field overrides the target field.
     * 
     * @param src     the source field
     * @param target  the target field
     */
    public static boolean override( Field src, Field target ) {
        
        String srcname = src.getName();
        String targetname = target.getName();
        boolean b = srcname.equals(targetname);
        if(!b) {
            return false;
        }
        
        Class<?> srccl = src.getDeclaringClass();
        Class<?> targetcl = target.getDeclaringClass();
        
        if( targetcl.isAssignableFrom(srccl) ) {
            /*
             * If targetcl is assignable from srccl, this means that src (the
             * declaring class of) is a subtype of target (idem.) Then, since
             * target and src share the same name, src overrides target.
             */
            return true;
        }
        
        return false;
    }

	/**
	 * Return a copy of the specified array where overridden fields have been
	 * removed.
	 */
	public static Field[] removeOverridden( Field[] fields ) {
		List<Field> res = new ArrayList<Field>();
		res.addAll(Arrays.asList(fields));
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields.length; j++) {
				if(j==i) continue;
				boolean b = override(fields[j],fields[i]);
				if(b) {
					res.remove(fields[i]);
				}
			}
		}
		return res.toArray(new Field[res.size()]);
	}
}
