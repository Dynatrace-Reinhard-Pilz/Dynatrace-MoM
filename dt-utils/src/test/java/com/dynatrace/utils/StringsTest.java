package com.dynatrace.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.Coverage;

/**
 * Tests for class {@link Strings}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class StringsTest extends Coverage<Strings> {

	@Override
	protected Class<Strings> getCoverageClass() {
		return Strings.class;
	}
	
	@Test
	public void testEnsureNotNull() {
		Assert.assertEquals("", Strings.ensureNotNull(null));
		String arg = UUID.randomUUID().toString();
		Assert.assertTrue(arg == Strings.ensureNotNull(arg));
	}
	
	@Test
	public void testIsNullOrEmpty() {
		Assert.assertTrue(Strings.isNullOrEmpty(null));
		Assert.assertTrue(Strings.isNullOrEmpty(""));
		Assert.assertFalse(Strings.isNullOrEmpty(UUID.randomUUID().toString()));
	}
	
	@Test
	public void testIsNotEmpty() {
		Assert.assertFalse(Strings.isNotEmpty(null));
		Assert.assertFalse(Strings.isNotEmpty(""));
		Assert.assertTrue(Strings.isNotEmpty(UUID.randomUUID().toString()));
	}
	
	@Test
	public void testRemoveLeadingSlash() {
		Assert.assertNull(Strings.removeLeadingSlash(null));
		String s = UUID.randomUUID().toString();
		Assert.assertTrue(s == Strings.removeLeadingSlash(s));
		Assert.assertEquals(s, Strings.removeLeadingSlash("/" + s));
		Assert.assertEquals("/" + s, Strings.removeLeadingSlash("//" + s));
	}
	
	@Test
	public void testEquals() {
		Assert.assertTrue(Strings.equals(null, null));
		Assert.assertFalse(Strings.equals(UUID.randomUUID(), null));
		Assert.assertFalse(Strings.equals(null, UUID.randomUUID()));
		Assert.assertFalse(
			Strings.equals(UUID.randomUUID(), UUID.randomUUID())
		);
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = new UUID(
			uuid1.getMostSignificantBits(),
			uuid1.getLeastSignificantBits()
		);
		Assert.assertTrue(Strings.equals(uuid1, uuid1));
		Assert.assertTrue(Strings.equals(uuid1, uuid2));
	}
	
	@Test
	public void testJoin() {
		Assert.assertEquals(0, Strings.join(null).length());
		Assert.assertEquals(0, Strings.join("").length());
		Assert.assertEquals(
			0,
			Strings.join("", (Object[]) null).length()
		);
		Assert.assertEquals(0, Strings.join(",").length());
		Assert.assertEquals(
			0,
			Strings.join(null, new Object[] { null }).length()
		);
		Assert.assertEquals(0, Strings.join(",", new Object[0]).length());
		Assert.assertEquals(
			0,
			Strings.join(",", new Object[] { null }).length()
		);
		Assert.assertEquals(0, Strings.join(",", new Object[] { "" }).length());
		Assert.assertEquals("x", Strings.join(",", new Object[] { "x" }));
		Assert.assertEquals("x", Strings.join(",", new Object[] { "x", null }));
		Assert.assertEquals(
			"x",
			Strings.join(",", new Object[] { null, "x", null })
		);
	}
	
	private String urlEncode(String s, String enc)
		throws InvocationTargetException
	{
		return (String) invoke(
			Strings.class,
			"urlEncode",
			sig(String.class, String.class),
			s,
			enc
		);
	}
	
	@Test(expected = InternalError.class)
	public void testUrlEncodeWithInvalidEncoding() {
		try {
			urlEncode("", "x");
		} catch (InvocationTargetException e) {
			throwRuntime(e);
		}
	}
	
	@Test
	public void testUrlEncode() {
		try {
			Assert.assertEquals(
				Strings.EMPTY,
				urlEncode(null, UUID.randomUUID().toString()
			));
			Assert.assertEquals(
				Strings.EMPTY,
				urlEncode(Strings.EMPTY, Strings.UTF8)
			);
			Assert.assertEquals(
				Strings.PLUS,
				urlEncode(Strings.SPC, Strings.UTF8)
			);
			String s = UUID.randomUUID().toString();
			Assert.assertEquals(s, urlEncode(s, Strings.UTF8));
		} catch (InvocationTargetException e) {
			throwRuntime(e);
			Assert.fail();
		}
	}

	@Test
	public void testEncode() {
		Assert.assertEquals(Strings.EMPTY, Strings.encode(null));
		Assert.assertEquals(Strings.EMPTY, Strings.encode(Strings.EMPTY));
		Assert.assertEquals(Strings.ENCSPC, Strings.encode(Strings.SPC));
		String s = UUID.randomUUID().toString();
		Assert.assertEquals(s, Strings.encode(s));
	}
	
	@Test
	public void testToString() {
		Assert.assertNull(Strings.toString(null));
		Assert.assertEquals(
			StringsTest.class.getSimpleName(),
			Strings.toString(this)
		);
		Assert.assertEquals(
			StringsTest.class.getSimpleName(),
			Strings.toString(this, (Object[]) null)
		);
		Assert.assertEquals(
			StringsTest.class.getSimpleName(),
			Strings.toString(this, new Object[0])
		);
		Assert.assertEquals(
			StringsTest.class.getSimpleName() + "[a, b]",
			Strings.toString(this, "a", null, "b")
		);
		Assert.assertEquals(
			StringsTest.class.getSimpleName(),
			Strings.toString(this, null, null, null)
		);
	}
	
	@Test
	public void testContains() {
		Assert.assertFalse(Strings.contains(null, null));
		Assert.assertFalse(Strings.contains(
			UUID.randomUUID().toString(),
			null
		));
		Assert.assertFalse(Strings.contains(
			null,
			UUID.randomUUID().toString()
		));
		Assert.assertFalse(Strings.contains(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		));
		Assert.assertTrue(Strings.contains("AB", "A"));
	}
	
	@Test
	public void testUrlDecode() {
		Assert.assertNull(Strings.urlDecode(null));
		String randStr = randStr();
		Assert.assertEquals(randStr, Strings.urlDecode(randStr));
	}
	
	@Test
	public void testUrlDecodeNull() throws Throwable {
		Method mUrlDecode = Strings.class.getDeclaredMethod(
			"urlDecode",
			new Class<?>[] { String.class, String.class}
		);
		mUrlDecode.setAccessible(true);
		Assert.assertNull(
			mUrlDecode.invoke((Object) null, new Object[] { null, null })
		);
	}
	
	@Test(expected = InternalError.class)
	public void testUrlDecodeWithIllegalCharSet() throws Throwable {
		Method mUrlDecode = Strings.class.getDeclaredMethod(
			"urlDecode",
			new Class<?>[] { String.class, String.class}
		);
		mUrlDecode.setAccessible(true);
		try {
			mUrlDecode.invoke((Object) null, randStr(), Strings.EMPTY);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
	
	public static String randStr() {
		return UUID.randomUUID().toString();
	}
	
	
}
