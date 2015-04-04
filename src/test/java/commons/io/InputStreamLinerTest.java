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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InputStreamLinerTest {

    @Test
    public void testReadLine() throws IOException {
        
        final String winFileName = "commons/io/InputStreamLinerTest.win.txt";
        testReadLine( winFileName, InputStreamLiner.WIN );
        
        final String unixFileName = "commons/io/InputStreamLinerTest.unix.txt";
        testReadLine( unixFileName, InputStreamLiner.UNIX );
        
        final String macFileName = "commons/io/InputStreamLinerTest.mac.txt";
        testReadLine( macFileName, InputStreamLiner.MAC );
    }
    
    private void testReadLine( String name, int type ) throws IOException {
        
        System.out.println(
            "=== InputStreamLinerTest.testReadLine("+
            type+") ===" );
            
        System.out.println("Reading: "+name);
        InputStream is = ClassLoader.getSystemResourceAsStream(name);
        InputStreamLiner isl = new InputStreamLiner(is);
        
        String line = isl.readLine(type);
        System.out.println("Line 0 ("+line.length()+" bytes): "+line);
        line = isl.readLine(type);
        System.out.println("Line 1 ("+line.length()+" bytes): "+line);
        
        int b = is.read();
        System.out.println("Line 2, byte 0: "+b+" "+new String(new byte[]{(byte)b}));
        b = is.read();
        System.out.println("Line 2, byte 1: "+b+" "+new String(new byte[]{(byte)b}));
        line = isl.readLine(type);
        System.out.println("Line 2 ("+line.length()+" bytes): "+line);
        
        isl.close();
    }
}
