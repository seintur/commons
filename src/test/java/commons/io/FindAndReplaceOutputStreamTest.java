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
public class FindAndReplaceOutputStreamTest {

    private void testPattern(
            String input,
            String find,
            String replace,
            String expectedResult ) throws IOException {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = new FindAndReplaceOutputStream(baos,find,replace);
        
        PipedStreams.dump(bais,os);
        os.close();
        
        final String result = baos.toString();
        
        System.out.println("Input  : "+input);
        System.out.println("Find   : "+find);
        System.out.println("Replace: "+replace);
        System.out.println("Result : "+result);
        
        if ( ! result.equals(expectedResult) )
            throw new RuntimeException("Bad expected result: "+expectedResult);
    }
    
    @Test
    public void testSingleFindInTheMiddle() throws IOException {

        System.out.println(
           "=== FindAndReplaceOutputStreamTest.testSingleFindInTheMiddle ===");
        
        final String input = "abdcdeazyx";
        final String find = "dc";
        final String replace = "zyz";
        final String expectedResult = "abzyzdeazyx";
        
        testPattern(input,find,replace,expectedResult);
    }
    
    @Test
    public void testSingleMultipleInTheMiddle() throws IOException {

        System.out.println(
       "=== FindAndReplaceOutputStreamTest.testSingleMultipleInTheMiddle ===");
        
        final String input = "abdcdedcazyx";
        final String find = "dc";
        final String replace = "zyz";
        final String expectedResult = "abzyzdezyzazyx";
        
        testPattern(input,find,replace,expectedResult);
    }
    
    @Test
    public void testSingleMultipleAtTheBeginning() throws IOException {

        System.out.println(
              "=== TrimOutputStreamTest.testSingleMultipleInTheBeginning ===");
        
        final String input = "abdcdedcabdzyx";
        final String find = "abd";
        final String replace = "z";
        final String expectedResult = "zcdedczzyx";
        
        testPattern(input,find,replace,expectedResult);
    }
    
    @Test
    public void testSingleMultipleAtTheEnd() throws IOException {

        System.out.println(
          "=== FindAndReplaceOutputStreamTest.testSingleMultipleAtTheEnd ===");
        
        final String input = "yxabdyxcdedcabdzyx";
        final String find = "yx";
        final String replace = "jhd";
        final String expectedResult = "jhdabdjhdcdedcabdzjhd";
        
        testPattern(input,find,replace,expectedResult);
    } 
}
