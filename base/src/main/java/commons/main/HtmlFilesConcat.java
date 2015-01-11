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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import commons.io.FileExt;
import commons.io.FindBlockAndReplaceOutputStream;
import commons.io.PipedStreams;

/**
 * HtmlFilesConcat
 * loads HTML files from the current directory and recursively,
 * from all its sub directories,
 * writes them in one single large HTML file (defaulAllFileName),
 * replaces blocks delimited by
 * a begin string (contained in defaulBeginFileName),
 * a end string (contained in defaulEndFileName),
 * by a replacement string (contained in defaulReplaceFileName).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class HtmlFilesConcat {

    final static protected String defaultAllFileName = "_all.html";

    final static protected String defaultBeginFileName = "_begin.html";
    final static protected String defaultEndFileName = "_end.html";
    final static protected String defaultReplaceFileName = "_replace.html";

    public static void main( String[] args ) throws Exception {
        
        /** Get the current user directory. */
        String userDirName = System.getProperty("user.dir");
        FileExt userDir = new FileExt(userDirName);

        /** Check for files. */
        FileExt beginFile = new FileExt(userDir,defaultBeginFileName);
        FileExt endFile = new FileExt(userDir,defaultEndFileName);
        FileExt replaceFile = new FileExt(userDir,defaultReplaceFileName);

        if ( ! beginFile.exists() )
            System.out.println(
                    defaultBeginFileName + " missing in current directory" );
        if ( ! endFile.exists() )
            System.out.println(
                    defaultEndFileName + " missing in current directory" );
        if ( ! replaceFile.exists() )
            System.out.println(
                    defaultReplaceFileName + " missing in current directory" );

        if ( !beginFile.exists() || !endFile.exists() || !replaceFile.exists() )
            return;
        

        // Delete the result file if it exists
        String allFileName = args.length==1 ? args[0] : defaultAllFileName;
        File allFile = new File(userDir,allFileName);
        allFile.delete();

        // Recursively get all HTML files from the current directory
        FileExt[] htmlFiles = userDir.recursiveListFiles( new HtmlFilesFilter() );

        // Construct an array of FileInputStream instances for all HTML files
        FileInputStream[] fis = new FileInputStream[ htmlFiles.length ];
        for ( int i=0 ; i < htmlFiles.length ; i++ ) {
            fis[i] = new FileInputStream( htmlFiles[i] );
        }

        // Dump all HTML files into the result file
        byte[] begin = beginFile.getContent();
        byte[] end = endFile.getContent();
        byte[] replace = replaceFile.getContent();
        
        FileOutputStream fos = new FileOutputStream(allFile);
        FindBlockAndReplaceOutputStream fbaros =
            new FindBlockAndReplaceOutputStream(fos,begin,end,replace);
        PipedStreams.dump(fis,fbaros);
        fbaros.close();
        for ( int i=0 ; i < fis.length ; i++ ) {
            fis[i].close();
        }
    }   
}

class HtmlFilesFilter implements FilenameFilter {
    public boolean accept( File dir, String name ) {
        return name.endsWith(".html");
    }
}
