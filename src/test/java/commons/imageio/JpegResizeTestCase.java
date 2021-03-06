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

package commons.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing the functionalities of the {@link JpegResize} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class JpegResizeTestCase {
    
    @Test
    public void testResize() throws IOException {

        final String srcFileName = "src/test/resources/commons/imageio/src.jpg";
        final String dstFileName = "target/classes/commons/imageio/dst.jpg";
        
        File src = new File(srcFileName);
        File dst = new File(dstFileName);
        dst.delete();
        
        int targetWidth = 500;
        int targetHeight = 400;
        
        JpegResize.resize(src,targetWidth,targetHeight,dst);
        
        // Read the produced image to check its size
        BufferedImage b = ImageIO.read(dst);
        int width = b.getWidth();
        int height = b.getHeight();
        
        Assert.assertEquals(width,targetWidth);        
        Assert.assertEquals(height,targetHeight);
    }
}
