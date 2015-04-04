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
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;

import commons.io.PipedStreams;

/**
 * This class executes a command with stderr redirected to stdout.
 * The command is stored in a file whose name is given in the command line
 * parameters.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MergeErrAndOut {

    public static void main( String[] args )
    throws InterruptedException, IOException {

        if ( args.length != 1 ) {
            System.out.println( "Usage: java common.lang.MergeErrAndOut file" );
            System.exit(0);
        }

        // Read the command from the file
        BufferedReader br = new BufferedReader( new FileReader(args[0]) );
        String command = br.readLine();
        br.close();
        
        // Execute the command
        Runtime runtime = Runtime.getRuntime();
        System.out.println( command );
        Process process = runtime.exec( command );

        // Redirect the error stream to the output stream
        InputStream err = process.getErrorStream();
        PipedStreams.dump(err,System.out);

        // Wait for the end of the process
        process.waitFor();
    }
}
