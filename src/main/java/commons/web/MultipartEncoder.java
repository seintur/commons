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

import java.util.Map;

/**
 * This class constructs a multipart/form-data encoded input stream.
 * Each part is either a file upload field, or an input field. 
 *
 * A multipart input stream is of the form:
 * <pre>
 * ------------foo
 * Content-Disposition: form-data; name="file upload field name"; filename="uploaded file name"
 * Content-Type: uploaded file MIME type
 * 
 * uploaded file content
 * ------------foo
 * Content-Disposition: form-data; name="input field name"
 * 
 * input field value
 * ------------foo
 * ....
 * ------------foo--
 * </pre>
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class MultipartEncoder {

    /**
     * The parts are given by the Map parameter.
     * Each key of the map is a field name.
     * For input fields, the associated value is the field value.
     * For file upload fields, the associated value is
     * a UploadedFile instance.
     *
     * @param parts  a map containing the parts to encode
     * @return       The encoded parts. For performance issues related
     *               to the implementation, a two dimension array of bytes
     *               is returned. Nevertheless, the two dimension do not
     *               reflect any relevant data structure. The return data
     *               structure is inherently one dimensional.
     */
    public static byte[][] encode( Map<String,Object> parts ) throws Exception {

        String id = Long.toHexString( (long) (Math.random()*1e6) );

        int len = id.length();
        byte[] separator = new byte[ 2 + 30 + len + 2 ];
        separator[0] = 0xd;
        separator[1] = 0xa ;
        for ( int i=2 ; i < 32 ; i++ ) {
            separator[i] = '-';
        }
        System.arraycopy( id.getBytes(), 0, separator, 32, len );
        separator[32+len] = 0xd;
        separator[33+len] = 0xa;

        byte[] header = new byte[ separator.length - 2 ];
        System.arraycopy( separator, 2, header, 0, header.length );
        
        byte[] trailer = new byte[ separator.length + 2 ];
        System.arraycopy( separator, 0, trailer, 0, 32+len );
        trailer[32+len] = '-';
        trailer[33+len] = '-';
        trailer[34+len] = 0xd;
        trailer[35+len] = 0xa;

        byte[] emptyline = new byte[] { 0xd, 0xa, 0xd, 0xa };

        // header + ( headers+emptyline+content+ (separator|trailer) ) + ...
        len = 1 + 4*parts.size();
        byte[][] ret = new byte[len][];
        ret[0] = header;

        int i = 0;
        for (Map.Entry<String,Object> entry : parts.entrySet()) {
			
            String key = entry.getKey();
            Object value = entry.getValue();
            String headers = "Content-Disposition: form-data; name=\"" + key + "\"";
            
            if ( value instanceof String ) {
                ret[ i*4 + 1 ] = headers.getBytes();
                ret[ i*4 + 2 ] = emptyline;
                ret[ i*4 + 3 ] = ((String) value).getBytes();
            }
            else if ( value instanceof UploadedFile ) {
                UploadedFile uf = (UploadedFile) value;
                headers +=
                    "; filename=\"" + uf.filename + "\"" +
                    new String( new byte[]{0xd,0xa} ) +
                    "Content-Type: " + uf.contentType ;
                ret[ i*4 + 1 ] = headers.getBytes();
                ret[ i*4 + 2 ] = emptyline;
                ret[ i*4 + 3 ] = uf.data;
            }
            else {
            	final String msg =
        			"Map part values must only contain String or "+
					"UploadedFile instances";
                throw new Exception(msg);
            }

            ret[ i*4 + 4 ] = separator;
            i++;
        }

        ret[ ret.length - 1 ] = trailer;

        return ret;
    }
}
