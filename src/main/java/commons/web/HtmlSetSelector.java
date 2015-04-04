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
import java.util.Set;
import java.util.TreeSet;

/**
 * HTML fragment for selecting an item presented in a HTML table.
 * 
 * This class extends HtmlSelector
 * by implementing the items container as a set.
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
 * add items in the list with a text input.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class HtmlSetSelector extends HtmlSelector {
    
    /**
     * The URL for adding a item.
     * The item to be added with be transmitted as an additional
     * parameter name in the URL.
     */
    private String addURL;
    
    /** Parameters to be transmitted when addURL is invoked. */
    private Map<Object,Object> addURLParameters;
    
    /** The items. */
    private Set<String> items;
    
    public HtmlSetSelector() {
        reset();
    }
    
    public HtmlSetSelector( Set<String> dst ) {
        reset();
        addAll(dst);
    }
    
    public void add(String item) { items.add(item); }
    public void addAll(Set<String> dst) { items.addAll(dst); }
    public boolean contains(String item) { return items.contains(item); }
    public void remove(String item) { items.remove(item); }
    public void select(String item) { current=item; }
    
    /** Reset the items container. */
    public void reset() {
        items = new TreeSet<String>();
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
        
        // Add input text & button
        if ( addURL != null )
            renderHTMLForAdd(pw);
        
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
        
        for (String item : items) {

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
            
            if ( removeURL != null ) {
                // Remove item check box
                pw.println(
                    "<td><input type=\"checkbox\" name=\""+
                    item+"\" onclick=\""+
                    uniqueness+"RemoveScript()\"></td>");
            }
            pw.println("</tr>");
        }
        pw.println("</form></table></td></tr>");
    }
    
    /**
     * Generate the HTML fragment for the form that adds an.
     * This should be inlined in renderHTML(OutputStream) as it is
     * the only place where it is called.
     * For clarity sake, we separate it.
     * 
     * @param pw  the print writer stream where content is to be written
     */
    private void renderHTMLForAdd( PrintWriter pw ) {
        
        pw.print("<form ");
            pw.print("action=\""+addURL+"\" ");
            pw.print("name=\""+uniqueness+"AddForm\" ");
            pw.println("method=\"get\">");
        pw.println("<tr><td>");
        
            if ( addURLParameters != null ) {
                for (Map.Entry<Object,Object> entry : addURLParameters.entrySet()) {                    
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    pw.print("<input type=\"hidden\" ");
                        pw.print("name=\""+key+"\" ");
                        pw.println("value=\""+value+"\">");
                }
            }
        
            pw.println("<input type=\"text\" name=\"name\" size=\"20\">");
            pw.println("<input type=\"submit\" value=\"Add\">");

        pw.println("</td></tr>");
        pw.println("</form>");
    }
    
    public String getAddURL() {
        return addURL;
    }

    /**
     * @param string      the URL used to add an item
     * @param parameters  the parameters to be transmitted
     *                    when the URL is invoked
     */
    public void setAddURL( String string, Map<Object,Object> parameters ) {
        if ( parameters.containsKey("name") ) {
            final String msg = "Parameters should not contain name";
            throw new IllegalArgumentException(msg);
        }
        addURL = string;
        addURLParameters = parameters;
    }
}
