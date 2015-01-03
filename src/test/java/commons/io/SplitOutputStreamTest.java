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

package commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class SplitOutputStreamTest extends TestCase {

    public SplitOutputStreamTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SplitOutputStreamTest.class);
    }

    private void testPattern(
            String input,
            String pattern,
            String expectedBefore,
            String expectedAfter ) throws IOException {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        SplitOutputStream sos = new SplitOutputStream(pattern,baos1,baos2);
        
        PipedStreams.dump(bais,sos);
        sos.close();
        
        final String before = baos1.toString();
        final String after = baos2.toString();
        
        System.out.println("Input  : "+input);
        System.out.println("Pattern: "+pattern);
        System.out.println("Before : "+before);
        System.out.println("After  : "+after);
        
        if ( ! before.equals(expectedBefore) )
            throw new RuntimeException("Bad expected before: "+expectedBefore);
        if ( ! after.equals(expectedAfter) )
            throw new RuntimeException("Bad expected after: "+expectedAfter);
    }
    
    public void testPatternInTheMiddle() throws IOException {

        System.out.println(
                "=== SplitOutputStreamTest.testPatternInTheMiddle ===");
        
        final String input = "abdcdzy";
        final String pattern = "dcd";
        final String expectedBefore = "ab";
        final String expectedAfter = "zy";
        
        testPattern(input,pattern,expectedBefore,expectedAfter);
    }
    
    public void testPatternAtTheBeginning() throws IOException {

        System.out.println(
                "=== SplitOutputStreamTest.testPatternAtTheBeginning ===");
        
        final String input = "abdcdzy";
        final String pattern = "abd";
        final String expectedBefore = "";
        final String expectedAfter = "cdzy";
        
        testPattern(input,pattern,expectedBefore,expectedAfter);
    }
    
    public void testPatternAtTheEnd() throws IOException {

        System.out.println(
                "=== SplitOutputStreamTest.testPatternAtTheEnd ===");
        
        final String input = "abdcdzy";
        final String pattern = "zy";
        final String expectedBefore = "abdcd";
        final String expectedAfter = "";
        
        testPattern(input,pattern,expectedBefore,expectedAfter);
    }
}
