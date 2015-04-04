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

package commons.ipf;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import commons.annotations.AnnotationHelper;
import commons.lang.ClassHelper;
import commons.reflect.AccessibleObjectHelper;
import commons.reflect.SetterMethodFilter;

/**
 * This class manages the injection points of a given type for a specified
 * class. The type is an annotation such as the one for properties or
 * references. The injection points are stored in the inherited map and are
 * indexed by their name. The name is typically a property or a reference name.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class CompositeInjectionPointHashMap
extends InjectionPointHashMap<Annotation> {
    
    private static final long serialVersionUID = -2199033442372122424L;

    /** The injection point type names. */
    private String[] annotClassNames;
    
    public CompositeInjectionPointHashMap( Class<?> cl, String... annotClassNames ) {
        super(cl,Annotation.class);
        this.annotClassNames = annotClassNames;
    }
    
    /**
     * Register all injection points by introspecting the class associated with
     * this instance. Discard overridden injection points.
     * 
     * @throws DuplicationInjectionPointException
     *         if for a given name, more than one injection point exist
     */
    @Override
    public void putAll() throws DuplicationInjectionPointException {
        
        AccessibleObject[] aos =
            ClassHelper.getAllAnnotatedSettersAndFields(cl,annotClassNames);        
        for (AccessibleObject ao : aos) {
            
            /*
             * Check whether the accessible object has a 'name' annotation
             * parameter. If the 'name' annotation parameter is not found, infer
             * the name from the setter method name or the field.
             */
            String ipname = getInjectionPointName(ao);
            
            Annotation annot =
                    AccessibleObjectHelper.getAnnotation(ao,annotClassNames);
            InjectionPoint<Annotation> ip =
                InjectionPointImpl.getInjectionPoint(ao,annot);

            /*
             * Check whether the name has already been encountered before. If
             * so, this means that more than one injection point (setter or
             * field) exists, which is inconsistent.
             */
            if( containsKey(ipname) ) {
                InjectionPoint<?> other = get(ipname);
                boolean b = ip.override(other);
                if(b) {
                    /*
                     * Another injection point exists for the same name, but the
                     * current one overrides the other one. Keep only the
                     * current one.
                     */
                    remove(other);
                    put(ipname,ip);
                }
                else {
                    /*
                     * Another injection point exists for the same name. If this
                     * other injection points overrides the current one, do
                     * nothing, simply retain the other one and discards the
                     * current one.
                     */
                    b = other.override(ip);
                    if(!b) {
                        /*
                         * Another injection point exists for the same name, and
                         * none of them overrides the other one.
                         */
                        final String str = Arrays.deepToString(annotClassNames);
                        throw new DuplicationInjectionPointException(ipname,cl,str);                        
                    }
                }
            }
            else {
                put(ipname,ip);
            }
        }
    }
    
    /**
     * Register the injection point associated with the specified name by
     * introspecting the class associated with this instance. The name is
     * typically a property or a reference name.
     */
    @Override
    public void put( String name )
    throws NoSuchInjectionPointException, DuplicationInjectionPointException {
        
        AccessibleObject[] aos =
            ClassHelper.getAllAnnotatedSettersAndFields(cl,annotClassNames);   
        boolean found = false;
        for (AccessibleObject ao : aos) {
            
            /*
             * Check whether the accessible object has a 'name' annotation
             * parameter. If the 'name' annotation parameter is not found, infer
             * the name from the setter method name or the field.
             */
            String ipname = getInjectionPointName(ao);

            if( ipname.equals(name) ) {
                
                /*
                 * Check whether the name has already been encountered before.
                 * If so, this means that more than one injection point (setter
                 * or field) exists, which is inconsistent.
                 */
                if( containsKey(name) ) {
                    // TODO check for overriding (as for putAll)
                    final String str = Arrays.deepToString(annotClassNames);
                    throw new DuplicationInjectionPointException(name,cl,str);
                }

                Annotation annot =
                    AccessibleObjectHelper.getAnnotation(ao,annotClassNames);
                InjectionPoint<Annotation> ip =
                    InjectionPointImpl.getInjectionPoint(ao,annot);
                put(name,ip);
                
                /*
                 * Needed to be sure we iterate on all elements of aos to check
                 * for duplicate elements.
                 */
                found = true;
            }
        }
        
        if(!found) {
            throw new NoSuchInjectionPointException(name,cl,annotClassNames);
        }
    }
    
    
    // ---------------------------------------------------------------------
    // Implementation specific
    // ---------------------------------------------------------------------
    
    /**
     * Return the name of the injection point associated with the specified
     * element (setter method or field.) Retrieve the name from the name
     * parameter of the annotation. If missing, infer the name from the setter
     * method or the field.
     */
    private String getInjectionPointName( AccessibleObject ao ) {
        
        /*
         * Check whether the accessible object has a 'name' annotation
         * parameter. If the 'name' annotation parameter is not found, infer
         * the name from the setter method name or the field.
         */
        Annotation annot = AccessibleObjectHelper.getAnnotation(ao,annotClassNames);
        String name = AnnotationHelper.getAnnotationParamValue(annot,"name");
        if( name==null || name.length() == 0 ) {
            if( ao instanceof Method ) {
                Method method = (Method) ao;
                name = SetterMethodFilter.getSetterPropertyName(method);
            }
            else {
                Field field = (Field) ao;
                name = field.getName();
            }
        }
        
        return name;
    }
}
