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
 * Class for testing the functionalities of the {@link InputStreamLiner} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InputStreamLinerTestCase {

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
        
        InputStream is = ClassLoader.getSystemResourceAsStream(name);
        InputStreamLiner isl = new InputStreamLiner(is);
        
        isl.readLine(type);
        isl.readLine(type);
        
        is.read();
        is.read();
        isl.readLine(type);
        
        isl.close();
    }
}
