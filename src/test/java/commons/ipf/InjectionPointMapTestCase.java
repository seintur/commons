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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for testing the functionalities of the
 * {@link CompositeInjectionPointHashMap} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class InjectionPointMapTestCase {

    private Field targetcpt, srccpt1, targetcpt2;
    private Method targetref, srcref1, targetref2;

    @Before
    public void setUp() throws NoSuchMethodException, NoSuchFieldException {
        
        targetcpt = Target.class.getDeclaredField("cpt");
        srccpt1 = Src.class.getDeclaredField("cpt1");
        targetcpt2 = Target.class.getDeclaredField("cpt2");
        
        targetref = Target.class.getMethod("setRef",String.class);
        srcref1 = Src.class.getMethod("setRef1",String.class);
        targetref2 = Target.class.getMethod("setRef2",String.class);
    }
    
    @Test
    public void putAll() throws DuplicationInjectionPointException {
        
        CompositeInjectionPointHashMap ipm =
            new CompositeInjectionPointHashMap(Target.class,Reference.class.getName());
        ipm.putAll();
        
        InjectionPoint<?> ip = ipm.get("cpt");
        InjectionPointFieldImpl<?> ipf = (InjectionPointFieldImpl<?>) ip;
        Field f = ipf.getField();
        Assert.assertEquals(targetcpt,f);
        
        ip = ipm.get("cpt1");
        ipf = (InjectionPointFieldImpl<?>) ip;
        f = ipf.getField();
        Assert.assertEquals(srccpt1,f);
        
        ip = ipm.get("cpt2");
        ipf = (InjectionPointFieldImpl<?>) ip;
        f = ipf.getField();
        Assert.assertEquals(targetcpt2,f);
        
        ip = ipm.get("ref");
        InjectionPointMethodImpl<?> ipmeth =
            (InjectionPointMethodImpl<?>) ip;
        Method m = ipmeth.getSetterMethod();
        Assert.assertEquals(targetref,m);
        
        ip = ipm.get("ref1");
        ipmeth = (InjectionPointMethodImpl<?>) ip;
        m = ipmeth.getSetterMethod();
        Assert.assertEquals(srcref1,m);
        
        ip = ipm.get("ref2");
        ipmeth = (InjectionPointMethodImpl<?>) ip;
        m = ipmeth.getSetterMethod();
        Assert.assertEquals(targetref2,m);
    }

    @Test(expected=DuplicationInjectionPointException.class)
    public void putAllDuplicate() throws DuplicationInjectionPointException {
        
        CompositeInjectionPointHashMap ipm =
            new CompositeInjectionPointHashMap(
                Duplicate.class, Reference.class.getName() );
        ipm.putAll();
    }
    
    @Test
    public void put()
    throws NoSuchInjectionPointException, DuplicationInjectionPointException {
        
        CompositeInjectionPointHashMap ipm =
            new CompositeInjectionPointHashMap(
                Target.class, Reference.class.getName() );
        
        ipm.put("cpt2");
        InjectionPoint<?> ip = ipm.get("cpt2");
        InjectionPointFieldImpl<?> ipf = (InjectionPointFieldImpl<?>) ip;
        Field f = ipf.getField();
        Assert.assertEquals(targetcpt2,f);
    }
    
    @Test(expected=NoSuchInjectionPointException.class)
    public void putNoSuch()
    throws DuplicationInjectionPointException, NoSuchInjectionPointException {
        
        CompositeInjectionPointHashMap ipm =
            new CompositeInjectionPointHashMap(
                Target.class, Reference.class.getName() );
        ipm.put("foo");            
    }
    
    @Test(expected=DuplicationInjectionPointException.class)
    public void putDuplicate()
    throws NoSuchInjectionPointException, DuplicationInjectionPointException {
        
        CompositeInjectionPointHashMap ipm =
            new CompositeInjectionPointHashMap(
                Duplicate.class, Reference.class.getName() );
        ipm.put("ref");
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Reference {}
    
    class Src {
        @Reference
        protected int cpt;
        @Reference
        protected int cpt1;
        @Reference
        public void setRef( String value ) {}
        @Reference
        public void setRef1( String value ) {}
    }
    
    class Target extends Src {
        @Reference
        protected int cpt;
        @Reference
        protected int cpt2;
        @Override
        @Reference
        public void setRef( String value ) {}
        @Reference
        public void setRef2( String value ) {}
    }
    
    class Duplicate {
        @Reference
        private String ref;
        @Reference
        public void setRef( String value ) {}
    }
}
