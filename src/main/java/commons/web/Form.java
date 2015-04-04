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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.io.PipedStreams;

/**
 * This class contains methods to decode data uploaded from a HTML form.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Form {

    /**
     * Parse data encoded with the multipart/form-data encoding rules
     * (e.g. data uploaded from an HTML form containing file upload fields).
     *
     * @param in  the servlet input stream where data is to be read
     * @return    a map where keys are the names of the form fields and values are,
     *            either a string for text input fields,
     *            either an UploadedFile object for file upload fields.
     *            Files are stored in the data field of UploadedFile objects.
     */
    public static Map<String,Object> parseFormData( InputStream in )
    throws IOException {

        MultipartDecoder mpd = new MultipartDecoder(in);
        Map<String,Object> ret = new HashMap<String,Object>();
        List<String> headers;
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        while ( (headers=mpd.getHeaders()) != null ) {
            
            /*
             * Look for the three mentioned attributes in the headers.
             * name is mandatory.
             * filename and contentType are non null only for file upload fields.
             */
            String name = null;
            String filename = null;
            String contentType = null;

            for ( int i=0 ; i < headers.size() ; i++ ) {
                
                String header = headers.get(i);

                name = searchUniqueAttribute(header,"name",name);
                filename = searchUniqueAttribute(header,"filename",filename);

                if ( header.startsWith("Content-Type: ") ) {
                    contentType = header.substring(14);
                }
            }

            /*
             * Get the content of the multipart.
             * The content is either the uploaded file (file upload field)
             * or the value of the input field.
             */
            InputStream is = mpd.getPart();
            content.reset();
            PipedStreams.dump(is,content);
            
            if ( filename != null ) {
                // This is a file upload field
                byte[] data = content.toByteArray();
                UploadedFile file = new UploadedFile( filename, contentType, data );
                ret.put( name, file );
            }
            else {
                String value = content.toString();
                ret.put( name, value );
            }
        }

        return ret;
    }

    /**
     * Parse data encoded with the multipart/form-data encoding rules
     * (e.g. data uploaded from an HTML form containing file upload fields).
     *
     * @param in   the servlet input stream where data is to be read
     * @param dir  the directory where uploaded files are to be saved
     * @return     a map where keys are the names of the form fields and values are,
     *             either a string for text input fields,
     *             either an File object for file upload fields.
     */
    public static Map<String,Object> parseFormData( InputStream in, File dir )
    throws IOException {

        MultipartDecoder mpd = new MultipartDecoder(in);
        Map<String,Object> ret = new HashMap<String,Object>();
        List<String> headers;

        while ( (headers=mpd.getHeaders()) != null ) {
            
            /*
             * Look for the two mentioned attributes in the headers.
             * name is mandatory.
             * filename is non null only for file upload fields.
             */
            String name = null;
            String filename = null;

            for ( int i=0 ; i < headers.size() ; i++ ) {
                String header = headers.get(i);
                name = searchUniqueAttribute(header,"name",name);
                filename = searchUniqueAttribute(header,"filename",filename);
            }

            /*
             * Get the content of the multipart.
             * The content is either the uploaded file (file upload field)
             * or the value of the input field.
             */
            InputStream is = mpd.getPart();
            
            if ( filename != null ) {
                
                /*
                 * This is a file upload field.
                 * Windows browsers send the full path of the uploaded file.
                 * Get rid of the path.
                 * new File(file.filename).getName() does not work on Unix
                 * as the path separator is not \
                 */
                filename = filename.substring( filename.lastIndexOf('\\')+1 );
                File file = new File( dir, filename );
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos,65536);
                PipedStreams.dump(is,bos);
                bos.close();

                ret.put( name, file );
            }
            else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PipedStreams.dump(is,baos);
                String value = baos.toString();
                
                ret.put( name, value );
            }
        }

        return ret;
    }


    /**
     * Search for the value of a given attribute in a header.
     * The header is of the form: attributeName="attributeValue"; ...
     * Throws an exception in case of multiply defined attribute.
     *
     * @param header         the header
     * @param attributeName  the attribute name
     * @param currentValue   the current value of the attribute.
     *                       Any other value than null causes an exception
     *                       if the attribute is found.
     */
    private static String searchUniqueAttribute(
        String header, String attributeName, String currentValue )
    throws IOException {

        String ret = null;
        int index = header.indexOf(attributeName+"=\"");
        if ( index == -1 )
            return currentValue;
            
        if ( currentValue != null ) {
            throw new IOException(
                "Multiply defined attribute " + attributeName +
                " in the headers of the multipart");
        }
        int begin = index + attributeName.length() + 2;
        int end = header.indexOf("\"",begin);
        
        /*
         * When the attribute value is empty,
         * the header is of the form attributeName=""
         * begin == end, and substring return null.
         * Hence the test.
         */
        ret = (end==begin) ? "" : header.substring(begin,end);
        return ret;
    }
}
