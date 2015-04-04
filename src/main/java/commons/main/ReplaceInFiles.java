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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import commons.io.FileExt;
import commons.io.FindAndReplaceOutputStream;
import commons.io.PipedStreams;

/**
 * Recursively perform string replacements in a hierarchy of files.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ReplaceInFiles {

    public static void main( String[] args ) throws Exception {
        
        if ( args.length != 4 )
            usage();

        String dir = args[0];
        String extension = args[1];
        String pattern = args[2];
        String replacement = args[3];
        
        File parent = new File(dir);
        if ( ! parent.isDirectory() ) {
            System.out.println( "Error: " + dir + " must be a directory" );
            usage();
        }

        ffJavaFiles = new JavaFilesFileFilter(extension);

        StringBuffer ret = new StringBuffer();
        dir( ret, parent, "", pattern, replacement );
        System.out.println(ret);
    }

    private static void usage() {    
        System.err.println( "Usage: java common.main.ReplaceInFiles <dir> <ext> <pattern> <replacement>" );
        System.err.println( "Recursively perform string replacements in a hierarchy of files." );
        System.err.println( "<dir>         : root directory" );
        System.err.println( "<ext>         : filename extensions" );
        System.err.println( "<pattern>     : string to find" );
        System.err.println( "<replacement> : string replacement" );
        System.exit(1);
    }

    private static void dir(
        StringBuffer ret, File parent, String dir,
        String pattern, String replacement )
        throws IOException {

        /*
         * Apply the patch to the .java file of the current directory.
         */
        File current = new File(parent,dir);
        File[] ls = current.listFiles(ffJavaFiles);
        for ( int i=0 ; i < ls.length ; i++ ) {
            patch(ls[i],pattern,replacement);
        }

        /*
         * Recursively perform the same operation in all sub directories.
         */
        ls = current.listFiles(ffDirs);
        for ( int i=0 ; i < ls.length ; i++ ) {
            String lsName = ls[i].getName();
            String next = dir.length()==0 ? lsName : dir+File.separatorChar+lsName ;
            dir( ret, parent, next, pattern, replacement );
        }
    }

    /**
     * Class for filtering regular files (i.e. that are not directories)
     * and its singleton instance.
     */
    private static class JavaFilesFileFilter implements FileFilter {
        private String extension;
        public JavaFilesFileFilter( String extension ) {
            this.extension = extension;
        }
        public boolean accept( File file ) {
            return file.getName().endsWith(extension) && !file.isDirectory();
        }
    }
    private static FileFilter ffJavaFiles;

    private static void patch( File file, String pattern, String replacement )
        throws IOException {

        File dir = file.getParentFile();
        String filename = file.getName();
        byte[] buffer = new byte[ (int) file.length() ];
        
        InputStream is = new FileInputStream(file);

        File tmp = File.createTempFile(filename,null);
        OutputStream os = new FileOutputStream(tmp);
        OutputStream faros =
            new FindAndReplaceOutputStream(os,pattern,replacement);

        PipedStreams.dump( is, faros, buffer );

        is.close();
        faros.close();

        file.delete();
        File newTmp = new FileExt(tmp).moveFile(dir);
        newTmp.renameTo(file);
    }
    
    /**
     * Class for filtering directories and its singleton instance.
     */
    private static class DirectoriesFileFilter implements FileFilter {
        public boolean accept( File file ) {
            return file.isDirectory();
        }
    }
    private static FileFilter ffDirs = new DirectoriesFileFilter();
}
