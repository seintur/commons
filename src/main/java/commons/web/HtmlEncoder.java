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
 * This class provides methods to encode a text according to HTML rules.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class HtmlEncoder {

    /** Store mappings between accentuated characters and HTML encoded version. */
    final protected static Map<String,String> htmlEncodingMap =
		new HashMap<String,String>() {
			private static final long serialVersionUID = -4204345549342162528L;
		{
            put( "�","&eacute;" );  // TODO fix encoding
            put( "�","&egrave;" );
            put( "�","&ecirc;" );
            put( "�","e" );
            put( "�","&agrave;" );
            put( "�","&acirc;" );
            put( "�","a" );
            put( "�","&ocirc;" );
            put( "�","o" );
            put( "�","&ucirc;" );
            put( "�","u" );
            put( "�","u" );
            put( "�","&icirc;" );
            put( "�","i" );
            put( "�","&ccedil;" );
            put( "<","&lt;" );
            put( ">","&gt;" );
            put( "&","&amp;" );
            put( "%","" );
            put( "�","" );
            put( "�","" );
            put( "�","" );
            put( "#8364;", "E" ); // euro
            put( "\"", "" );
		}};

    /**
     * Encode a string by replacing accentuated characters by their HTML version.
     *
     * @param orig  the original string
     * @return      the modified string
     */
    public static String encodeHTMLText( String orig ) {
        return StringExt.adapt( orig, htmlEncodingMap );
    }
}