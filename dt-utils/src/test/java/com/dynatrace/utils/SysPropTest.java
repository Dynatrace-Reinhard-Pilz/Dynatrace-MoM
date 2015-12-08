package com.dynatrace.utils;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for class {@link SysProp}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class SysPropTest {
	
	@Test
	public void testConstructor() {
		try (SysProp sp = new SysProp(SysPropTest.class.getName())) {
			
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNull() {
		try (SysProp sp = new SysProp(null)) {
			
		}
	}
	
	@Test
	public void testSet() {
		String key = SysPropTest.class.getName();
		try {
			String value = SysProp.class.getName();
			Assert.assertNull(System.getProperty(key));
			SysProp sp = new SysProp(key);
			sp.set(value);
			Assert.assertEquals(value, System.getProperty(key));
			sp.restore();
			Assert.assertNull(System.getProperty(key));
			sp.close();
			
			String exValue = UUID.randomUUID().toString();
			System.setProperty(key, exValue);
			Assert.assertEquals(exValue, System.getProperty(key));
			sp = new SysProp(key);
			sp.set(value);
			Assert.assertEquals(value, System.getProperty(key));
			sp.restore();
			sp.close();
			Assert.assertEquals(exValue, System.getProperty(key));
			
			sp.set(null);
			Assert.assertNull(System.getProperty(key));
			sp.restore();
			sp.close();
			Assert.assertEquals(exValue, System.getProperty(key));
		} finally {
			System.clearProperty(key);
		}
	}
	
	@Test
	public void testAutoCloseable() {
		String key = SysPropTest.class.getName();
		Assert.assertNull(System.getProperty(key));
		
		String value = SysProp.class.getName();
		System.setProperty(key, value);
		Assert.assertEquals(value, System.getProperty(key));
		try (
			SysProp sp = new SysProp(key);
		) {
			String newValue = UUID.randomUUID().toString();
			System.setProperty(key, newValue);
			Assert.assertEquals(newValue, System.getProperty(key));
		}
		Assert.assertEquals(value, System.getProperty(key));
	}
	
}
