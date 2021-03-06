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

package commons.main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Extract package names from a hierarchy of .class files.
 * Output generated by this program is particularly useful
 * for feeding command line arguments of the javadoc program.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Directories2Packages {

    public static void main( String[] args ) throws Exception {
        
        if ( args.length != 1 ) {
            
            System.err.println(
                "Usage: java common.main.Directories2Packages <dir>"
            );
            System.exit(1);
        }
        
        File parent = new File(args[0]);
        if ( ! parent.isDirectory() ) {
            System.out.println( "Error: " + args[0] + " must be a directory" );
            System.err.println(
                "Usage: java common.main.Directories2Packages <dir>"
            );
            System.exit(1);
        }

        StringBuffer ret = new StringBuffer();
        dir( ret, parent, "" );
        System.out.println(ret);
    }

    private static void dir( StringBuffer ret, File parent, String dir )
    throws IOException {

        /*
         * Test whether the directory contains only directories.
         * If so, the package contains only sub packages,
         * and we don't generate its name
         * (else javadoc raises an "empty package" error.
         */
        File current = new File(parent,dir);
        File[] ls = current.listFiles(ffRegularFiles);
        if ( ls.length != 0 ) {
            ret.append( dir.replace(File.separatorChar,'.') );
            ret.append( ' ' );
        }

        /*
         * Recursively perform the same operation in all sub directories.
         */
        ls = current.listFiles(ffDirs);
        for ( int i=0 ; i < ls.length ; i++ ) {
            String lsName = ls[i].getName();
            String next =
        		dir.length()==0 ? lsName : dir+File.separatorChar+lsName ;
            dir( ret, parent, next );
        }
        
    }

    /**
     * Class for filtering regular files (i.e. that are not directories)
     * and its singleton instance.
     */
    private static class RegularFilesFileFilter implements FileFilter {
        public boolean accept( File file ) {
            return ! file.isDirectory();
        }
    }
    private static RegularFilesFileFilter ffRegularFiles =
        new RegularFilesFileFilter();
    
    /**
     * Class for filtering directories
     * and its singleton instance.
     */
    private static class DirectoriesFileFilter implements FileFilter {
        public boolean accept( File file ) {
            return file.isDirectory();
        }
    }
    private static DirectoriesFileFilter ffDirs = new DirectoriesFileFilter();
}