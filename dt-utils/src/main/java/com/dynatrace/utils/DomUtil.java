package com.dynatrace.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DomUtil implements ErrorHandler {
	
	private static final ErrorHandler ERROR_HANDLER = new DomUtil();
	
	public static Document build(byte[] bytes) throws IOException {
		try (InputStream in = new ByteArrayInputStream(bytes)) {
			return build(in);
		}
	}
	
	public static Document build(String s) throws IOException {
		try (InputStream in = new ByteArrayInputStream(s.getBytes())) {
			return build(in);
		}
	}

	public static Document build(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			return build(in);
		}
	}
	
	public static Document build(InputStream in) throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.setErrorHandler(ERROR_HANDLER);
			return dBuilder.parse(in);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
	}
	
	public static Version extractVersion(Document document, String fileName) throws IOException {
		NodeList dynatraceElements = document.getElementsByTagName("dynatrace");
		if (dynatraceElements.getLength() == 0) {
			throw new IOException(fileName + " is not a System Profile (no dynatrace element found)");
		}
		Element element = (Element) dynatraceElements.item(0);
		String sVersion = element.getAttribute("version");
		if (sVersion == null) {
			throw new IOException(fileName + " is not a System Profile (no version attribute found)");
		}
		try {
			return Version.parse(sVersion);
		} catch (IllegalArgumentException e) {
			throw new IOException(fileName + " is not a System Profile (version attribute invalid)", e);
		}
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		// ingore
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		// ingore
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		// ingore
	}
	
}
