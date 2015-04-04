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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.reflect.AnnotatedElementFilter;
import commons.reflect.Filter;
import commons.reflect.Filters;
import commons.reflect.SetterMethodFilter;
import commons.reflect.UnAnnotatedElementFilter;

/**
 * Utility methods.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @since 2.6
 */
public class ClassHelper {

    /**
     * Return all fields declared by the specified class and all fields declared
     * by parent classes, except {@link java.lang.Object}, of the specified
     * class. All fields are included whatever their access modifier is.
     * 
     * @param cl  the class
     */
    public static Field[] getAllFields( Class<?> cl ) {
    	List<Field> result = new ArrayList<Field>();
    	addAllFields(cl,result);
    	return result.toArray(new Field[result.size()]);
    }
    
    /**
     * Add to the specified list, all fields declared by the specified class and
     * all fields declared by parent classes, except {@link java.lang.Object},
     * of the specified class. All fields are included whatever their access
     * modifier is.
     * 
     * @param cl    the class
     * @param list  the list where fields are to be added
     */
    public static void addAllFields( Class<?> cl, List<Field> list ) {
    	
    	// Stop when java.lang.Object is reached
    	if( cl.equals(Object.class) ) {
    		return;
    	}

    	// Declared fields
    	Field[] fields = cl.getDeclaredFields();
    	for (Field field : fields) {
			list.add(field);
		}
    	
    	// Recurse in the parent class
    	Class<?> supercl = cl.getSuperclass();
    	addAllFields(supercl,list);
    }
    
    /**
     * Return all methods declared by the specified class and all methods
     * declared by parent classes, except {@link java.lang.Object}, of the
     * specified class. All methods are included whatever their access modifier
     * is.
     * 
     * @param cl  the class
     */
    public static Method[] getAllMethods( Class<?> cl ) {
    	List<Method> result = new ArrayList<Method>();
    	addAllMethods(cl,result);
    	return result.toArray(new Method[result.size()]);
    }
    
    /**
     * Add to the specified list, all methods declared by the specified class
     * and all methods declared by parent classes, except {@link
     * java.lang.Object}, of the specified class. All methods are included
     * whatever their access modifier is.
     * 
     * @param cl    the class
     * @param list  the list where methods are to be added
     */
    public static void addAllMethods( Class<?> cl, List<Method> list ) {
    	
    	// Stop when java.lang.Object is reached
    	if( cl.equals(Object.class) ) {
    		return;
    	}

    	// Declared methods
    	Method[] methods = cl.getDeclaredMethods();
    	for (Method method : methods) {
			list.add(method);
		}
    	
    	// Recurse in the parent class
    	Class<?> supercl = cl.getSuperclass();
		addAllMethods(supercl,list);
    }
    
    /**
     * Return all setter methods and fields associated with an annotation whose
     * type name is one of those contained in <code>annotClassNames</code>. The
     * matching elements declared in <code>cl</code>, whatever their access
     * modifier is, and the public inherited ones are returned.
     * 
     * @param cl               the class
     * @param annotClassNames  the annotation class names
     */
    public static AccessibleObject[] getAllAnnotatedSettersAndFields(
		Class<?> cl, String... annotClassNames ) {
    	
    	// Setter methods
    	Method[] methods = getAllMethods(cl);
    	Method[] ms0 = Filters.filter(methods,new SetterMethodFilter());
    	Filter<AnnotatedElement> filter =
    		new AnnotatedElementFilter(annotClassNames);
    	Method[] ms1 = Filters.filter(ms0,filter);
    	
    	// Fields
    	Field[] fields = getAllFields(cl);
    	Field[] fs0 = Filters.filter(fields,filter);

    	// Result
    	AccessibleObject[] aos =
    		new AccessibleObject[ ms1.length + fs0.length ];
    	System.arraycopy(ms1,0,aos,0,ms1.length);
    	System.arraycopy(fs0,0,aos,ms1.length,fs0.length);
    	
    	return aos;
    }
    
    /**
     * Return all public declared and inherited setter methods and fields
     * associated with an annotation whose type name is one of those contained
     * in <code>annotClassNames</code>.
     * 
     * @param cl               the class
     * @param annotClassNames  the annotation class names
     */
    public static AccessibleObject[] getAnnotatedSettersAndFields(
		Class<?> cl, String... annotClassNames ) {
    	
    	// Setter methods
    	Method[] methods = cl.getMethods();
    	Method[] ms0 = Filters.filter(methods,new SetterMethodFilter());
    	Filter<AnnotatedElement> filter =
    		new AnnotatedElementFilter(annotClassNames);
    	Method[] ms1 = Filters.filter(ms0,filter);
    	
    	// Fields
    	Field[] fields = cl.getFields();
    	Field[] fs0 = Filters.filter(fields,filter);

    	// Result
    	AccessibleObject[] aos =
    		new AccessibleObject[ ms1.length + fs0.length ];
    	System.arraycopy(ms1,0,aos,0,ms1.length);
    	System.arraycopy(fs0,0,aos,ms1.length,fs0.length);
    	
    	return aos;
    }
    
    /**
     * Return the annotation associated with <code>cl</code> whose type name is
     * one of those contained in <code>annotClassNames</code>. If no such
     * annotation exists, recurse into the inheritance hierarchy up to {@link
     * java.lang.Object}. Return <code>null</code> if no such annotation is
     * found.
     */
    public static Annotation getAnnotation(
		Class<?> cl, String... annotClassNames ) {

    	// No annotation on Object.class
    	if( cl.equals(Object.class) ) {
    		return null;
    	}

    	Annotation[] annots = cl.getAnnotations();
    	for (String annotClassName : annotClassNames) {
    		for (Annotation annot : annots) {
				String name = annot.annotationType().getName();
				if( name.equals(annotClassName) ) {
					return annot;
				}
			}
		}
    	
    	// Recurse in the parent class up to java.lang.Object
    	Class<?> supercl = cl.getSuperclass();
    	if( supercl!=null && !supercl.equals(Object.class) ) {
    		Annotation annot = getAnnotation(supercl,annotClassNames);
    		return annot;
    	}
    	
    	// No such annotation found
    	return null;
    }
        
    /**
     * Return all unannotated methods, declared ones whatever their access
     * modifier is and inherited public ones, in the specified class.
     * 
     * @param cl  the class
     * @return    the corresponding map of methods indexed by property names
     *            (the property set by the method)
     */
    public static Map<String,Method> getAllUnAnnotatedSetterMethods(
		Class<?> cl ) {
        
    	Method[] methods = getAllMethods(cl);
    	Method[] ms0 = Filters.filter(methods,new SetterMethodFilter());
    	Filter<AnnotatedElement> filter = new UnAnnotatedElementFilter();
    	Method[] ms1 = Filters.filter(ms0,filter);
        
    	// Create the resulting map
        Map<String,Method> result = new HashMap<String,Method>();
        for (Method setter : ms1) {
			String propName = SetterMethodFilter.getSetterPropertyName(setter);
			result.put(propName,setter);
		}

        return result;
    }

    /**
     * Return <code>true</code> if an annotation whose type name is one of those
     * contained in <code>annotClassNames</code> is associated with
     * <code>cl</code>. If no such annotation exists, recurse into the
     * inheritance hierarchy up to {@link java.lang.Object}. Return
     * <code>false</code> if no such annotation is found.
     */
    public static boolean isAnnotationPresent(
		Class<?> cl, String... annotClassNames ) {
    	
    	Annotation annot = getAnnotation(cl,annotClassNames);
    	return annot != null;
    }
}
