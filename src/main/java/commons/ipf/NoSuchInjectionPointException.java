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

import java.util.Arrays;

/**
 * Exception thrown if an injection point cannot be found.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class NoSuchInjectionPointException extends Exception {
    
    private static final long serialVersionUID = 8092032071601905620L;

    private String name;
    private Class<?> cl;
    private String[] annotClassNames;
    
    public NoSuchInjectionPointException(
        String name, Class<?> cl, String... annotClassNames ) {
        
        super();
        this.name = name;
        this.cl = cl;
        this.annotClassNames = annotClassNames;
    }

    @Override
    public String getMessage() {
        final String str = Arrays.deepToString(annotClassNames);        
        final String msg =
            "No field or method annotated with @"+str+" for "+name+" in class "+
            cl.getName();
        return msg;
    }
}
