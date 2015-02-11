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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import commons.reflect.Util;

/**
 * Class for testing the functionalities of the {@link Util} class.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 * @since 1.3.1
 */
public class UtilTestCase {

	private Field srccontext, targetcontext, targetctx;
	private Method srcinit, srcnot, targetinit, targetnot;

	@Before
	public void setUp() throws NoSuchMethodException, NoSuchFieldException {
		
		srccontext = Src.class.getDeclaredField("context");
		targetcontext = Target.class.getDeclaredField("context");
		targetctx = Target.class.getDeclaredField("ctx");
		
		srcinit = Src.class.getMethod("init");
		srcnot = Src.class.getMethod("not",String.class,UtilTestCase.class);
		targetinit = Target.class.getMethod("init");
		targetnot = Target.class.getMethod("not",String.class);		
	}
	
	@Test
	public void getShortSignature() {
		Assert.assertEquals("init()",Util.getShortSignature(srcinit));
		Assert.assertEquals("init()",Util.getShortSignature(targetinit));
	}
	
	@Test
	public void sameSignature() {
		Assert.assertEquals(true,Util.sameSignature(srcinit,targetinit));
	}
	
	@Test
	public void overrideField() {		
		Assert.assertEquals(true,Util.override(targetcontext,srccontext));
		Assert.assertEquals(false,Util.override(srccontext,targetcontext));
		Assert.assertEquals(false,Util.override(targetctx,srccontext));
	}
	
	@Test
	public void overrideMethod() {		
		Assert.assertEquals(true,Util.override(targetinit,srcinit));
		Assert.assertEquals(false,Util.override(srcinit,targetinit));
		Assert.assertEquals(false,Util.override(targetnot,srcnot));
	}
	
	@Test
	public void removeOverridenMethods() {
		Method[] methods = new Method[]{srcinit,targetinit,srcnot,targetnot};
		Method[] actuals = Util.removeOverridden(methods);
		Method[] expecteds = new Method[]{targetinit,srcnot,targetnot};
		Assert.assertArrayEquals(expecteds,actuals);
	}
	
	@Test
	public void removeOverridenAccessibleObjects() {
		AccessibleObject[] aos =
			new AccessibleObject[]{
				srccontext,targetcontext,targetctx,srcinit,targetinit,srcnot,
				targetnot};
		AccessibleObject[] actuals = Util.removeOverridden(aos);
		AccessibleObject[] expecteds =
			new AccessibleObject[]{
				targetcontext,targetctx,targetinit,srcnot,targetnot};
		Assert.assertArrayEquals(expecteds,actuals);
	}

	@SuppressWarnings("unused")
	private static class Src {
		protected String context;
		public void init() throws RuntimeException {}
		public boolean not( String s, UtilTestCase utc ) { return false; }
	}
	
	@SuppressWarnings("unused")
	private static class Target extends Src {
		protected Object context;
		protected String ctx;
		@Override
		public void init() {}
		public boolean not( String s ) { return false; }
	}
}
