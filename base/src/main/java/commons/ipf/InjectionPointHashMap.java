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
import java.util.HashMap;

import commons.reflect.SetterMethodFilter;
import commons.reflect.Util;

/**
 * This class manages {@link InjectionPoint}s.  The injection points are stored
 * in the inherited map and are indexed by their name. The name is typically a
 * property or a reference name.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InjectionPointHashMap<A extends Annotation>
extends HashMap<String,InjectionPoint<A>> {
    
	private static final long serialVersionUID = -2199033442372122424L;

	/** The class. */
    protected Class<?> cl;
    
    /** The annotation. */
    protected Class<A> annotClass;
    
    public InjectionPointHashMap( Class<?> cl, Class<A> annotClass ) {
    	this.cl = cl;
        this.annotClass = annotClass;
    }
    
    /**
     * Register all injection points by introspecting the class associated with
     * this instance. Discard overridden injection points.
     * 
     * @throws DuplicationInjectionPointException
     * 		if for a given name, more than one injection point exist
     */
    public void putAll() throws DuplicationInjectionPointException {
        
        AccessibleObject[] aos =
        	Util.getAllAnnotatedSettersAndFields(cl,annotClass.getName());        
        for (AccessibleObject ao : aos) {
            
            /*
             * Check whether the accessible object has a 'name' annotation
             * parameter. If the 'name' annotation parameter is not found, infer
             * the name from the setter method name or the field.
             */
        	String ipname = getInjectionPointName(ao);
            
        	A annot = ao.getAnnotation(annotClass);
            InjectionPoint<A> ip =
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
                    	throw new DuplicationInjectionPointException(
                    			ipname,cl,annotClass.getName());            			
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
    public void put( String name )
    throws NoSuchInjectionPointException, DuplicationInjectionPointException {
        
        AccessibleObject[] aos =
        	Util.getAllAnnotatedSettersAndFields(cl,annotClass.getName());   
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
                	throw new DuplicationInjectionPointException(
                			name,cl,annotClass.getName());
                }

                A annot = ao.getAnnotation(annotClass);
                InjectionPoint<A> ip =
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
            throw new NoSuchInjectionPointException(name,cl,annotClass.getName());
        }
    }
    
    /**
     * Register the injection point associated with the specified field.
     * 
     * @param field  the field
     * @param annot  the annotation associated with the field
     */
    public void put( Field field, A annot )
    throws DuplicationInjectionPointException {
    	
    	String name = field.getName();
    	
        /*
         * Check whether the name has already been encountered before. If so,
         * this means that more than one injection point exists, which is
         * inconsistent.
         */
        if( containsKey(name) ) {
        	throw new DuplicationInjectionPointException(
    			name,cl,annotClass.getName());
        }

    	InjectionPoint<A> ip = new InjectionPointFieldImpl<>(field,annot);
        put(name,ip);
    }
    
    /**
     * Register the injection point associated with the specified setter.
     * 
     * @param setter  the setter
     * @param annot   the annotation associated with the setter
     */
    public void put( Method setter, A annot )
    throws DuplicationInjectionPointException {
    	
    	SetterMethodFilter.checkSetterMethod(setter);
    	String name = SetterMethodFilter.getSetterPropertyName(setter);
    	
        /*
         * Check whether the name has already been encountered before. If so,
         * this means that more than one injection point exists, which is
         * inconsistent.
         */
        if( containsKey(name) ) {
        	throw new DuplicationInjectionPointException(
    			name,cl,annotClass.getName());
        }

    	InjectionPoint<A> ip = new InjectionPointMethodImpl<>(setter,annot);
        put(name,ip);
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
    	A annot = ao.getAnnotation(annotClass);
    	String name = Util.getAnnotationParamValue(annot,"name");
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
