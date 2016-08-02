package com.dynatrace.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;


public class ExtractTest {

	@Test
	public void testExtract() throws Exception {
//		try (InputStream in = getInputStream("<dynatrace version=\"6.3.0.1275\">")) {
//			Assert.assertEquals("6.3.0.1275", Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
//		try (InputStream in = getInputStream("asdad<dynatrace version=\"6.3.0.1275\">")) {
//			Assert.assertEquals("6.3.0.1275", Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
//		try (InputStream in = getInputStream("asdad<dynatrace version=\"6.3.0.1275\">jklkjhkj")) {
//			Assert.assertEquals("6.3.0.1275", Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
//		try (InputStream in = getInputStream("<dynatrace version=\"6.3.0.1275\">iuytiy")) {
//			Assert.assertEquals("6.3.0.1275", Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
//		try (InputStream in = getInputStream("<dynatrace version=\"\">iuytiy")) {
//			Assert.assertEquals("", Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
//		try (InputStream in = getInputStream("dynatrace version=\"\">iuytiy")) {
//			Assert.assertEquals(null, Extract.extract(in, "<dynatrace version=\"", "\"")); 
//		}
		try (InputStream in = getInputStream("<dynatrace version=\"6.3.0.1275\" date=\"4/19/16 11:34 AM\">iuytiy")) {
			Assert.assertEquals("6.3.0.1275", Extract.extract(in, "<dynatrace version=\"", "\"")); 
		}
		
		 
	}
	
	private static InputStream getInputStream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
