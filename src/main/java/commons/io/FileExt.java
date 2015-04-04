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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commons.lang.StringExt;

/**
 * This class holds file related functionalities not found in.
 * java.io.File (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class FileExt extends File {
	
	static final long serialVersionUID = 1191375325949187094L;

	/** Match File constructors. */
    public FileExt( String pathname ) { super(pathname); }
    /** Match File constructors. */
    public FileExt( String parent, String child ) { super(parent,child); }
    /** Match File constructors. */
    public FileExt( File parent, String child ) { super(parent,child); }

    public FileExt( File file ) { this(file.getAbsolutePath()); }

    /**
     * Transform an array of File instances into an array of FileExt instances.
     *
     * @param files  the File instances
     * @return       the FileExt instances
     */
    public static FileExt[] toFileExt( File[] files ) {
        if ( files == null ) {
            return null;
        }
        FileExt[] filesExt = new FileExt[ files.length ];
        for ( int i=0 ; i < files.length ; i++ ) {
            filesExt[i] = new FileExt(files[i]);
        }
        return filesExt;
    }

    /**
     * Compare the content of two files.
     *
     * @param dst  the destination file whose content is to be compared
     *             with the content of the current file
     * @return     true if the binary contents are the same, false otherwise
     */
    public boolean compareContent( File dst ) throws IOException {

        long srcLength = length();
        long dstLength = dst.length();

        if ( srcLength != dstLength ) {
        	return false;
        }

        FileInputStream srcfis = new FileInputStream(this);
        FileInputStream dstfis = new FileInputStream(dst);

        int bsrc = srcfis.read();
        int bdst = dstfis.read();
        while ( bsrc!=-1 && bdst!=-1 && bsrc==bdst ) {
            bsrc = srcfis.read();
            bdst = dstfis.read();
        }

        srcfis.close();
        dstfis.close();

        return (bsrc==bdst);
    }


    /**
     * Perform a file copy.
     *
     * @param dst  the destination file or directory
     * @return     the copied file
     */
    public File copyFile( File dst ) throws IOException {

        File dstFile = dst.isDirectory() ? new File(dst,getName()) : dst;
        FileInputStream fin = new FileInputStream(this);
        FileOutputStream fout = new FileOutputStream(dstFile);

        for ( int b=fin.read() ; b != -1 ; b=fin.read() )
            fout.write(b);

        fout.close();
        fin.close();

        return dstFile;
    }

    /**
     * Get the content of a file in a single byte array.
     *
     * @return  the byte array
     */
    public byte[] getContent() throws IOException {

        FileInputStream fis = new FileInputStream(this);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int b;
        while ( (b=fis.read()) != -1 ) {
            baos.write(b);
        }
        fis.close();

        return baos.toByteArray();
    }
   
    /**
     * Given a file name and a directory, compute an unique file name.
     * If the given file name already exists, append it with a numbered suffix.
     *
     * @param dirName   the directory name
     * @param fileName  the original file name
     * @return          an unique name for the original file
     */
    public static String getUniqueFileName( String dirName, String fileName ) {
        
        File f = new File(dirName,fileName);
        if (!f.exists())  return fileName;

        int lastDotIndex = fileName.lastIndexOf('.');
        String prefix = null;
        String suffix = null;
        if ( lastDotIndex == -1 ) {
            prefix = fileName;
            suffix = "";
        }
        else {
            prefix = fileName.substring(0,lastDotIndex);
            suffix = fileName.substring(lastDotIndex+1);
        }
        int index = 0;
        String newFileName = null;
        do {
            newFileName = prefix+index+"."+suffix;
            f = new File(dirName,newFileName);
            index++;
        } while (f.exists());

        return newFileName;
    }

    /**
     * Return the directory contains in the current directory.
     *
     * @return  the directories or
     *          null if the current instance is not a directory
     */
    public FileExt[] listDirs() {
        if (!isDirectory())  return null;
        return (FileExt[]) listFiles(df);
    }

    final static protected DirFilter df = new DirFilter();
    static protected class DirFilter implements FileFilter {
        public boolean accept( File file ) {
            return file.isDirectory();
        }
    }

    /**
     * Move all the files of the current directory.
     *
     * @param dst  the destination directory
     */
    public void moveFiles( File dst ) throws java.io.IOException {

        if ( ! dst.isDirectory() ) {
            throw new IOException( dst + " is not a directory." );
        }

        File[] files = listFiles();
        if ( files == null )  return;

        /*
         * Copy each file individually.
         * Do not use moveFile() as we want to delete only once
         * all files have been copied.
         */
        for ( int i=0 ; i < files.length ; i++ ) {
            ((FileExt)files[i]).copyFile(dst);
        }

        // Delete files
        for ( int i=0 ; i < files.length ; i++ ) {
            files[i].delete();
        }
    }

    /**
     * Move the current files to a destination directory.
     *
     * @param dst  the destination directory
     * @return     the copied file
     */
    public File moveFile( File dst ) throws IOException {

        if ( ! isFile() ) {
        	throw new IOException( "Current FileExt instance is not a file." );
        }
        if ( ! dst.isDirectory() ) {
            throw new IOException( dst + " is not a directory." );
        }

        File dstFile = copyFile(dst);
        delete();
        return dstFile;
    }

    /**
     * Recursively list the files contained in the current directory.
     *
     * @return     the files or null if the current instance is not a directory
     */
    public FileExt[] recursiveListFiles() {
    	return recursiveListFiles(allFiles);
    }
    
    final private static FilenameFilter allFiles =
    	new FilenameFilter() {
    		public boolean accept( File f, String s ) {
    			return true;
    		}
    	};
    
    /**
     * Recursively list the files contained in the current directory and whose
     * name matches the given filter.
     *
     * @param fnf  the filename filter
     * @return     the files or null if the current instance is not a directory
     */
    public FileExt[] recursiveListFiles( FilenameFilter fnf ) {

        if (!isDirectory())  return null;

        List<FileExt> files = new ArrayList<FileExt>();
        recursiveListFiles( fnf, files );
        return files.toArray( new FileExt[]{} );
    }

    protected void recursiveListFiles( FilenameFilter fnf, List<FileExt> list ) {

        FileExt[] currentMatchingFiles = (FileExt[]) listFiles(fnf);
        Arrays.sort(currentMatchingFiles);
        list.addAll( Arrays.asList(currentMatchingFiles) );

        FileExt[] dirs = listDirs();
        for ( int i=0 ; i < dirs.length ; i++ ) {
            dirs[i].recursiveListFiles( fnf, list );
        }
    }
    
	/**
     * Return a file system compliant path name from an array of directory names.
     *
     * @param pathNames  an array of directory names
     * @return           the corresponding file system compliant path name 
     */
    public static String toPath( String[] pathNames ) {
        return StringExt.insert(pathNames,File.separator);
    }
    
    /**
	 * Return the line separator sequence associated to the specified text file.
	 * Return one of the three sequences {@link #MAC_LS}, {@link #UNIX_LS} or
	 * {@link #WIN_LS} depending on the text file format. If the file does not
	 * contain any line separator, return {@link #UNIX_LS}.
	 * 
	 * @param f  the text file
	 * @return   the line separator sequence
	 */
	public static char[] getTextFileLineSeparator( File f )
	throws IOException {
		
	    FileReader fr = new FileReader(f);
	    int b = fr.read();
	    while( b != -1 ) {
	    	if( b == 0x0A ) {
	    		// Unix
	    	    fr.close();
	    		return UNIX_LS;
	    	}
	    	if( b == 0x0D ) {
	    		b = fr.read();
	    		if( b == -1 ) {
	        		// Mac
	    		    fr.close();
	        		return MAC_LS;
	    		}
	    		if( b == 0x0A ) {
	    			// Win
	    		    fr.close();
	        		return WIN_LS;
	    		}
	    		else {
	        		// Mac
	    		    fr.close();
	        		return MAC_LS;
	    		}
	    	}
	        b = fr.read();
	    }
	    fr.close();
	    
	    /*
	     * No line separator. Can not infer the text file format. Assume Unix.
	     */
		return UNIX_LS;
	}

	final private static char[] MAC_LS = new char[]{0x0D};
	final private static char[] UNIX_LS = new char[]{0x0A};
	final private static char[] WIN_LS = new char[]{0x0D,0x0A};
}
