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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing the functionalities of the {@link MapHolder} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MapHolderTestCase {

    @Test
    public void testSubtract() {

        /*
         * [13, 14, 12] - [14, 12] =? [13]
         */
        Map<Integer,String> src = new HashMap<Integer,String>();
        src.put( Integer.valueOf(12), "s12" );
        src.put( Integer.valueOf(13), "s13" );
        src.put( Integer.valueOf(14), "s14" );
        
        Set<Integer> dst = new HashSet<Integer>();
        dst.add( Integer.valueOf(12) );
        dst.add( Integer.valueOf(14) );
        
        Map<Integer,String> result = MapHelper.subtract(src,dst);
        if ( result.size() != 1 || !result.keySet().contains(Integer.valueOf(13)) )
            Assert.fail();

        /*
         * [13, 14, 12] - [141, 121] =? [13, 14, 12]
         */
        dst = new HashSet<Integer>();
        dst.add( Integer.valueOf(121) );
        dst.add( Integer.valueOf(141) );
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 3 ||
             !result.keySet().contains(Integer.valueOf(13)) ||
             !result.keySet().contains(Integer.valueOf(14)) ||
             !result.keySet().contains(Integer.valueOf(12)) )
            Assert.fail();

        /*
         * [13, 14, 12] - [13, 14, 12] =? []
         */
        dst = new HashSet<Integer>();
        dst.add( Integer.valueOf(13) );
        dst.add( Integer.valueOf(14) );
        dst.add( Integer.valueOf(12) );
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 0 )
            Assert.fail();

        /*
         * [13, 14, 12] - [13, 14, 12, 11] =? []
         */
        dst = new HashSet<Integer>();
        dst.add( Integer.valueOf(13) );
        dst.add( Integer.valueOf(14) );
        dst.add( Integer.valueOf(12) );
        dst.add( Integer.valueOf(11) );
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 0 )
            Assert.fail();
        
        /*
         * [13, 14, 12] - [] =? [13, 14, 12]
         */
        dst = new HashSet<Integer>();
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 3 ||
             !result.keySet().contains(Integer.valueOf(13)) ||
             !result.keySet().contains(Integer.valueOf(14)) ||
             !result.keySet().contains(Integer.valueOf(12)) )
            Assert.fail();

        /*
         * [] - [13, 14, 12] =? []
         */
        src = new HashMap<Integer,String>();
        dst = new HashSet<Integer>();
        dst.add( Integer.valueOf(13) );
        dst.add( Integer.valueOf(14) );
        dst.add( Integer.valueOf(12) );
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 0 )
            Assert.fail();

        /*
         * [] - [] =? []
         */
        dst = new HashSet<Integer>();
        
        result = MapHelper.subtract(src,dst);
        if ( result.size() != 0 )
            Assert.fail();
    }
}
