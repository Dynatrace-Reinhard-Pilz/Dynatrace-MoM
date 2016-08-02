package com.dynatrace.classloader;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class EmbeddedClassLoaderTest {

	@Test
	public void testEmbeddedClassLoader() throws Exception {
		try (EmbeddedClassLoader cl = new EmbeddedClassLoader(
			new URL[0],
			EmbeddedClassLoaderTest.class.getClassLoader())
		) {
			Assert.assertEquals(0, cl.getURLs().length);
		}
	}
}
