package com.dynatrace.utils;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.Coverage;

/**
 * Tests for class {@link Unchecked}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class UncheckedTest extends Coverage<Unchecked> {

	@Test
	public void testCast() {
		Assert.assertEquals(this, Unchecked.cast(this));
	}
	
	@Test(expected = ClassCastException.class)
	public void testFailedCast() {
		String s = Unchecked.cast(this);
		System.out.println(s);
	}

	@Override
	protected Class<Unchecked> getCoverageClass() {
		return Unchecked.class;
	}
	
}
