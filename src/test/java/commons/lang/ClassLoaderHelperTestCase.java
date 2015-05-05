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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import commons.io.FileHelper;

/**
 * Class for testing the functionalities of {@link ClassLoaderHelper}.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ClassLoaderHelperTestCase {

	@Test
	public void testGetClassPathEntries() throws IOException {
		
		File tmp1 = FileHelper.createTempDir("testGetClassPathEntries","");
		File tmp2 = FileHelper.createTempDir("testGetClassPathEntries","");
		String name1 = tmp1.getAbsolutePath();
		String name2 = tmp2.getAbsolutePath();
		
		ClassLoader classLoader =
			new ClassLoader(
				new URLClassLoader(
					new URL[]{new URL("file://"+name1),new URL("file://"+name2)},
					new ClassLoader(
						new URLClassLoader(new URL[]{new URL("file://"+name1)},null)
					){}
				)
			){};
			
		List<String> cpes = ClassLoaderHelper.getClassPathEntries(classLoader);
		
		Assert.assertEquals(3,cpes.size());
		Assert.assertTrue(cpes.get(0).equals(name1));
		Assert.assertTrue(cpes.get(1).equals(name2));
		Assert.assertTrue(cpes.get(0).equals(name1));
	}
}
