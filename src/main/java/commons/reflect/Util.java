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

package commons.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Util {

    /**
     * Check whether the specified method is a getter.
     * 
     * @param getter  the method to be checked
     * @throws IllegalArgumentException
     *      if <code>getter</code> is not a getter method
     */
    public static void checkGetterMethod( Method getter )
    throws IllegalArgumentException {        

        String name = getter.getName();
        if( ! name.startsWith("get") ) {
            String msg =
                "The name of a getter method should start with get: "+getter;
            throw new IllegalArgumentException(msg);
        }
        Class<?>[] ptypes = getter.getParameterTypes();
        if( ptypes.length != 0 ) {
            String msg =
                "A getter method should not define any parameter: "+getter;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check whether the specified methods are a valid pair of setter/getter
     * methods for the same property.
     * 
     * @throws IllegalArgumentException  if this is not the case
     */
    public static void checkMatchingSetterGetterMethods(
		Method setter, Method getter )
    throws IllegalArgumentException {
    	
    	SetterMethodFilter.checkSetterMethod(setter);
    	checkGetterMethod(getter);
    	
    	String setterPropName = SetterMethodFilter.getSetterPropertyName(setter);
    	String getterPropName = getGetterPropertyName(getter);
    	if( ! setterPropName.equals(getterPropName) ) {
    		String msg =
    			"Property names differ: "+setterPropName+" vs "+getterPropName;
    		throw new IllegalArgumentException(msg);
    	}
    	
    	Class<?> setterPropType = SetterMethodFilter.getSetterPropertyType(setter);
    	Class<?> getterPropType = getGetterPropertyType(getter);
    	if( ! setterPropType.equals(getterPropType) ) {
    		String msg =
    			"Property types differ: "+setterPropType+" vs "+getterPropType;
    		throw new IllegalArgumentException(msg);
    	}
    }
    
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
     * Return the annotation associated with <code>ao</code> whose type name is
     * one of those contained in <code>annotClassNames</code>. Return
     * <code>null</code> if no such annotation is found.
     */
    public static Annotation getAnnotation(
		AccessibleObject ao, String... annotClassNames ) {
		
    	Annotation[] annots = ao.getAnnotations();
    	for (String annotClassName : annotClassNames) {
    		for (Annotation annot : annots) {
    			String name = annot.annotationType().getName();
        		if( name.equals(annotClassName) ) {
        			return annot;
        		}
			}
		}
    	return null;
    }

    /**
     * Return the value of the parameter associated with the specified
     * annotation.
     * 
     * @param annot  the annotation
     * @param name   the parameter name
     * @return
     * 		the parameter value or <code>null</code> if the annotation is
     * 		<code>null</code> or if the annotation does not define the specified
     * 		parameter name 
     */
    public static <T> T getAnnotationParamValue(
		Annotation annot, String name ) {
    	
    	if( annot == null ) {
    		return null;
    	}
    	
    	Class<?> annotcl = annot.getClass();
		try {
			Method meth = annotcl.getMethod(name);
	    	@SuppressWarnings("unchecked")
			T value = (T) meth.invoke(annot);
	    	return value;
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
    }
    
    /**
     * Return the getter method corresponding to the specified setter.
     * 
     * @throws NoSuchMethodException  if the getter does not exist
     */
    public static Method getGetterForSetter( Method setter )
    throws NoSuchMethodException {
    	
    	Class<?> cl = setter.getDeclaringClass();
    	
    	// First search in declared methods
    	Method[] methods = cl.getDeclaredMethods();
    	for (Method method : methods) {
			if( isMatchingSetterGetter(setter,method) ) {
				return method;
			}
		}

    	// First search in declared methods
    	methods = cl.getMethods();
    	for (Method method : methods) {
			if( isMatchingSetterGetter(setter,method) ) {
				return method;
			}
		}
    	
    	throw new NoSuchMethodException();
    }
    
    /**
     * Return the name of the property set by the specified getter method.
     * 
     * @param getter  a getter method
     * @return        the name of the property set by <code>getter</code>
     * @throws IllegalArgumentException
     *      if <code>getter</code> is not a getter method
     */
    public static String getGetterPropertyName( Method getter )
    throws IllegalArgumentException {
        
        checkGetterMethod(getter);
        
        String name = getter.getName();
        name = name.substring(3);   // Skip get
        name = name.substring(0,1).toLowerCase() + name.substring(1);
        
        return name;
    }
    
    /**
     * Return the type of the property set by the specified getter method.
     * 
     * @param getter  a setter method
     * @return        the type of the property set by <code>getter</code>
     * @throws IllegalArgumentException
     *      if <code>getter</code> is not a getter method
     */
    public static Class<?> getGetterPropertyType( Method getter )
    throws IllegalArgumentException {
        
        checkGetterMethod(getter);
        
        Class<?> rtype = getter.getReturnType();
        return rtype;
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
    
    /**
     * Return <code>true</code> if the specified method is a getter.
     * 
     * @param getter  the method to be checked
     * @return  <code>true</code> if this is a getter
     */
    public static boolean isGetterMethod( Method getter ) {
        try {
            checkGetterMethod(getter);
            return true;
        }
        catch( IllegalArgumentException iae ) {
            return false;
        }
    }
    
    /**
     * Return <code>true</code> if the specified pair of methods are valid
     * setter/getter for the same property.
     */
    public static boolean isMatchingSetterGetter(
		Method setter, Method getter ) {
    	
    	try {
    		checkMatchingSetterGetterMethods(setter,getter);
    		return true;
    	}
    	catch( IllegalArgumentException iae ) {
    		return false;
    	}
    }

    /**
     * Load the class whose name is specified with the specified classloader.
     * 
     * @throws ClassNotFoundException  if the class can not be loaded
     */
    public static Class<?> loadClass( String name, ClassLoader cl )
    throws ClassNotFoundException {
        Class<?> c = cl.loadClass(name);
        return c;
    }

    /**
     * Strip modifiers, return type, class name and throw clause from the
     * signature of the specified method.
     * 
     * For a method signature of the form
     * 	 <code>public void org.ow2.frascati.tinfi.reflect.UtilTestCase$Target.init() throws java.lang.RuntimeException</code>
     * this method returns
     *   <code>init()</code>
     */
    public static String getShortSignature( Method src ) {
    	String signature = src.toString();
    	int parenthesis = signature.indexOf('(');
    	String s = signature.substring(0,parenthesis);
    	int methoddot = s.lastIndexOf('.');
    	String ret = signature.substring(methoddot+1);
    	int thr = ret.indexOf(" throws");
    	if( thr != -1 ) {
    		ret = ret.substring(0,thr);
    	}
    	return ret;
    }

    /**
     * Return <code>true</code> if the target and source methods share the same
     * signature.
     * 
     * @param src     the source method
     * @param target  the target method
     */
    public static boolean sameSignature( Method src, Method target ) {
    	String srcsig = getShortSignature(src);
    	String targetsig = getShortSignature(target);
    	boolean b = srcsig.equals(targetsig);
    	return b;
    }
    
    /**
     * Return <code>true</code> if the source field overrides the target field.
     * 
     * @param src     the source field
     * @param target  the target field
     */
    public static boolean override( Field src, Field target ) {
    	
    	String srcname = src.getName();
    	String targetname = target.getName();
    	boolean b = srcname.equals(targetname);
    	if(!b) {
    		return false;
    	}
    	
    	Class<?> srccl = src.getDeclaringClass();
    	Class<?> targetcl = target.getDeclaringClass();
    	
    	if( targetcl.isAssignableFrom(srccl) ) {
    		/*
    		 * If targetcl is assignable from srccl, this means that src (the
    		 * declaring class of) is a subtype of target (idem.) Then, since
    		 * target and src share the same name, src overrides target.
    		 */
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Return <code>true</code> if the source method overrides the target
     * method.
     * 
     * @param src     the source method
     * @param target  the target method
     */
    public static boolean override( Method src, Method target ) {
    	
    	boolean b = sameSignature(src,target);
    	if(!b) {
    		return false;
    	}
    	
    	Class<?> srccl = src.getDeclaringClass();
    	Class<?> targetcl = target.getDeclaringClass();
    	
    	if( targetcl.isAssignableFrom(srccl) ) {
    		/*
    		 * If targetcl is assignable from srccl, this means that src (the
    		 * declaring class of) is a subtype of target (idem.) Then, since
    		 * target and src share the same signature, src overrides target.
    		 */
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Return a copy of the specified array where overridden methods have been
     * removed.
     */
    public static Method[] removeOverridden( Method[] methods ) {
    	List<Method> res = new ArrayList<Method>();
    	res.addAll(Arrays.asList(methods));
    	for (int i = 0; i < methods.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				if(j==i) { continue; }
				boolean b = override(methods[j],methods[i]);
				if(b) {
					res.remove(methods[i]);
				}
			}
		}
    	return res.toArray(new Method[res.size()]);
    }

    /**
     * Return a copy of the specified array where overridden methods and fields
     * have been removed.
     */
    public static AccessibleObject[] removeOverridden( AccessibleObject[] aos ) {
    	List<AccessibleObject> res = new ArrayList<AccessibleObject>();
    	res.addAll(Arrays.asList(aos));
    	for (int i = 0; i < aos.length; i++) {
			for (int j = 0; j < aos.length; j++) {
				if(j==i) { continue; }
				boolean b = false;
				if( aos[i] instanceof Method && aos[j] instanceof Method ) {
					b = override((Method)aos[j],(Method)aos[i]);
				}
				else if( aos[i] instanceof Field && aos[j] instanceof Field ) {
					b = override((Field)aos[j],(Field)aos[i]);
				}
				if(b) {
					res.remove(aos[i]);
				}
			}
		}
    	return res.toArray(new AccessibleObject[res.size()]);
    }
}
