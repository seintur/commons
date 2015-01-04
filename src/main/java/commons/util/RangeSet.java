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

import java.util.HashSet;
import java.util.Set;

/**
 * This class manages a set of integer ranges.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class RangeSet {
    
    /** A description of the ranges, e.g. "7;2-5;3-4;10" */
    private String descr;
    
    /** A set of integers containing the elements of the ranges. */
    private Set<Integer> elements;
    

    public RangeSet( String descr ) {
        this.descr = descr;
    }
    
    /**
     * Set a new description and
     * clean elements to enable its regeneration at the next
     * call to getElements().
     */
    public void setDescr( String descr ) {
        this.descr = descr;
        elements = null;
    }
    
    /**
     * Compute the integers matching the ranges description.
     * This method does not validate the ranges description
     * format (see validate for this).
     * 
     * @return  a set of Integer instances
     */
    public Set<Integer> getElements() {
        
        elements = new HashSet<Integer>();
        
        // Ignore null and empty strings
        if ( descr==null || descr.length()==0 )
            return elements;
            
        // Split the string around
        String[] ranges = descr.split(";");
        for ( int i=0 ; i < ranges.length ; i++ ) {
            String range = ranges[i];
            
            // Ignore null and empty ranges
            if ( range==null || range.length()==0 )
                continue;
            
            /*
             * Split each range around -
             * If there is only one element, this is a single value,
             * else this is really a range.
             */
            String[] pairs = range.split("-");
            if ( pairs.length == 1 ) {
                // This is a single value.
                
                try {
                    int value = Integer.parseInt(pairs[0]);
                    elements.add( new Integer(value) );
                }
                catch( NumberFormatException nfe ) {
                    // Ignore errors in number formats
                }
            }
            else {
                /*
                 * This is a range:
                 * - get the 1st and 2nd integers.
                 * - use them as boundaries,
                 * - remaining integers if any, are ignored.
                 */
                if ( pairs[0]==null || pairs[0].length()==0 ||
                     pairs[1]==null || pairs[1].length()==0 )
                    continue;
            
                try {
                    int start = Integer.parseInt(pairs[0]);
                    int end = Integer.parseInt(pairs[1]);
                    for ( int j=start ; j <= end ; j++ ) {
                        elements.add( new Integer(j) );
                    }
                }
                catch( NumberFormatException nfe ) {
                    // Ignore errors in number formats
                }
            }
        }

        return elements;
    }
    
    /**
     * Compute the integers matching the ranges description.
     * This method validates the ranges description.
     * 
     * @return  a set of Integer instances
     * @throws  RangeSetFormatException 
     *   thrown to indicate that the description is badly formatted
     */
    public Set<Integer> validate() throws RangeSetFormatException {
        
        elements = new HashSet<Integer>();
        
        // null and empty strings
        if ( descr==null || descr.length()==0 )
            throw new RangeSetFormatException("Bad description \""+descr+"\"");
            
        // Split the string around
        String[] ranges = descr.split(";");
        for ( int i=0 ; i < ranges.length ; i++ ) {
            String range = ranges[i];
            
            // null and empty ranges
            if ( range==null || range.length()==0 )
                throw new RangeSetFormatException(
                    "Bad range \"" + range +
                    "\" in description \"" + descr + "\"" );
            
            /*
             * Split each range around -
             * If there is only one element, this is a single value,
             * else this is really a range.
             */
            String[] pairs = range.split("-");
            if ( pairs.length == 1 ) {
                // This is a single value.
                
                try {
                    int value = Integer.parseInt(pairs[0]);
                    elements.add( new Integer(value) );
                }
                catch( NumberFormatException nfe ) {
                    throw new RangeSetFormatException(
                        "Integer \"" + pairs[0] + "\" badly formatted " +
                        "in description \"" + descr + "\"" );
                }
            }
            else {
                /*
                 * This is a range:
                 * - get the 1st and 2nd integers.
                 * - use them as boundaries,
                 * - remaining integers if any, are ignored.
                 */
                if ( pairs[0]==null || pairs[0].length()==0 ||
                     pairs[1]==null || pairs[1].length()==0 )
                    throw new RangeSetFormatException(
                        "Range \"" + range +
                        "\" in description \"" + descr + "\"" +
                        " should have only two boundaries" );
            
                int start = 0;
                int end = 0;
            
                try {
                    start = Integer.parseInt(pairs[0]);
                }
                catch( NumberFormatException nfe ) {
                    throw new RangeSetFormatException(
                        "Integer \"" + pairs[0] + "\" badly formatted " +
                        "in description \"" + descr + "\"" );
                }

                try {
                    end = Integer.parseInt(pairs[1]);
                }
                catch( NumberFormatException nfe ) {
                    throw new RangeSetFormatException(
                        "Integer \"" + pairs[1] + "\" badly formatted " +
                        "in description \"" + descr + "\"" );
                }

                for ( int j=start ; j <= end ; j++ ) {
                    elements.add( new Integer(j) );
                }
            }
        }

        return elements;
    }
}
