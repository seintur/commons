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
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Class for testing the functionalities of the {@link SplitOutputStream} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
@RunWith(Parameterized.class)
public class SplitOutputStreamTestCase {

    private String[] values;
    private String[] expecteds;
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new  Object[][]{
                {new String[]{"abdcdzy","dcd"},new String[]{"ab","zy"}},
                {new String[]{"abdcdzy","abd"},new String[]{"","cdzy"}},
                {new String[]{"abdcdzy","zy"},new String[]{"abdcd",""}}
            });
    }

    public SplitOutputStreamTestCase( String[] values, String[] expecteds ) {
        Assert.assertEquals(2,values.length);
        Assert.assertEquals(2,expecteds.length);
        this.values = values;
        this.expecteds = expecteds;
    }
    
    @Test
    public void testPattern() throws IOException {
        
        final String input = values[0];
        final String pattern = values[1];
        final String expectedBefore = expecteds[0];
        final String expectedAfter = expecteds[1];
        
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        SplitOutputStream sos = new SplitOutputStream(pattern,baos1,baos2);
        PipedStreams.dump(bais,sos);
        sos.close();
        
        final String before = baos1.toString();
        final String after = baos2.toString();
        
        Assert.assertEquals(expectedBefore,before);
        Assert.assertEquals(expectedAfter,after);
    }
}
