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
import java.io.OutputStream;

import org.junit.Test;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class FindBlockAndReplaceOutputStreamTest {

    private void testPattern(
            String input,
            String begin,
            String end,
            String replace,
            String expectedResult ) throws IOException {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os =
            new FindBlockAndReplaceOutputStream(baos,begin,end,replace);
        
        PipedStreams.dump(bais,os);
        os.close();
        
        final String result = baos.toString();
        
        System.out.println("Input  : "+input);
        System.out.println("Begin  : "+begin);
        System.out.println("End    : "+end);
        System.out.println("Replace: "+replace);
        System.out.println("Result : "+result);
        
        if ( ! result.equals(expectedResult) )
            throw new RuntimeException("Bad expected result: "+expectedResult);
    }
    
    @Test
    public void testSingleFindInTheMiddle() throws IOException {

        System.out.println(
      "=== FindBlockAndReplaceOutputStreamTest.testSingleFindInTheMiddle ===");
        
        final String input = "abdcdeazyx";
        final String begin = "dc";
        final String end = "zy";
        final String replace = "---";
        final String expectedResult = "ab---x";
        
        testPattern(input,begin,end,replace,expectedResult);
    }
    
    @Test
    public void testMultipleFindInTheMiddle() throws IOException {

        System.out.println(
      "=== FindBlockAndReplaceOutputStreamTest.testSingleFindInTheMiddle ===");
        
        final String input = "abdcdeazyxdcazyb";
        final String begin = "dc";
        final String end = "zy";
        final String replace = "---";
        final String expectedResult = "ab---x---b";
        
        testPattern(input,begin,end,replace,expectedResult);
    }
    
    @Test
    public void testSingleFindAtTheBeginning() throws IOException {

        System.out.println(
  "=== FindBlockAndReplaceOutputStreamTest.testSingleFindAtTheBeginning ===");
        
        final String input = "abdcdeazyxdcabzyb";
        final String begin = "ab";
        final String end = "dea";
        final String replace = "---";
        final String expectedResult = "---zyxdcabzyb";
        
        testPattern(input,begin,end,replace,expectedResult);
    }
    
    @Test
    public void testSingleFindAtTheEnd() throws IOException {

        System.out.println(
  "=== FindBlockAndReplaceOutputStreamTest.testSingleFindAtTheEnd ===");
        
        final String input = "acdeazyxdcabzyb";
        final String begin = "ab";
        final String end = "b";
        final String replace = "---";
        final String expectedResult = "acdeazyxdc---";
        
        testPattern(input,begin,end,replace,expectedResult);
    }
}
