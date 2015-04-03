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

import java.util.HashMap;
import java.util.Map;

import commons.lang.StringExt;

/**
 * This class provides methods to encode a text according to XML rules.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class XmlEncoder {

    /** Store mappings between accentuated characters and XML encoded version. */
    final protected static Map<String,String> xmlEncodingMap =
		new HashMap<String,String>() {
			private static final long serialVersionUID = -5603820495087104452L;
		{
            put( "<","&lt;" );
            put( ">","&gt;" );
            put( "&","&amp;" );
		}};

    /**
     * Encode a string by replacing accentuated characters by their XML version.
     *
     * @param orig  the original string
     * @return      the modified string
     */
    public static String encodeXMLText( String orig ) {
        return StringExt.adapt( orig, xmlEncodingMap );
    }
}
