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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This program changes the version number of a Maven project.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MvnChangeProjectVersionNumber {

    public static void main(String[] args) throws IOException {
        
        if( args.length != 2 ) {
            usage();
            return;
        }
        
        String find = args[0];
        String replace = args[1];
        File current = new File(".");
        
        List<File> poms = new ArrayList<File>();
        findPoms(current,poms);
        
        for (File pom : poms) {
            System.out.print(pom.getAbsolutePath()+":");
            changeProjectVersionNumber(pom,find,replace);
        }
    }
    
    private static void usage() {
        System.out.println("Usage: java "+MvnChangeProjectVersionNumber.class.getName()+" <find> <replace>");
    }
    
    /**
     * Recursively scan the given directory and add pom.xml files in the
     * specified list.
     *  
     * @param dir   the directory to scan
     * @param poms  the list of pom.xml files
     */
    private static void findPoms( File dir, List<File> poms ) {
        
        File[] files =
            dir.listFiles(
                new FilenameFilter(){
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.equals("pom.xml");
                    }});
        poms.addAll( Arrays.asList(files) );
        
        /*
         * Recursively scan sub directories.
         */
        File[] subs = dir.listFiles();
        for (File sub : subs) {
            if( sub.isDirectory() ) {
                findPoms(sub,poms);
            }
        }
    }
    
    /**
     * Change the version number from <code>find</code> to <code>replace</code>
     * in the specified POM file.
     * 
     * @param pom      the pom.xml file
     * @param find     the original project version number
     * @param replace  the new project version number
     */
    private static void changeProjectVersionNumber(
        File pom, String find, String replace )
    throws IOException {
        
        boolean fileHasChanged = false;
        boolean inParentSection = false;
        
        CharArrayWriter caw = new CharArrayWriter();
        
        FileReader fr = new FileReader(pom);
        BufferedReader br = new BufferedReader(fr);        
        String line = br.readLine();
        int linenumber = 0;
        
        while( line != null ) {
            
            String[] tokens = line.split(" |\t");
            boolean lineHasChanged = false;
            
            for (String token : tokens) {
                
                if( inParentSection && token.startsWith("<version>") ) {
                    String versionNumber = token.substring(9,token.length()-10);
                    
                    if( versionNumber.equals(find) ) {
                        fileHasChanged = true;
                        lineHasChanged = true;
                        System.out.print(" line "+linenumber+".");
                        
                        int firstChar = line.indexOf("<version>");                        
                        caw.write(line.substring(0,firstChar));
                        caw.write("<version>");
                        caw.write(replace);
                        caw.write("</version>");
                    }
                }
                else {
                    if( token.equals("<parent>") ) {
                        inParentSection = true;
                    }
                    else if( token.equals("</parent>") ) {
                        inParentSection = false;
                    }
                }
            }
            
            if(!lineHasChanged) {
                caw.write(line);
            }
            caw.write('\n');
            
            line = br.readLine();
            linenumber++;
        }
        
        br.close();
        fr.close();
        
        if( fileHasChanged ) {
            FileWriter fw = new FileWriter(pom);
            caw.writeTo(fw);
            fw.close();
            System.out.println();
        }
        else {
            System.out.println(" no change.");
        }
    }
}
