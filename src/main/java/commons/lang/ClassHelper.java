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
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import commons.lang.reflect.AnnotatedElementFilter;
import commons.lang.reflect.SetterMethodFilter;
import commons.lang.reflect.UnAnnotatedElementFilter;
import commons.util.function.Filters;

/**
 * This class provides helper methods for the {@link Class} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class ClassHelper {

	private final static Map<Class<?>,String> nulvalues =
		new HashMap<Class<?>,String>() {
		    private static final long serialVersionUID = -8428019352784119543L;
		{
		    put(boolean.class,"false");
		    put(char.class,"' '");
		    put(byte.class,"0");
		    put(short.class,"0");
		    put(int.class,"0");
		    put(long.class,"0");
		    put(float.class,"0.0f");
		    put(double.class,"0.0");
		}};

	private final static Map<Class<?>,Class<?>> boxed =
		new HashMap<Class<?>,Class<?>>() {
		    private static final long serialVersionUID = 2398856843237335831L;
		{
		    put(boolean.class,Boolean.class);
		    put(char.class,Character.class);
		    put(byte.class,Byte.class);
		    put(short.class,Short.class);
		    put(int.class,Integer.class);
		    put(long.class,Long.class);
		    put(float.class,Float.class);
		    put(double.class,Double.class);
		    put(void.class,Void.class);
		}};

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
	 * If the specified class corresponds to a primitive type, return its
	 * corresponding boxed class. Else return the specified class.
	 */
	public static Class<?> box( Class<?> cl ) {
	    if( cl.isPrimitive() ) {
	    	Class<?> b = boxed.get(cl);
	        return b;
	    }
	    else {
	        return cl;
	    }
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
    	Predicate<AnnotatedElement> filter =
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
    	Predicate<AnnotatedElement> filter = new UnAnnotatedElementFilter();
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
    	Predicate<AnnotatedElement> filter =
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
     * Return the annotation of type <code>annotationClass</code> associated
     * with the specified class. If no such annotation exists, recurse into the
     * inheritance hierarchy up to {@link java.lang.Object}. Return
     * <code>null</code> if no such annotation is found.
     */
    public static <A extends Annotation> A getAnnotation(
		Class<?> cl, Class<A> annotationClass ) {

    	// No annotation on Object.class
    	if( cl.equals(Object.class) ) {
    		return null;
    	}

    	A annot = cl.getAnnotation(annotationClass);
    	if( annot != null ) {
    		return annot;
    	}
    	
    	Class<?> supercl = cl.getSuperclass();
    	if( supercl!=null && !supercl.equals(Object.class) ) {
    		annot = getAnnotation(supercl,annotationClass);
    		return annot;
    	}

    	// No such annotation found
    	return null;
    }    

	/**
	 * Return all public declared and inherited methods for the specified class,
	 * provided that, when a method is redefined, only the one with the most
	 * specialized return type is returned.
	 */
	public static Method[] getMethodsWithMostSpecializedReturnType( Class<?> cl ) {
	
		/*
		 * Map of methods to return indexed by their name.
		 */
		Map<String,Collection<Method>> methodsByName =
			new HashMap<String,Collection<Method>>();		
	
		Method[] methods = cl.getMethods();
		for(Method method : methods) {
	
			String methodName = method.getName();
			
			boolean methodAlreadyExists = methodsByName.containsKey(methodName);
			if(!methodAlreadyExists) {
				// Register a new set of method for this name
				Set<Method> set = new HashSet<Method>();
				set.add(method);
				methodsByName.put(methodName,set);
				continue;
			}
	
			Collection<Method> _methods = methodsByName.get(methodName);
			for( Method _method : _methods ) {
				
				/*
				 * method and _method share the same name.
				 * Check whether their signature is the same.
				 */
				
				Class<?>[] parameters = method.getParameterTypes();
				Class<?>[] _parameters = _method.getParameterTypes();
				
				methodAlreadyExists = (parameters.length==_parameters.length);
				if(methodAlreadyExists) {					
					for(int i=0; i<parameters.length; i++) {						
						if(!parameters[i].equals(_parameters[i])){ 
							methodAlreadyExists = false;
							break;
						}
					}
					
					/*
					 * method and _method share the same name and signature.
					 * Check whether the return type of method is more
					 * specialized than the one of _method and in this case,
					 * retain method and discard _method.
					 */
					if(methodAlreadyExists) {
						Class<?> returnType = method.getReturnType();
						Class<?> _returnType = _method.getReturnType();
						if(_returnType.isAssignableFrom(returnType)) {
							_methods.remove(_method);
							_methods.add(method);
						}
						else if(returnType.isAssignableFrom(_returnType)) {
							break;
						}
					}
				}
			}
	
			/*
			 * At least two different methods with the same name are defined
			 * (declared or inherited) in unifiedClass but their signatures
			 * differ. Register method in the methodsByName map.
			 */
			if(!methodAlreadyExists) {
				_methods.add(method);
			}
		}
		
		// Get all the methods stored in methodsByName
		Collection<Method> ret = new HashSet<Method>();
		for(Collection<Method> _methods : methodsByName.values()) {
			ret.addAll(_methods);
		}
		
		return ret.toArray(new Method[ret.size()]);		
	}

    /**
     * <p>
     * Return the name of the specified class as it appears in the source code.
     * </p>
     * 
     * <p>
     * This method is a replacement for {@link Class#getName()} that, for array
     * types, return something like <code>Ljava.lang.Object;</code> instead of
     * <code>Object[]</code>, and for inner types, return something like
     * <code>Outter$Inner</code> instead of <code>Outter.Inner</code>.
     * </p>
     */
    public static String getName( Class<?> cl ) {
        
        // Arrays
        if( cl.isArray() ) {
            Class<?> element = cl.getComponentType();
            return getName(element)+"[]";
        }
        
        // Inner types
        String name = cl.getName();
        if( name.indexOf('$') != -1 ) {
            name = name.replace('$','.');
        }
        
        return name;
    }

	/**
	 * Return the string representation of the null value associated to the
	 * specified class.
	 */
	public static String getNullValue( Class<?> cl ) {
	    if( nulvalues.containsKey(cl) ) {
	        String ret = nulvalues.get(cl);
	        return ret;
	    }
	    return "null";
	}

	/**
	 * Return the signature of the generic parameter names. Return a string of
	 * the form <code>&lt;P1,P2,...&gt;</code>.
	 */
	public static String getTypeParameterNamesSignature( String[] tpnames ) {
	    
	    if( tpnames.length == 0 ) {
	        return "";
	    }
	
	    StringBuffer sb = new StringBuffer();
	    sb.append('<');
	    boolean first = true;
	    for (String tpname : tpnames) {
	        if(first) {first=false;} else {sb.append(',');}
	        sb.append(tpname);
	    }
	    sb.append('>');
	    return sb.toString();
	}

	/**
	 * Return the string representations of the type variable names declared by
	 * the specified class. This method omits upper bounds. Upper bounds are
	 * elements such as <code>extends FooBar</code> in a generic declaration of
	 * the form <code>&lt;T extends FooBar&gt;</code>.
	 * 
	 * @since 2.1.1
	 */
	public static String[] getTypeParameterNames( Class<?> cl ) {
	    TypeVariable<?>[] tvs = cl.getTypeParameters();
	    String[] rets = new String[tvs.length];
	    for (int i = 0; i < tvs.length; i++) {
	        rets[i] = tvs[i].toString();
	    }
	    return rets;
	}

	/**
	 * Return the delimiter for values of the specified class.
	 */
	public static String getValueDelimiter( Class<?> cl ) {
		if( cl.equals(String.class) ) {
			return "\"";
		}
		if( cl.equals(char.class) || cl.equals(Character.class) ) {
			return "'";
		}
		return "";
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
