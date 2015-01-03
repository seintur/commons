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

package commons.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

/**
 * This class provides methods to read a multipart input stream
 * such as the ones generated by a multipart/form-data encoded web form.
 *
 * A multipart input stream is of the form:
 * ------------foo
 * headers (several lines)
 * (empty line)
 * content
 * ------------foo
 * headers (several lines)
 * (empty line)
 * content
 * ------------foo
 * ....
 * ------------foo--
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MultipartDecoder {

    /** The stream to read the multipart from. */
    private InputStream is;
    
    /** The separator sequence between each part of the multipart. */
    private byte[] separator;

    /** A buffer used to read data from the underlying input stream. */
    private ByteArrayOutputStream baos; 
    
    
    public MultipartDecoder( InputStream is ) throws IOException {
        
        this.is = is;

        /**
         * The first line of the multipart gives the separator.
         * For computational purpose, our looked for separator starts with 0xd 0xa
         *
         * Discard the return value from readLine().
         * If the stream ends after the separator line,
         * it will be detected when nextPart() is called.
         */
        baos = new ByteArrayOutputStream();
        baos.write(0xd);
        baos.write(0xa);
        readLine(baos);
        separator = baos.toByteArray();
    }


    /**
     * Return the headers of the current part.
     * The headers end when an empty line is met.
     *
     * @return  the headers or null if the end of the stream is met
     */
    public List getHeaders() throws IOException {

        baos.reset();
        if ( readLine(baos) == -1 ) return null;
        byte[] read = baos.toByteArray();

        Vector<String> headers = new Vector<String>();
        
        while ( read.length != 0 ) {
            headers.add( new String(read) );
            
            baos.reset();
            if ( readLine(baos) == -1 ) return null;
            read = baos.toByteArray();
        }

        return headers;
    }


    /**
     * Return an input stream instance to read the content of the current part.
     * The part ends (ie DelimitedInputStream.read() returns -1 )
     * when the separator is met.
     */
    public InputStream getPart() {
        return new DelimitedInputStream(is,separator);
    }

    
    /**
     * Read bytes until 0xd 0xa is encountered (hence an end of line).
     * The bytes are stored is the given ByteArrayOutputStream instance.
     *
     * @param baos  the ByteArrayOutputStream instance to store read bytes
     * @return      -1 if the end of stream is encountered before the end of line
     *              any other value else
     */
    private int readLine( ByteArrayOutputStream baos ) throws IOException {

        int b = is.read();
        if ( b == -1 ) return -1;
        
        int lookahead = is.read();
        if ( lookahead == -1 ) return -1;

        if ( b==0xd && lookahead==0xa ) return 0;

        while ( !(b==0xd && lookahead==0xa) ) {
            baos.write(b);
            b = lookahead;
            lookahead = is.read();
            if ( lookahead == -1 ) return -1;
        }

        return 0;
    }
}
