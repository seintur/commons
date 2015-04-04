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

package commons.util;

/**
 * Thrown to indicate that a badly formatted description has been used while
 * trying to generate a RangeSet instance. Either illegal ranges separator has
 * been used, or range boundaries are not valid non negative integers.
 *  
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class RangeSetFormatException extends Exception {
    
	static final long serialVersionUID = -4661966246947366741L;
	
	public RangeSetFormatException() {
        super();
    }
    
    public RangeSetFormatException( String message ) {
        super(message);
    }

}
