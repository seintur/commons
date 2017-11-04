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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a readLine() method for InputStreams objects.
 * Depending on the system, three specific method may be called:
 * readLineUnix(), readLineWin(), and readLineMac().
 * readLine(int) is a wrapper for these three methods.
 * The line delimiters for these systems are:
 * <ul>
 * <li>Unix: 0x0A</li>
 * <li>Windows: 0x0D 0x0A</li>
 * <li>Mac: 0x0D</li>
 * </ul>
 * 
 * This class intends to be a simple alternative to constructing
 * an InputStreamReader and then a BufferedReader 
 * to call BufferedReader.readLine().
 * Moreover this last solution may be inappropriate as data read like
 * this, are interpreted according to a (default) character encoding.
 * Hence, InputStreamLiner allows to keep reading data from
 * the InputStream as raw bytes together with the ability
 * to call readLine().
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InputStreamLiner extends FilterInputStream {
    
    public InputStreamLiner(InputStream in) {
        super(in);
    }

    final static public int UNIX = 0;
    final static public int WIN = 1;
    final static public int MAC = 2;
    
    /**
     * General method to read a line from the input stream.
     * Depending on the value of the fileType parameter, one of three
     * other method of the class is called.
     * 
     * @param fileType  enumerated value: either UNIX, or WIN, or MAC 
     */
    public String readLine( int fileType ) throws IOException {
        switch(fileType) {
            case UNIX : return readLineUnix();
            case WIN : return readLineWin();
            case MAC : return readLineMac();
            default :
                throw new IllegalArgumentException("Illegal value: "+fileType);
        }
    }
    

    /**
     * Read bytes from the underlying input stream up to the next
     * pattern of bytes 0x0D 0x0A
     * 
     * @return  the read bytes (striped of 0x0D 0x0A) as a String or
     *          null if the end of stream is met
     */
    public String readLineWin() throws IOException {
        
        boolean first = true;
        buffer.clear();
        
        while (true) {
            
            int b = read();
            if (b==-1) {
                if (first)
                    return null;
                return bufferContent();
            }
            first = false;
        
            if ( b == 0x0D ) {
                b = read();
                if ( b==-1 || b==0x0A )
                    return bufferContent();
                buffer.add(Integer.valueOf(0x0D));
                buffer.add(Integer.valueOf(b));
            }
            else
                buffer.add(Integer.valueOf(b));
        }
    }
    

    /**
     * Read bytes from the underlying input stream up to the next
     * 0x0A byte.
     * 
     * @return  the read bytes (striped of 0x0A) as a String or
     *          null if the end of stream is met
     */
    public String readLineUnix() throws IOException {
        
        int b = read();
        if (b==-1)  return null;
        
        buffer.clear();
        while ( b!=-1 && b!=0x0A ) {
            buffer.add(Integer.valueOf(b));
            b = read();
        }
        return bufferContent();
    }
    

    /**
     * Read bytes from the underlying input stream up to the next
     * 0x0D byte.
     * 
     * @return  the read bytes (striped of 0x0A) as a String or
     *          null if the end of stream is met
     */
    public String readLineMac() throws IOException {
        
        int b = read();
        if (b==-1)  return null;
        
        buffer.clear();
        while ( b!=-1 && b!=0x0D ) {
            buffer.add(Integer.valueOf(b));
            b = read();
        }
        return bufferContent();
    }


    /** Buffer used by readLine(). Store Integer objects. */
    private List<Integer> buffer = new ArrayList<Integer>();

    /** Return the content of the buffer as a String. */
    private String bufferContent() {
        
        Integer[] content = buffer.toArray(integerArray);
        byte[] b = new byte[content.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = content[i].byteValue();
        }
        
        return new String(b);
    }
    
    /** Working variable used by bufferContent(). */
    final static private Integer[] integerArray = new Integer[0];
}
