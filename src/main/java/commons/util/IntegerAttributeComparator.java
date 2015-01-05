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

import java.util.Comparator;

import commons.reflect.Property;

/**
 * This class provides a comparator based on the value of an field of
 * objects to compare. The field type must be integer.
 * Used for instance by getAttributesOrderedBy(Collection,String,String).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class IntegerAttributeComparator<T> implements Comparator<T> {

    private String orderingAttributeName;

    public IntegerAttributeComparator( String orderingAttributeName ) {
        this.orderingAttributeName = orderingAttributeName;
    }
    
    public int compare( T o1, T o2 ) {

        Object v1 = null;
        Object v2 = null;

        try {
            v1 = Property.getAttributeValue( o1, orderingAttributeName );
            v2 = Property.getAttributeValue( o2, orderingAttributeName );
        }
        catch( IllegalAccessException iae ) {
            throw new IllegalArgumentException(
                "Object cannot be compared: no " + orderingAttributeName +
                " attribute found." );
        }

        if ( ! ( v1 instanceof Integer && v2 instanceof Integer) ) {
            throw new IllegalArgumentException(
                "Object cannot be compared: " + orderingAttributeName +
                " is not an integer." );
        }

        int r1 = ( (Integer) v1 ).intValue();
        int r2 = ( (Integer) v2 ).intValue();
        
        if ( r1 == r2 )  return 0;
        else if ( r1 < r2 )  return -1;
             else return 1;
    }
    
    @Override
    public boolean equals( Object obj ) {
        throw new UnsupportedOperationException();
    }
}
