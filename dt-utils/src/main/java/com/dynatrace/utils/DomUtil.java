package com.dynatrace.utils;

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
	
	private static final String EXM_NO_DYNATRACE = "%s is not a System Profile (no dynatrace element found)";
	private static final String EXM_NO_VERSION = "%s is not a System Profile (no version attribute found)";
	private static final String EXM_INV_VERSION = "%s is not a System Profile (version attribute invalid)";
	
	private static final ErrorHandler ERROR_HANDLER = new DomUtil();
	
	public static Document build(byte[] bytes) throws IOException {
		try (InputStream in = Strings.openStream(bytes)) {
			return build(in);
		}
	}
	
	public static Document build(String s) throws IOException {
		try (InputStream in = Strings.openStream(s)) {
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
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(ERROR_HANDLER);
			return db.parse(in);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
	}
	
	public static Version extractVersion(Document doc, String fName) throws IOException {
		NodeList dtElems = doc.getElementsByTagName("dynatrace");
		if (dtElems.getLength() == 0) {
			throw new IOException(String.format(EXM_NO_DYNATRACE, fName));
		}
		Element dtElem = (Element) dtElems.item(0);
		String vAtt = dtElem.getAttribute("version");
		if (vAtt == null) {
			throw new IOException(String.format(EXM_NO_VERSION, fName));
		}
		try {
			return Version.parse(vAtt);
		} catch (IllegalArgumentException e) {
			throw new IOException(String.format(EXM_INV_VERSION, fName), e);
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
