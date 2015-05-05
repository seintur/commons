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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import commons.io.FileHelper;

/**
 * This class provides helper methods for the {@link ClassLoader} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @since 2.6
 */
public class ClassLoaderHelper {

    /**
     * Return the class path elements defined by {@link URLClassLoader}
     * contained in the hierarchy of the specified class loader.
     */
    public static List<String> getClassPathEntries( ClassLoader classLoader ) {
        
        List<String> lst = new ArrayList<String>();
        while( classLoader != null ) {
        	
        	if( classLoader instanceof URLClassLoader ) {
        		
                URLClassLoader urlcl = (URLClassLoader) classLoader;
                URL[] urls = urlcl.getURLs();
                for (URL url : urls) {
                    try {
                        File f = new File(url.toURI());
                        boolean isRegular = FileHelper.isRegularClassPathEntry(f);
                        if(isRegular) {
                            lst.add(f.getPath());
                        }
                    } catch (URISyntaxException e) {
                        // just skip this file
                    }
                }
        	}
            
            classLoader = classLoader.getParent();
        }

        return lst;
    }
}
