package com.dynatrace.xml.mock;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class MockMarshaller implements Marshaller {
	
	private final HashMap<String, Object> properties =
			new HashMap<String, Object>();

	@Override
	public void setProperty(String name, Object value)
		throws PropertyException
	{
		properties.put(name, value);
	}

	@Override
	public Object getProperty(String name) throws PropertyException {
		return properties.get(name);
	}

	@Override
	public void marshal(Object jaxbElement, Result result)
			throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, OutputStream os)
			throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, File output) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, Writer writer)
		throws JAXBException
	{
	}

	@Override
	public void marshal(Object jaxbElement, ContentHandler handler)
			throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, Node node) throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, XMLStreamWriter writer)
			throws JAXBException {
	}

	@Override
	public void marshal(Object jaxbElement, XMLEventWriter writer)
			throws JAXBException {
	}

	@Override
	public Node getNode(Object contentTree) throws JAXBException {
		return null;
	}

	@Override
	public void setEventHandler(ValidationEventHandler handler)
			throws JAXBException {
	}

	@Override
	public ValidationEventHandler getEventHandler() throws JAXBException {
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setAdapter(XmlAdapter adapter) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <A extends XmlAdapter> A getAdapter(Class<A> type) {
		return null;
	}

	@Override
	public void setAttachmentMarshaller(AttachmentMarshaller am) {
	}

	@Override
	public AttachmentMarshaller getAttachmentMarshaller() {
		return null;
	}

	@Override
	public void setSchema(Schema schema) {
	}

	@Override
	public Schema getSchema() {
		return null;
	}

	@Override
	public void setListener(Listener listener) {
	}

	@Override
	public Listener getListener() {
		return null;
	}

}
