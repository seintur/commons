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

import java.io.Serializable;

/**
 * Utility class used as a record by Form.parseFormData()
 * whenever an uploaded file is to be stored.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class UploadedFile implements Serializable {
    
    static final long serialVersionUID = 7991317690591327202L;
    
    /**
     * The remote file name as transmitted by the browser.
     * Windows browsers send the (remote) path and the file name.
     * Other browsers only send the file name.
     */
    public String filename;
    
    /** The MIME type of the file. */
    public String contentType;

    /** Data. */
    public byte[] data;

    public UploadedFile( String filename, String contentType, byte[] data ) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
    }
}
