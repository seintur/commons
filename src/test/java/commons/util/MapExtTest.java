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

import junit.framework.TestCase;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MapExtTest extends TestCase {

	public MapExtTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MapExtTest.class);
	}

	public void testSubtract() {

        System.out.println("=== MapExtTest.subtract() ===");

        /**
         * [13, 14, 12] - [14, 12] =? [13]
         */
        Map<Integer,String> src = new HashMap<Integer,String>();
        src.put( new Integer(12), "s12" );
        src.put( new Integer(13), "s13" );
        src.put( new Integer(14), "s14" );
        
        Set<Integer> dst = new HashSet<Integer>();
        dst.add( new Integer(12) );
        dst.add( new Integer(14) );
        
        Map result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 1 || !result.keySet().contains(new Integer(13)) )
            throw new RuntimeException("MapExt.subtract() failed");

        /**
         * [13, 14, 12] - [141, 121] =? [13, 14, 12]
         */
        dst = new HashSet<Integer>();
        dst.add( new Integer(121) );
        dst.add( new Integer(141) );
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 3 ||
             !result.keySet().contains(new Integer(13)) ||
             !result.keySet().contains(new Integer(14)) ||
             !result.keySet().contains(new Integer(12)) )
            throw new RuntimeException("MapExt.subtract() failed");

        /**
         * [13, 14, 12] - [13, 14, 12] =? []
         */
        dst = new HashSet<Integer>();
        dst.add( new Integer(13) );
        dst.add( new Integer(14) );
        dst.add( new Integer(12) );
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 0 )
            throw new RuntimeException("MapExt.subtract() failed");

        /**
         * [13, 14, 12] - [13, 14, 12, 11] =? []
         */
        dst = new HashSet<Integer>();
        dst.add( new Integer(13) );
        dst.add( new Integer(14) );
        dst.add( new Integer(12) );
        dst.add( new Integer(11) );
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 0 )
            throw new RuntimeException("MapExt.subtract() failed");
        
        /**
         * [13, 14, 12] - [] =? [13, 14, 12]
         */
        dst = new HashSet<Integer>();
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 3 ||
             !result.keySet().contains(new Integer(13)) ||
             !result.keySet().contains(new Integer(14)) ||
             !result.keySet().contains(new Integer(12)) )
            throw new RuntimeException("MapExt.subtract() failed");

        /**
         * [] - [13, 14, 12] =? []
         */
        src = new HashMap<Integer,String>();
        dst = new HashSet<Integer>();
        dst.add( new Integer(13) );
        dst.add( new Integer(14) );
        dst.add( new Integer(12) );
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 0 )
            throw new RuntimeException("MapExt.subtract() failed");

        /**
         * [] - [] =? []
         */
        dst = new HashSet<Integer>();
        
        result = MapExt.subtract(src,dst);
        System.out.println(src+" - "+dst+" = "+result);
        if ( result.size() != 0 )
            throw new RuntimeException("MapExt.subtract() failed");
	}

}
