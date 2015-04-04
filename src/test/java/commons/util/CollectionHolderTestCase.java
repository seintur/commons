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

package commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * Class for testing the functionalities of the {@link CollectionHolder} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class CollectionHolderTestCase {

    @Test
    public void testRecursiveGet() throws Exception {
        
        System.out.println("=== CollectionExtTest.recursiveGet() ===");

        Model src = Model.getSampleModel1();
        
        testRecursiveGet(
            src,
            new String[]{"getClasses","getAssociationEnds"},
            "[ cl-co, cl-in ]");
        
        testRecursiveGet(
            src,
            new String[]{
                "getClasses","getAssociationEnds", "getAssociation"},
            "[ C-C, C-I ]");
        
        testRecursiveGet(
            src,
            new String[]{
                "getClasses","getAssociationEnds",
                "getAssociation","getAssociationEnds"},
            "[ cl-co, co-cl, cl-in, in-cl ]");
    }
        
    private void testRecursiveGet(
        Model src, String[] methodNames, String expected )
    throws Exception {
        Collection<?> res = CollectionHelper.recursiveGet(src,methodNames);
        System.out.println( expected + " = " + res );
    }
    
    /**
     * This class is part of the data model used to test recursiveGet.
     * Model -> 0..n Clazz -> 0..n AssociationEnd -> 0..1 Association
     * Association -> 0..n AssociationEnd
     */
    public static class Model {
        private String name;
        private Collection<Clazz> classes;
        
        private Model( String name, Clazz... classes ) {
            this.name = name;
            this.classes = Arrays.asList(classes);
        }
        
        public String getName() { return name; }
        public Collection<Clazz> getClasses() { return classes; }
        @Override
        public String toString() { return "Model: " + name; }
        
        private static Model model1;
        
        /**
         * @return  a sample instance of the model with 3 classes and 2 associations.
         */
        public static Model getSampleModel1() {
            
            if ( model1 != null )
                return model1;
            
            Clazz client = new Clazz("Client");
            Clazz command = new Clazz("Command");
            Clazz invoice = new Clazz("Invoice");
        
            AssociationEnd ae1 = new AssociationEnd("cl-co");
            AssociationEnd ae2 = new AssociationEnd("co-cl");
            AssociationEnd ae3 = new AssociationEnd("cl-in");
            AssociationEnd ae4 = new AssociationEnd("in-cl");
        
            Association a1 = new Association("C-C",ae1,ae2);
            Association a2 = new Association("C-I",ae3,ae4);
        
            client.addAssociationEnd(ae1);
            client.addAssociationEnd(ae3);
            command.addAssociationEnd(ae2);
            invoice.addAssociationEnd(ae4);
            
            ae1.setAssociation(a1);
            ae2.setAssociation(a1);
            ae3.setAssociation(a2);
            ae4.setAssociation(a2);
            
            model1 = new Model("Model1",client);
            return model1;
        }
    }
    
    /**
     * This class is part of the data model used to test recursiveGet.
     * Model -> 0..n Clazz -> 0..n AssociationEnd -> 0..1 Association
     * Association -> 0..n AssociationEnd
     */
    public static class Clazz {
        private String name;
        private Collection<AssociationEnd> associationEnds =
            new ArrayList<AssociationEnd>();
        
        public Clazz( String name ) { this.name = name; }
        
        public Collection<AssociationEnd> getAssociationEnds() {
            return associationEnds;
        }
        public String getName() { return name; }
        public void addAssociationEnd(AssociationEnd end) {
            associationEnds.add(end);
        }
        @Override
        public String toString() { return "Clazz: " + name; }
    }

    /**
     * This class is part of the data model used to test recursiveGet.
     * Model -> 0..n Clazz -> 0..n AssociationEnd -> 0..1 Association
     * Association -> 0..n AssociationEnd
     */
    public static class AssociationEnd {
        private String name;
        private Association association;
        
        public AssociationEnd( String name ) { this.name = name; }
        
        public Association getAssociation() { return association; }
        public String getName() { return name; }
        public void setAssociation(Association association) {
            this.association = association;
        }
        @Override
        public String toString() { return "End: " + name; }
    }

    /**
     * This class is part of the data model used to test recursiveGet.
     * Model -> 0..n Clazz -> 0..n AssociationEnd -> 0..1 Association
     * Association -> 0..n AssociationEnd
     */
    public static class Association {
        private String name;
        private Collection<AssociationEnd> associationEnds =
            new ArrayList<AssociationEnd>();
        
        public Association(
            String name, AssociationEnd src, AssociationEnd dst ) {
            this.name = name;
            associationEnds.add(src);
            associationEnds.add(dst);
        }
        
        public Collection<AssociationEnd> getAssociationEnds() {
            return associationEnds;
        }
        public String getName() { return name; }
        @Override
        public String toString() { return "Association: " + name; }
    }    
}
