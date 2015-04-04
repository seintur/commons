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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * HTML fragment for selecting an item presented in a HTML table.
 * 
 * This abstract class does not provide any implementation for
 * the items container.
 * 
 * This class provides the following features:
 * <ul>
 * <il>items are presented vertically in a HTML table</li>
 * <li>each item is selectable with a link</li>
 * <li>items can be removed from the list with a single click
 *     on a check box</li>
 * </ul>
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public abstract class HtmlSelector {

    /** The label displayed as caption for the table. */
    protected String tableLabel;

    /**
     * The URL for selecting an item.
     * The item to be selected with be transmitted as an additional
     * parameter name in the URL.
     */
    protected String selectURL;

    /**
     * The URL for removing an item.
     * The item to be selected with be transmitted as an additional
     * parameter name in the URL.
     */
    protected String removeURL;

    /**
     * true if a confirmation dialog bow is to be opened before
     * removing an item.
     */
    protected boolean removeConfirmation = true;

    /** Parameters to be transmitted when removeURL is invoked. */
    protected Map<Object,Object> removeURLParameters;

    /**
     * Prefix for HTML generated elements (JavaScript functions).
     * renderHTML generates HTML code and uniqueness is used as
     * a prefix to avoid name collisions with elements of the
     * document where the fragment will be inserted.
     */
    protected String uniqueness = "_listSelector";

    /**
     * The currently selected item.
     * May be null if no item is selected.
     */
    protected String current;


    /**
     * Render the content of the list as an HTML table.
     * 
     * @param out  the stream where content is to be written
     */
    public abstract void renderHTML(OutputStream out) throws IOException;

    /**
     * Generate the HTML fragment for the script that removes an item.
     * The generated code checks which items are to be removed.
     * The code that effectively performs the removal is generated
     * by renderHTMLForRemoveAction.
     * 
     * @param pw  the print writer stream where content is to be written
     */
    protected void renderHTMLForRemove(PrintWriter pw) {
        
        pw.print("<form ");
            pw.print("action=\""+removeURL+"\" ");
            pw.print("name=\""+uniqueness+"RemoveForm\" ");
            pw.println("method=\"get\">");
            
            if ( removeURLParameters != null ) {
                for (Map.Entry<Object,Object> entry : removeURLParameters.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    pw.print("<input type=\"hidden\" ");
                        pw.print("name=\""+key+"\" ");
                        pw.println("value=\""+value+"\">");
                }
            }
        
            pw.println("<input type=\"hidden\" name=\"items\">");
    
        pw.println("</form>");
        
        pw.println("<script>");        
        pw.println("function "+uniqueness+"RemoveScript() {");
            pw.print("document."+uniqueness+"RemoveForm.items.value = \"\";");
            pw.println("elts = document."+uniqueness+"ItemsForm.elements;");
            pw.println("for ( i=0 ; i < elts.length ; i++ ) {");
                pw.println("if ( elts[i].checked ) {");
                    pw.print("document."+uniqueness+"RemoveForm.items.value");
                    pw.println(" += elts[i].name + \" \";");
                pw.println("}");
            pw.println("}");
            pw.println("if ( document."+uniqueness+"RemoveForm.items.value == \"\" ) {");
                pw.println("/** This should never happen as this script is only invoked by a checkbox of the ItemsForm form. */ ");
                pw.println("alert(\"Select at least one item to remove\");");
            pw.println("}");
            pw.println("else {");
                renderHTMLForRemoveAction(pw);
            pw.println("}");
        pw.println("}");
        pw.println("</script>");
    }

    /**
     * Generate the HTML fragment for the script that removes an item.
     * The generated code effectively performs the removal.
     * The code that before hand checks which items are to be removed
     * is generated by renderHTMLForRemove.
     * 
     * @param pw  the print writer stream where content is to be written
     */
    protected void renderHTMLForRemoveAction(PrintWriter pw) {
        if ( ! removeConfirmation ) {
            pw.println("document."+uniqueness+"RemoveForm.submit();");
            return;
        }
        
        /*
         * For now on, we only deal with cases where a confirmation
         * dialog box is requested.
         */
        pw.println("if ( window.confirm(\"Are you sure you want to remove the selected item ?\") ) {");
            pw.println("document."+uniqueness+"RemoveForm.submit();");
        pw.println("}");
        pw.println("else {");
            /**
             * The removal has been canceled.
             * Uncheck checked boxes.
             */
            pw.println("for ( i=0 ; i < elts.length ; i++ ) {");
                pw.println("if ( elts[i].checked ) {");
                    pw.print("elts[i].checked = false;");
                pw.println("}");
            pw.println("}");
        pw.println("}");
    }

    /**
     * Render the content of the list as an HTML table.
     * 
     * @return  the content as a string
     */
    public String renderHTML() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderHTML(out);
        return out.toString();
    }

    public boolean isRemoveConfirmation() {
        return removeConfirmation;
    }

    public String getCurrent() {
        return current;
    }

    public String getRemoveURL() {
        return removeURL;
    }

    public String getSelectURL() {
        return selectURL;
    }

    public String getTableLabel() {
        return tableLabel;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public void setCurrent(String string) {
        current = string;
    }

    public void setRemoveConfirmation(boolean b) {
        removeConfirmation = b;
    }

    /**
     * @param string      the URL used to remove an item from the list
     * @param parameters  the parameters to be transmitted
     *                    when the URL is invoked
     */
    public void setRemoveURL(String string, Map<Object,Object> parameters) {
        if ( parameters.containsKey("items") ) {
            final String msg = "Parameters should not contain items";
            throw new IllegalArgumentException(msg);
        }
        removeURL = string;
        removeURLParameters = parameters;
    }

    public void setSelectURL(String string) {
        if ( string.indexOf("name=") != -1 ) {
            final String msg = "URL <"+string+"> should not contain name";
            throw new IllegalArgumentException(msg);
        }
        selectURL = string;
    }

    public void setTableLabel(String string) {
        tableLabel = string;
    }

    public void setUniqueness(String string) {
        uniqueness = string;
    }
}
