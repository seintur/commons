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

package commons.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utility methods for the {@link String} class.
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class StringHelper {
    
   /**
    * Adapt a string by replacing each character that is a key in the map by its value.
    * Keys and values of the map must be strings.
    *
    * @param orig  the original string
    * @param map   the map for characters and their replacement
    * @return      the modified string
    */
   public static String adapt( String orig, Map<String,String> map ) {
   
      if ( orig == null )  return null;
      
      String res = "";
      for ( int i=0 ; i < orig.length() ; i++ ) {
        String c = orig.substring(i,i+1);
        String replace = map.get(c);
        res += (replace==null) ? c : replace;
      }
      
      return res;
   }


   /**
    * Insert the given separator sep between each element and at the end of the
    * given array.
    *
    * @param pathNames  an array
    * @param sep        the separator
    * @return           the resulting string
    */
   public static String insert( String[] pathNames, String sep ) {
      String ret = "";      
      for ( int i=0 ; i < pathNames.length ; i++ )
         ret += pathNames[i] + sep;
      return ret;
   }


   /**
    * Insert the given separator sep between each element of the array elements.
    * Use the arguments header as a prefix and trailer as a suffix.
    *
    * @param elements  an array of elements
    * @param header    the header
    * @param sep       the separator
    * @param trailer   the trailer
    * @return          the resulting string
    */
   public static String insert(
        String[] elements,
        String header,
        String sep,
        String trailer ) {
      
      String ret = header;
      for ( int i=0 ; i < elements.length ; i++ ) {
         ret += elements[i];
         if ( i < (elements.length-1) )  ret += sep;
      }
      return ret+trailer;
   }


    /** Store mappings between accentuated characters and their non-accentuated version. */
    final protected static Map<String,String> accentuatedCharMap =
        new HashMap<String,String>() {
            private static final long serialVersionUID = -7119087625689196461L;
        {
            put( "�","e" );  // TODO fix encoding
            put( "�","e" );
            put( "�","e" );
            put( "�","e" );
            put( "�","a" );
            put( "�","a" );
            put( "�","a" );
            put( "�","o" );
            put( "�","o" );
            put( "�","u" );
            put( "�","u" );
            put( "�","i" );
            put( "�","i" );
            put( "�","c" );
        }};

   /**
    * Replace accents by their corresponding non accentuated character.
    * 
    * @param src  the source string
    * @return     the string stripped from accents
    */
   public static String removeAccents( String src ) {
       return adapt( src, accentuatedCharMap );
   }


   /**
    * Replace occurrences of pattern in the current string with newPattern.
    *
    * @param src         the source string
    * @param pattern     the pattern to look for
    * @param newPattern  the string replacement
    * @return            the replaced string
    */
   public static String replace( String src, char pattern, String newPattern ) {
    
      if (src==null)  return null;
      
      StringBuffer buf = new StringBuffer(src);
      
      for ( int i=0 ; i < buf.length() ; ) {
         if ( buf.charAt(i) == pattern ) {
            buf.replace(i,i+1,newPattern);
            i += newPattern.length();
         }
         else i++;
      }
      
      return buf.toString();
   }
   
   
   /**
    * Perform successively several replacements of a pattern in the a string.
    *
    * @param src         the source string
    * @param pattern     the patterns to look for
    * @param newPattern  the string replacements (the array must have the same length as pattern)
    * @return            the replaced string
    */
   public static String replace( String src, char[] pattern, String[] newPattern ) {
    
      if ( src==null || (pattern.length>newPattern.length) ) {
          final String msg =
              "Parameters pattern and newPattern should be arrays of same length";
          throw new IllegalArgumentException(msg);
      }

      for ( int i=0 ; i < pattern.length ; i++ )
        src = replace( src, pattern[i], newPattern[i] );
      
      return src;
   }
   
   
   /**
    * Replace occurrences of pattern in src with newPattern.
    *
    * @param src         the source string
    * @param pattern     the pattern to look for
    * @param newPattern  the string replacement
    * @return            the replaced string
    */
   public static String replace( String src, byte pattern, String newPattern ) {
    
      if (src==null)  return null;
      
      byte[] buf = src.getBytes();
      String ret = "";
      
      for ( int i=0 ; i < buf.length ; i++ )
         ret += (buf[i]==pattern) ? newPattern : new String(new byte[]{buf[i]}) ;
      
      return ret;
   }
      
   /**
    * Perform successively several replacements of a pattern in the a string.
    *
    * @param src         the source string
    * @param pattern     the patterns to look for
    * @param newPattern  the string replacements (the array must have the same length as pattern)
    * @return            the replaced string
    */
   public static String replace( String src, byte[] pattern, String[] newPattern ) {
    
      if ( src==null || (pattern.length>newPattern.length) ) {
          final String msg =
              "Parameters pattern and newPattern should be arrays of same length";
          throw new IllegalArgumentException(msg);
      }
      
      for ( int i=0 ; i < pattern.length ; i++ )
        src = replace( src, pattern[i], newPattern[i] );
      
      return src;
   }
   
   /**
    * Replace occurrences of pattern in src with newPattern.
    *
    * @param src         the source string
    * @param pattern     the pattern to look for
    * @param newPattern  the string replacement
    * @return            the replaced string
    */
   public static String replace( String src, String pattern, String newPattern ) {

       StringBuffer str = new StringBuffer(src.length());
       final int patternLength = pattern.length();
       int fromIndex = 0;
       int findIndex;

       while ( (findIndex=src.indexOf(pattern,fromIndex)) != -1 ) {
            str.append( src.substring(fromIndex,findIndex) );
            str.append( newPattern );
            fromIndex = findIndex + patternLength;
       }

       str.append( src.substring(fromIndex) );
       return str.toString();
   }


    /**
     * Split a string into n elements.
     * The elements are obtained from a StringTokenizer.
     * Trailing elements are empty if the string contains less than n tokens.
     * The n-th element contains the remaining of the string
     * if the string contains more than n tokens.
     *
     * @param str  the input string
     * @param n    the number of elements
     * @return     an array of substrings
     */
    public static String[] split( String str, int n ) {

        StringTokenizer st = new StringTokenizer(str);
        String[] ret = new String[n];

        int i=0;
        for ( ; st.hasMoreTokens() && i<n ; i++ )
            ret[i] = st.nextToken();

        if ( st.hasMoreTokens() ) {
            while( st.hasMoreTokens() )
                ret[n-1] += " " + st.nextToken();
        }
        else {
            for ( ; i<n ; i++ )
                ret[i] = "";            
        }

        return ret;
    }

    /**
     * Split a string into elements of the form (name,value) and return the
     * elements in a hash map.
     *
     * @param src               the source string
     * @param elementSeparator  the separator string for elements
     * @param valueSeparator    the separator string between names and values
     * @return                  a hash table containing all the pairs (name,value)
     */
    public static Map<String,Object> split(
        String src, String elementSeparator, String valueSeparator ) {

        Map<String,Object> ret = new HashMap<String,Object>();
        StringTokenizer st = new StringTokenizer(src,elementSeparator);
        while (st.hasMoreElements()) {
            String element = st.nextToken();
            StringTokenizer st2 = new StringTokenizer(element,valueSeparator);
            if (st2.hasMoreElements()) {
                String name = st2.nextToken();
                Object value = new Object();
                if (st2.hasMoreElements()) {
                    value = st2.nextToken();
                }
                ret.put( name, value );
            }
        }
        return ret;
    }
}
