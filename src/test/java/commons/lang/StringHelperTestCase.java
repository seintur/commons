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

package commons.lang;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing the functionalities of {@link StringHelper}.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class StringHelperTestCase extends TestCase {

	@Test
	public void testAround() {
		Assert.assertArrayEquals( new String[0], StringHelper.around("noslash",'/'));
		Assert.assertArrayEquals( new String[]{"before","after"}, StringHelper.around("before/after",'/'));
		Assert.assertArrayEquals( new String[]{"before","//after"}, StringHelper.around("before///after",'/'));
		Assert.assertArrayEquals( new String[]{"","//after"}, StringHelper.around("///after",'/'));
		Assert.assertArrayEquals( new String[]{"before",""}, StringHelper.around("before/",'/'));
	}

	@Test
	public void testTrimHeadingAndTrailingBlank() {
        String src = "  hello\t\n ";
        String res = StringHelper.trimHeadingAndTrailingBlanks(src);
        assertEquals("hello",res);
    }

	@Test
    public void testTrimBlankInTheMiddle() {
        String src = "  hello world\t \nfoo bar\t\n ";
        String res = StringHelper.trimHeadingAndTrailingBlanks(src);
        assertEquals("hello world\t \nfoo bar",res);
    }

	@Test
    public void testTrimEmpty() {
        String src = "";
        String res = StringHelper.trimHeadingAndTrailingBlanks(src);
        assertEquals("",res);
    }

	@Test
    public void testTrimOnlyBlanks() {
        String src = " \n\t \n";
        String res = StringHelper.trimHeadingAndTrailingBlanks(src);
        assertEquals("",res);
    }
}
