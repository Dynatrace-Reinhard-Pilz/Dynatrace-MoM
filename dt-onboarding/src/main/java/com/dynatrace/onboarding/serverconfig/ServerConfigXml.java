package com.dynatrace.onboarding.serverconfig;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.client.ConnectorClient;
import com.dynatrace.utils.Version;

public class ServerConfigXml {
	
	private static final Logger LOGGER =
			Logger.getLogger(ServerConfigXml.class.getName());
	
	public static final ServerConfigXml VOID = new ServerConfigXml();

	private final String contents;
	private Version permissionTaskVersion = Version.UNDEFINED;
	
	private ServerConfigXml() {
		this.contents = null;
	}
	
	private ServerConfigXml(String contents) {
		this.contents = contents;
		foo();
	}
	
	public static ServerConfigXml get(ServerConfig serverConfig) throws IOException {
		ConnectorClient client = new ConnectorClient(serverConfig);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.getServerConfig(out);
			return new ServerConfigXml(new String(out.toByteArray()));
		}
	}
	
	public Version getPermissionTaskVersion() {
		return permissionTaskVersion;
	}
	
	public void foo() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse collector.config.xml", e);
			return;
		}
		DefaultHandler handler = new DefaultHandler() {
			
			@Override
			public void startElement(String uri, String localName,	String qName, Attributes attributes) throws SAXException {
				if ("plugintypeconfig".equals(qName)) {
					String name = attributes.getValue("name");
					if ("Permission User Group Task".equals(name)) {
						permissionTaskVersion = Version.parse(attributes.getValue("bundleversion"));
					}
				}
			}
			
		};
		try (InputStream xmlIn = new ByteArrayInputStream(contents.getBytes())) {
			saxParser.parse(new BufferedInputStream(xmlIn) {
				@Override
				public void close() throws IOException {
				}
			}, handler);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse server.config.xml", e);
		}		
	}
}
