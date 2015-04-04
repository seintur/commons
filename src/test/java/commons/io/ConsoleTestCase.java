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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for testing the functionalities of {@link Console}s.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ConsoleTestCase {
    
    private Console console;
    
    @Before
    public void setUp() {
        console = Console.getConsole("test");
    }

    @After
    public void tearDown() {
        console.close();
    }

    @Test
    public void testEqualsOneLine() {
        final String expected = "Hello World!";
        console.print(expected);
        console.assertEquals(new String[]{expected});
    }

    @Test
    public void testEqualsSeveralLines() {
        final String expected0 = "Hello ";
        final String expected1 = "World!";
        console.println(expected0);
        console.println(expected1);
        console.assertEquals(new String[]{expected0,expected1});
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testDiffersOneLine() {
        console.print('a');
        console.assertEquals(new String[]{"b"});
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testDiffersMoreThanExpected() {
        console.println("ab");
        console.println("cd");
        console.assertEquals(new String[]{"ab","c"});
        Assert.fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testDiffersFewerThanExpected() {
        console.println("ab");
        console.println("c");
        console.assertEquals(new String[]{"ab","cd"});
        Assert.fail();
    }
}
