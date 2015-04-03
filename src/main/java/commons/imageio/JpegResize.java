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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class provides a helper method to resize a JPEG file.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class JpegResize {

	/**
     * Resize a given JPEG file.
     * 
	 * @param src     the JPEG file to resize
	 * @param width   the target width
	 * @param height  the target height
	 * @param dst     the destination file for the resized image
	 * @throws IOException
	 */
    public static void resize( File src, int width, int height, File dst )
    throws IOException {
        
        BufferedImage b = ImageIO.read(src);
        Image img = b.getScaledInstance(width,height,Image.SCALE_DEFAULT);
        
        BufferedImage bi =
            new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bi.createGraphics();
        graphics.drawImage(img,0,0,null);

        ImageIO.write( bi, "JPEG", dst);
        
    }
}
