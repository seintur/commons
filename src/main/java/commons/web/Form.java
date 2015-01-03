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
     * (eg data uploaded from an HTML form containing file upload fields).
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
        List headers;
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        while ( (headers=mpd.getHeaders()) != null ) {
            
            /**
             * Look for the three mentionned attributes in the headers.
             * name is mandatory.
             * filename and contentType are non null only for file upload fields.
             */
            String name = null;
            String filename = null;
            String contentType = null;

            for ( int i=0 ; i < headers.size() ; i++ ) {
                
                String header = (String) headers.get(i);

                name = searchUniqueAttribute(header,"name",name);
                filename = searchUniqueAttribute(header,"filename",filename);

                if ( header.startsWith("Content-Type: ") ) {
                    contentType = header.substring(14);
                }
            }

            /**
             * Get the content of the multipart.
             * The content is either the uploaded file (file upload field)
             * or the value of the input field.
             */
            InputStream is = mpd.getPart();
            content.reset();
            PipedStreams.dump(is,content);
            
            if ( filename != null ) {
                /** This is a file upload field. */
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
     * (eg data uploaded from an HTML form containing file upload fields).
     *
     * @param in   the servlet input stream where data is to be read
     * @param dir  the directory where uploaded files are to be saved
     * @return     a map where keys are the names of the form fields and values are,
     *             either a string for text input fields,
     *             either an File object for file upload fields.
     */
    public static Map<String,Object> parseFormData( InputStream in, File dir ) throws IOException {

        MultipartDecoder mpd = new MultipartDecoder(in);
        Map<String,Object> ret = new HashMap<String,Object>();
        List headers;

        while ( (headers=mpd.getHeaders()) != null ) {
            
            /**
             * Look for the two mentionned attributes in the headers.
             * name is mandatory.
             * filename is non null only for file upload fields.
             */
            String name = null;
            String filename = null;

            for ( int i=0 ; i < headers.size() ; i++ ) {
                String header = (String) headers.get(i);
                name = searchUniqueAttribute(header,"name",name);
                filename = searchUniqueAttribute(header,"filename",filename);
            }

            /**
             * Get the content of the multipart.
             * The content is either the uploaded file (file upload field)
             * or the value of the input field.
             */
            InputStream is = mpd.getPart();
            
            if ( filename != null ) {
                
                /**
                 * This is a file upload field.
                 * Windows browsers send the full path of the uploaded file.
                 * Get rid of the path.
                 * new File(file.filename).getName() does not work on Unix
                 * as the path separator is not \
                 */
                filename = filename.substring( filename.lastIndexOf('\\')+1 );
                File file = new File( dir, filename );
                BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(file), 65536 );
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
        String header, String attributeName, String currentValue ) throws IOException {

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
        /**
         * When the attribute value is empty,
         * the header is of the form attributeName=""
         * begin == end, and substring return null.
         * Hence the test.
         */
        ret = (end==begin) ? "" : header.substring(begin,end);
        // System.out.println( attributeName + " " + begin + " " +end +" " + header + " ["+ret+"]" );
        return ret;
    }


//  /**
//   * Parse data encoded with the multipart/form-data encoding rules
//   * (eg data uploaded from an HTML form containing file upload fields).
//   *
//   * @param in  the servlet input stream where data is to be read
//   * @return    a map where keys are the names of the form fields and values are,
//   *            either a string for text input fields,
//   *            either an UploadedFile object for file upload fields.
//   *            Files are stored in the data fields on UploadedFile objects.
//   */
//  public static Map parseFormData( ServletInputStream in ) throws IOException {
//      
//      /**
//         * We assume there no boundary line longer than 128 bytes (OD OA included).
//         */
//        final int bufMax = 128;
//        byte[] buf = new byte[bufMax];
//      HashMap ret = new HashMap();
//
//      /**
//         * Each element is separated by a boundary line.
//       * At the end of file, the boundary contains 2 dashes ie -- before 0D 0A.
//         */
//      int b = in.readLine(buf,0,buf.length);
//        
//        byte[] boundary = new byte[b];
//        byte[] boundaryAtEOF = new byte[b+2];
//        System.arraycopy( buf, 0, boundary, 0, b );
//        System.arraycopy( buf, 0, boundaryAtEOF, 0, b-2 );
//        boundaryAtEOF[b-2] = '-';
//        boundaryAtEOF[b-1] = '-';
//        boundaryAtEOF[b] = 0x0D;
//        boundaryAtEOF[b+1] = 0x0A;
//
//      /** Get the first line. */
//      b = in.readLine(buf,0,buf.length);
//
//      while ( b != -1 ) {
//          
//          /** -2 discards 0D 0A */
//          String currentLine = new String(buf,0,b-2);
//          HashMap current = StringExt.split(currentLine,";","=");
//
//          /** Discard "" around the field name. */
//          String fieldName = (String) current.get(" name");
//          fieldName = fieldName.substring(1,fieldName.length()-1);
//
//          if ( current.containsKey(" filename") ) {
//              /** This is a file upload field. */
//
//              UploadedFile uf = new UploadedFile();
//              ret.put(fieldName,uf);
//
//              /** filename is surrounded by "" */
//              String filename = (String) current.get(" filename");
//              if ( filename.length() == 2 ) {
//                  b = in.readLine(buf,0,buf.length);
//                  while ( b!=-1 && b!=boundaryLength &&
//                          ! new String(buf,0,b).equals(boundary) ) {
//                      b = in.readLine(buf,0,buf.length);
//                  }
//              }
//              else {
//                  uf.fileName = filename.substring(1,filename.length()-1);
//
//                  /**
//                   * Windows browsers send the full filename, so discard directories.
//                   * // Tomcat does not seem to be able to serve filename with spaces.
//                   */
//                  filename = new File(uf.fileName).getName();
//                  // filename = filename.replace(' ','_');
//
//                  /**
//                     * Get the content-type line.
//                     * Content-Type: foo/bar
//                     */
//                  b = in.readLine(buf,0,buf.length);
//                  String contentTypeLine = new String(buf,0,b-2);
//                  StringTokenizer st = new StringTokenizer(contentTypeLine);
//                  st.nextToken();
//                  uf.contentType = st.nextToken();
//                  
//                  /** Discard the empty line. */
//                  in.readLine(buf,0,buf.length);
//                  
//                  /**
//                     * Iterate to get the file content till the boundary line is found.
//                     */
//                  byte[] prevbuf = new byte[buf.length];
//                  byte[] swap;
//                  int prevb;
//                  boolean eof,boundaryMet;
//                  prevb = in.readLine(prevbuf,0,prevbuf.length);
//                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                  do {
//                      b = in.readLine(buf,0,buf.length);
//                      if ( b == -1 ) {
//                          /**
//                           * If correct, we should never be in this case.
//                           * The stream ends with the boundaryAtEOF (including 0D 0A) string.
//                           * So this should be detected by the else statement.
//                           */
//                          eof = true;
//                      }
//                      else {
//                          currentLine = new String(buf,0,b);
//                          boundaryMet =
//                              currentLine.equals(boundaryAtEOF) ||
//                              currentLine.equals(boundary);
//                          eof = boundaryMet;
//                      }
//                      baos.write(prevbuf,0,eof?prevb-2:prevb);
//
//                      /** Swap prevbuf and buf. */
//                      prevb = b;
//                      swap = buf;
//                      buf = prevbuf;
//                      prevbuf = swap;
//                  }
//                  while ( !eof );
//
//                  /**
//                   * Store the content of the file in
//                   * the data field of the UploadedFile object.
//                   */
//                  uf.data = baos.toByteArray();
//              }
//          }
//          else {
//              /**
//               * This is an input field.
//               * A field value may contain several lines (see <textarea>).
//               */
//              
//              /** Discard the empty line. */
//              in.readLine(buf,0,buf.length);
//              
//              /** Iterate to get the field value till the boundary line is found. */
//              String value = "";
//              byte[] prevbuf = new byte[buf.length];
//              byte[] swap;
//              int prevb;
//              boolean eof,boundaryMet;
//              prevb = in.readLine(prevbuf,0,prevbuf.length);
//              do {
//                  b = in.readLine(buf,0,buf.length);
//                  if ( b == -1 ) {
//                      /**
//                       * If correct, we should never be in this case.
//                       * The stream ends with the boundaryAtEOF (including 0D 0A) string.
//                       * So this should be detected by the code in the else statements.
//                       */
//                      eof = true;
//                  }
//                  else {
//                      currentLine = new String(buf,0,b);
//                      boundaryMet =
//                          currentLine.equals(boundaryAtEOF) ||
//                          currentLine.equals(boundary);
//                      eof = boundaryMet;
//                  }
//                  value += new String(prevbuf,0,eof?prevb-2:prevb);
//                  /** Swap prevbuf and buf. */
//                  prevb = b;
//                  swap = buf;
//                  buf = prevbuf;
//                  prevbuf = swap;
//              }
//              while ( !eof );
//
//              ret.put(fieldName,value);
//          }
//
//          /** Get the next line to carry on parsing. */
//          b = in.readLine(buf,0,buf.length);
//      }
//
//      return ret;
//  }
//
//  /**
//   * Parse data encoded with the multipart/form-data encoding rules
//   * (eg data uploaded from an HTML form containing file upload fields).
//   *
//   * @param in       the servlet input stream where data is to be read
//   * @param dirName  directory name for uploaded files
//   * @return         a hash map where keys are the names of the form fields and values are,
//   *                 either a string for text input fields,
//   *                 either an UploadedFile object for file upload fields.
//   *                 Files are stored in dirName.
//   */
//  public static HashMap parseFormData( ServletInputStream in, String dirName ) throws IOException {
//      
//      HashMap ret = new HashMap();
//      byte[] buf = new byte[128];
//
//      /** Each element is separated by a boundary string. */
//      /** At the end of file, the boundary contains 2 more dashes before 0D 0A */
//      int b = in.readLine(buf,0,buf.length);
//      String boundary = new String(buf,0,b);
//      int boundaryLength = boundary.length();
//      String boundaryAtEOF = new String(buf,0,b-2)+"--"+new String(buf,b-2,2);
//      
//      /** Get the first line. */
//      b = in.readLine(buf,0,buf.length);
//
//      while ( b != -1 ) {
//          
//          /** -2 discards 0D 0A */
//          String currentLine = new String(buf,0,b-2);
//          HashMap current = StringExt.split(currentLine,";","=");
//
//          /** Discard "" around the field name. */
//          String fieldName = (String) current.get(" name");
//          fieldName = fieldName.substring(1,fieldName.length()-1);
//
//          if ( current.containsKey(" filename") ) {
//              /** This is a file upload field. */
//
//              UploadedFile uf = new UploadedFile();
//              ret.put(fieldName,uf);
//
//              /** filename is surrounded by "" */
//              String filename = (String) current.get(" filename");
//              if ( filename.length() == 2 ) {
//                  b = in.readLine(buf,0,buf.length);
//                  while ( b!=-1 && b!=boundaryLength &&
//                          ! new String(buf,0,b).equals(boundary) ) {
//                      b = in.readLine(buf,0,buf.length);
//                  }
//              }
//              else {
//                  uf.fileName = filename.substring(1,filename.length()-1);
//
//                  /**
//                   * Windows browsers send the full filename, so discard directories.
//                   * Tomcat does not seem to be able to serve filename with spaces.
//                   */
//                  filename = new File(uf.fileName).getName();
//                  filename = filename.replace(' ','_');
//
//                  /** Get an unique filename. */
//                  uf.localFileName = FileExt.getUniqueFileName(dirName,filename);
//                  
//                  /** Get the content-type line. */
//                  b = in.readLine(buf,0,buf.length);
//                  String contentTypeLine = new String(buf,0,b-2);
//                  StringTokenizer st = new StringTokenizer(contentTypeLine);
//                  st.nextToken();
//                  uf.contentType = st.nextToken();
//                  
//                  /** Discard the empty line. */
//                  in.readLine(buf,0,buf.length);
//                  
//                  /** Iterate to get the file content till the boundary line is found. */
//                  byte[] prevbuf = new byte[buf.length];
//                  byte[] swap;
//                  int prevb;
//                  boolean eof,boundaryMet;
//                  prevb = in.readLine(prevbuf,0,prevbuf.length);
//                  FileOutputStream fos =
//                      new FileOutputStream( new File(dirName,uf.localFileName) );
//                  do {
//                      b = in.readLine(buf,0,buf.length);
//                      if ( b == -1 ) {
//                          /**
//                           * If correct, we should never be in this case.
//                           * The stream ends with the boundaryAtEOF (including 0D 0A) string.
//                           * So this should be detected by the code in the else statements.
//                           */
//                          eof = true;
//                      }
//                      else {
//                          currentLine = new String(buf,0,b);
//                          boundaryMet =
//                              currentLine.equals(boundaryAtEOF) ||
//                              currentLine.equals(boundary);
//                          eof = boundaryMet;
//                      }
//                      fos.write(prevbuf,0,eof?prevb-2:prevb);
//                      /** Swap prevbuf and buf. */
//                      prevb = b;
//                      swap = buf;
//                      buf = prevbuf;
//                      prevbuf = swap;
//                  }
//                  while ( !eof );
//                  fos.close();
//              }
//          }
//          else {
//              /**
//               * This is an input field.
//               * A field value may contain several lines (see <textarea>).
//               */
//              
//              /** Discard the empty line. */
//              in.readLine(buf,0,buf.length);
//              
//              /** Iterate to get the field value till the boundary line is found. */
//              String value = "";
//              byte[] prevbuf = new byte[buf.length];
//              byte[] swap;
//              int prevb;
//              boolean eof,boundaryMet;
//              prevb = in.readLine(prevbuf,0,prevbuf.length);
//              do {
//                  b = in.readLine(buf,0,buf.length);
//                  if ( b == -1 ) {
//                      /**
//                       * If correct, we should never be in this case.
//                       * The stream ends with the boundaryAtEOF (including 0D 0A) string.
//                       * So this should be detected by the code in the else statements.
//                       */
//                      eof = true;
//                  }
//                  else {
//                      currentLine = new String(buf,0,b);
//                      boundaryMet =
//                          currentLine.equals(boundaryAtEOF) ||
//                          currentLine.equals(boundary);
//                      eof = boundaryMet;
//                  }
//                  value += new String(prevbuf,0,eof?prevb-2:prevb);
//                  /** Swap prevbuf and buf. */
//                  prevb = b;
//                  swap = buf;
//                  buf = prevbuf;
//                  prevbuf = swap;
//              }
//              while ( !eof );
//
//              ret.put(fieldName,value);
//
//              /** Discard the empty line and get the line containing the value.*/
//              // in.readLine(buf,0,buf.length);
//              // b = in.readLine(buf,0,buf.length);
//              // String value = new String(buf,0,b-2);
//              // ret.put(fieldName,value);
//              
//              /** Discard the boundary line. */
//              // in.readLine(buf,0,buf.length);
//          }
//
//          /** Get the next line to carry on parsing. */
//          b = in.readLine(buf,0,buf.length);
//      }
//
//      return ret;
//  }

}
