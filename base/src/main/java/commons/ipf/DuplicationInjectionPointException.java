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

/**
 * Exception thrown if more than one injection point is defined for a specified
 * name.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class DuplicationInjectionPointException extends Exception {
    
	private static final long serialVersionUID = -7622515465840970313L;
	
	private String name;
    private Class<?> cl;
    private String annotClassName;
    
    public DuplicationInjectionPointException(
        String name, Class<?> cl, String annotClassName ) {
        
        super();
        this.name = name;
        this.cl = cl;
        this.annotClassName = annotClassName;
    }
    
    @Override
    public String getMessage() {
    	final String msg =
            "Several fields and/or methods annotated with @"+annotClassName+
            " for reference "+name+" in class "+cl.getName();
        return msg;
    }
}
