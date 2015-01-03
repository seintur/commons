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

import java.io.OutputStream;

/**
 * <p>
 * This class provide a factory method to create streams that remove the bytes
 * located before a begin marker and after an end marker.
 * </p>
 * 
 * <p>
 * The begin marker is searched for first.
 * Hence, if the end marker appears before the begin marker, it is ignored.
 * </p>
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class TrimOutputStream {

    public static OutputStream create(
            OutputStream os,
            byte[] begin,
            byte[] end ) {
        
        DevNullOutputStream devnull = new DevNullOutputStream();
        SplitOutputStream sos1 = new SplitOutputStream(end,os,devnull);
        SplitOutputStream sos2 = new SplitOutputStream(begin,devnull,sos1);
        
        return sos2;
    }

    public static OutputStream create(
            OutputStream os,
            String begin,
            String end ) {
        
        return create(os,begin.getBytes(),end.getBytes());
    }

}
