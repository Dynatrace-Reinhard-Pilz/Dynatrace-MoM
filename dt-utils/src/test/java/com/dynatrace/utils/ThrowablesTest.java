package com.dynatrace.utils;


import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.Coverage;

/**
 * Tests for class {@link Throwables}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class ThrowablesTest extends Coverage<Throwables> {

	@Test
	public void testToString() {
		Assert.assertEquals(Strings.EMPTY, Throwables.toString(null));
		Assert.assertTrue(Throwables.toString(new NullPointerException()).contains(NullPointerException.class.getName()));
		Throwable t = new NullPointerException() {
			private static final long serialVersionUID = 1L;

			public void printStackTrace(PrintWriter s) {
				s.println(ThrowablesTest.class.getName());
				throw new RuntimeException();
			};
		};
		Assert.assertEquals(RuntimeException.class.getName(), Throwables.toString(t));
	}

	@Override
	protected Class<Throwables> getCoverageClass() {
		return Throwables.class;
	}
}
