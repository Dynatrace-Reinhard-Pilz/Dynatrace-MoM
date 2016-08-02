package com.dynatrace.xml;



import static javax.xml.bind.JAXBContext.JAXB_CONTEXT_FACTORY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.Coverage;
import com.dynatrace.utils.SysProp;
import com.dynatrace.utils.Unchecked;
import com.dynatrace.xml.classes.InvalidXmlPojo;
import com.dynatrace.xml.classes.NonXmlPojo;
import com.dynatrace.xml.classes.Pojo;
import com.dynatrace.xml.mock.MockJAXBContext;
import com.dynatrace.xml.mock.MockMarshaller;

/**
 * Tests for class {@link XMLUtil}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class XMLUtilTest {
	
	@Test
	public void testCast() {
		Assert.assertNull(Unchecked.cast(null));
		String s = XMLUtil.class.getName();
		Assert.assertEquals(s, Unchecked.cast(s));
	}
	
//	@Test
//	public void testSerialize() throws IOException {
//		Pojo pojo = new Pojo();
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		XMLUtil.serialize(pojo, out);
//		String serialized = new String(out.toByteArray());
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		pw.println(
//			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//		);
//		pw.println("<pojo>");
//		pw.println("    <field>" + pojo.getField() + "</field>");
//		pw.println("</pojo>");
//		pw.close();
//		System.out.println(serialized);
//		String expected = sw.getBuffer().toString().replace("\r\n", "\n");
//		System.out.println(expected);
//		Assert.assertEquals(expected, serialized);
//		System.out.println(serialized);
//	}
	
	@Test(expected = IOException.class)
	public void testSerializeNonXmlPojo() throws IOException {
		NonXmlPojo pojo = new NonXmlPojo();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLUtil.serialize(pojo, out);
	}

	@Test(expected = IOException.class)
	public void testSerializeNonXmlPojo2() throws IOException {
		try (
			SysProp sp = new SysProp(JAXB_CONTEXT_FACTORY);
		) {
			sp.set(XMLUtilTest.class.getName());
			InvalidXmlPojo pojo = new InvalidXmlPojo(null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLUtil.serialize(pojo, out);
		}
	}
	
	@Test
	public void testDeserialize() throws IOException {
		String uuid = UUID.randomUUID().toString();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		);
		pw.println("<pojo>");
		pw.println("    <field>" + uuid + "</field>");
		pw.println("</pojo>");
		pw.close();
		String serialized = sw.getBuffer().toString().replace("\r\n", "\n");
		ByteArrayInputStream in = new ByteArrayInputStream(serialized.getBytes());
		Pojo pojo = XMLUtil.deserialize(in, Pojo.class);
		Assert.assertEquals(uuid, pojo.getField());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateContextNull() throws IOException {
		XMLUtil.createContext((Class<?>[])null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateContextEmpty() throws IOException {
		XMLUtil.createContext(new Class<?>[0]);
	}
	
	@Test(expected = IOException.class)
	public void testCreateContextErronous() throws IOException {
		try (SysProp sp = new SysProp(JAXB_CONTEXT_FACTORY)) {
			sp.set(UUID.randomUUID().toString());
			XMLUtil.createContext(Pojo.class);
		}
	}
	
	@Test
	public void testCreateContext() throws IOException {
		Assert.assertNotNull(XMLUtil.createContext(Pojo.class));
	}
	
	@Test(expected = IOException.class)
	public void testCreateMarshallerErronous() throws IOException {
		JAXBContext ctx = new MockJAXBContext();
		XMLUtil.createMarshaller(ctx, null, true, false);
	}
	
	@Test
	public void testCreateMarshaller() throws IOException, PropertyException {
		Marshaller marshaller = new MockMarshaller();
		JAXBContext ctx = new MockJAXBContext(marshaller);
		Marshaller m = XMLUtil.createMarshaller(ctx, null, true, false);
		Assert.assertEquals(marshaller, m);
		
		Assert.assertEquals(
			Boolean.TRUE,
			marshaller.getProperty(Marshaller.JAXB_FORMATTED_OUTPUT)
		);
		Assert.assertEquals(
			Boolean.FALSE,
			marshaller.getProperty(Marshaller.JAXB_FRAGMENT)
		);
		Assert.assertEquals(
			Charset.defaultCharset().name(),
			marshaller.getProperty(Marshaller.JAXB_ENCODING)
		);

		marshaller = new MockMarshaller();
		ctx = new MockJAXBContext(marshaller);
		m = XMLUtil.createMarshaller(ctx, true, false);
		Assert.assertEquals(marshaller, m);
		
		Assert.assertEquals(
			Boolean.TRUE,
			marshaller.getProperty(Marshaller.JAXB_FORMATTED_OUTPUT)
		);
		Assert.assertEquals(
			Boolean.FALSE,
			marshaller.getProperty(Marshaller.JAXB_FRAGMENT)
		);
		Assert.assertEquals(
			Charset.defaultCharset().name(),
			marshaller.getProperty(Marshaller.JAXB_ENCODING)
		);
		
	}

	@Test
	public void testConstructor() throws IOException {
		XMLUtil util = new XMLUtil(Pojo.class);
		Assert.assertNotNull(util);
		Assert.assertNotNull(Coverage.getField(util, "ctx"));
	}
	
//	@Test
//	public void testToString() {
//		Pojo pojo = new Pojo();
//		String serialized = XMLUtil.toString(pojo);
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		pw.println(
//			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//		);
//		pw.println("<pojo>");
//		pw.println("    <field>" + pojo.getField() + "</field>");
//		pw.println("</pojo>");
//		pw.close();
//		System.out.println(serialized);
//		String expected = sw.getBuffer().toString().replace("\r\n", "\n");
//		System.out.println(expected);
//		Assert.assertEquals(expected, serialized);
//		System.out.println(serialized);
//	}
	
	@Test
	public void testToStringWithException() {
		try (SysProp sp = new SysProp(JAXB_CONTEXT_FACTORY)) {
			sp.set(UUID.randomUUID().toString());
			Assert.assertTrue(
				XMLUtil.toString(new Pojo())
					.contains(IOException.class.getName())
			);
		}
	}
	
	@Test(expected = IOException.class)
	public void testFailedDeserialize() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
		XMLUtil.deserialize(in, Pojo.class);
	}
	
	@Test
	public void testSerializeWithContext() throws IOException {
		Charset charset = Charset.defaultCharset();
		JAXBContext ctx = XMLUtil.createContext(Pojo.class);
		Pojo o = new Pojo();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLUtil.serialize(o, out, charset, ctx, true, false);
		XMLUtil.serialize(o, out, charset, ctx);
	}
	
	@Test
	public void testToString() {
		Pojo o = new Pojo();
		String s = XMLUtil.toString(o);
		System.out.println(s);
	}
	
	private static class Ref<T> {
		private T value = null;
		
		public void set(T value) {
			this.value = value;
		}
		
		public T get() {
			return value;
		}
	}
	
	@Test
	public void testDeserializeInterrupted() throws IOException {
		final ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		final Ref<Object> result = new Ref<Object>();
		Thread t = new Thread() {
			
			@Override
			public void run() {
				try {
					Object o = XMLUtil.deserialize(in, XMLUtilTest.class);
					result.set(o);
				} catch (IOException e) {
					result.set(e);
				}
			}
		};
		t.start();
		t.interrupt();
		try {
			t.join();
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNull(result.get());
		
	}
}
