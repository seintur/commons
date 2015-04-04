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

import java.io.OutputStream;

import commons.io.PipedStreams;
import commons.io.TrimOutputStream;

/**
 * This class trims a file by removing the bytes found before a begin marker
 * and after a end marker. Hence only the bytes found between the two markers
 * are kept (excluding the marker).
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Trim {
    
    private static void Usage() {
        System.out.println(
                "Usage: java common.main.Trim beginMarker endMarker");
        System.out.println(
"This program filters an input stream by removing all data found before "+
"the beginMarker (including the marker itself) and after the endMarker "+
"(including the marker itself)" );
    }

    public static void main(String[] args) throws Exception {
        
        if ( args.length != 2 ) {
            Usage();
            return;
        }
        
        final String begin = args[0];
        final String end = args[1];
        
        OutputStream os = TrimOutputStream.create(System.out,begin,end);
        PipedStreams.dump(System.in,os);
        os.close();
    }
}
