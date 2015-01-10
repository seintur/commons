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
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
@RunWith(Parameterized.class)
public class FindAndReplaceOutputStreamTest {

	private String[] values;
	private String expected;
	
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new  Object[][]{
				{new String[]{"abdcdeazyx","dc","zyz"},"abzyzdeazyx"},
				{new String[]{"abdcdedcazyx","dc","zyz"},"abzyzdezyzazyx"},
				{new String[]{"abdcdedcabdzyx","abd","z"},"zcdedczzyx"},
				{new String[]{"yxabdyxcdedcabdzyx","yx","jhd"},"jhdabdjhdcdedcabdzjhd"}
			});
	}
	
    public FindAndReplaceOutputStreamTest( String[] values, String expected ) {
		if( values.length != 3 ) {
			final String msg = "values should be an array of 3 strings";
			throw new RuntimeException(msg);
		}
		this.values = values;
		this.expected = expected;
	}
	
	@Test
    public void testPattern() throws IOException {
    
		final String input = values[0];
		final String find = values[1];
		final String replace = values[2];
		
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = new FindAndReplaceOutputStream(baos,find,replace);
        PipedStreams.dump(bais,os);
        os.close();
        
        final String result = baos.toString();        
        Assert.assertEquals(expected,result);        
    }
}
