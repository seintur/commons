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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTML fragment for selecting an item presented in a HTML table.
 * 
 * This class extends HtmlSelector
 * by implementing the items container as a map.
 * 
 * This class inherits the following features from HtmlSelector:
 * <ul>
 * <il>items are presented vertically in a HTML table</li>
 * <li>each item is selectable with a link</li>
 * <li>items can be removed from the list with a single click
 *     on a check box</li>
 * </ul>
 * 
 * This class adds the ability to
 * display additional information next to each item.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class HtmlMapSelector extends HtmlSelector {
    
    /** The items. */
    private Map<String,String> items;
    
    public HtmlMapSelector() {
        reset();
    }
    
    public HtmlMapSelector(Map<String,String> dst) {
        reset();
        putAll(dst);
    }
    
    public boolean containsKey(String item) { return items.containsKey(item); }
    public void put(String item,String value) { items.put(item,value); }
    public void putAll(Map<String,String> dst) { items.putAll(dst); }
    public void remove(String item) { items.remove(item); }
    public void select(String item) { current=item; }
    
    /** Reset the items container. */
    public void reset() {
        items = new TreeMap<String,String>();
    }
    

    /**
     * Render the items as an HTML table.
     * 
     * @param out  the stream where content is to be written
     */
    public void renderHTML(OutputStream out) throws IOException {
        
        PrintWriter pw = new PrintWriter(out);
        
        pw.println("<table border=\"1\">");
        
        // Table label
        pw.println("<tr><td align=\"center\"><b>"+tableLabel+"</b></td></tr>");
        
        // Select table
        renderHTMLForSelect(pw);
        
        // Remove script
        if ( removeURL != null )
            renderHTMLForRemove(pw);
        
        pw.println("</table>");
        
        pw.flush();
    }
    
    /**
     * Generate the HTML fragment for the table that displays items.
     * This should be inlined in renderHTML(OutputStream) as it is
     * the only place where it is called.
     * For clarity sake, we separate it.
     * 
     * @param pw  the print writer stream where content is to be written
     */
    private void renderHTMLForSelect( PrintWriter pw ) throws IOException {

        /*
         * Inner border-less table to display items.
         * A form is generated to hold remove check boxes.
         */
        pw.println("<tr><td><table width=\"100%\">");
        pw.println("<form name=\""+uniqueness+"ItemsForm\">");
        
        for ( String item : items.keySet() ) {

            pw.println("<tr>");
            pw.print("<td>");
            
            /*
             * Link for selecting the item
             * if the item is not the current one.
             */
            if ( selectURL==null || item.equals(current) ) {
                pw.print(item);
            }
            else {
                pw.print("<a href=\"");
                pw.print(selectURL);
                if ( selectURL.indexOf('?') != -1 )
                    pw.print("&name=");
                else
                    pw.print("?name=");
                pw.print(URLEncoder.encode(item,"ISO-8859-1"));
                pw.print("\">");
                pw.print(item);
                pw.println("</a>");
            }
            pw.println("</td>");
            
            /*
             * Information column.
             */
            pw.print("<td>");
            pw.print(items.get(item));
            pw.println("</td>");
            
            /*
             * Remove item check box.
             */
            if ( removeURL != null ) {
                pw.println(
                    "<td><input type=\"checkbox\" name=\""+
                    item+"\" onclick=\""+
                    uniqueness+"RemoveScript()\"></td>");
            }
            pw.println("</tr>");
        }
        
        pw.println("</form></table></td></tr>");
    }
}
