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

import java.io.File;
import java.io.IOException;

/**
 * This class provides helper methods for the {@link File} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @author Valerio Schiavoni <valerio.schiavoni@gmail.com>
 * @author Christophe Munilla <Christophe.Munilla@inria.fr>
 */
public class FileHelper {
	
	/**
	 * Create a temporary directory.
	 */
	public static File createTempDir( String prefix, String suffix )
	throws IOException {
		File tmp = File.createTempFile(prefix,suffix);
		tmp.delete();
		tmp.mkdir();
		return tmp;
	}

	/**
	 * If <code>fileName</code> denotes an absolute path, return the
	 * corresponding file. Else, if <code>fileName</code> denotes a relative
	 * path, return the corresponding file rooted from <code>baseDir</code>.
	 */
	public static File getFile( File baseDir, String fileName ) {
	    File f = new File(fileName);
	    if( ! f.isAbsolute() ) {
	        f = new File(baseDir,fileName);
	    }
	    return f;
	}

	/**
	 * Return <code>true</code> if the specified file corresponds to a regular
	 * class path entry.
	 * 
	 * Valerio: This method fixes 
	 * <a href="http://forge.objectweb.org/tracker/index.php?func=detail&aid=312387&group_id=329&atid=350479">bug #312387</a>.
	 * 
	 * Christophe M: Files which are not jars, zips, or directories cause {@link
	 * NullPointerException} when trying to create class path entries from them.
	 * 
	 * @param f  the file to check
	 * @return   <code>true</code> if the class path entry corresponding to the
	 *           specified file is regular
	 * 
	 * @author Valerio Schiavoni <valerio.schiavoni@gmail.com>
	 * @author Christophe Munilla <Christophe.Munilla@inria.fr>
	 */
	public static boolean isRegularClassPathEntry( File f ) {
	    boolean exits = f.exists();
	    boolean isJar = f.getAbsolutePath().endsWith(".jar");
	    boolean isZip = f.getAbsolutePath().endsWith(".zip");
	    boolean isDirectory = f.isDirectory();        
	    return (exits && (isJar || isZip || isDirectory));
	}
}
