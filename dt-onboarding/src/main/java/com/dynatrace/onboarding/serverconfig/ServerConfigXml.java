package com.dynatrace.onboarding.serverconfig;

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
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;

public class ServerConfigXml extends DefaultHandler {
	
	private static final String EXMSG_CANNOT_PARSE =
			"Unable to parse collector.config.xml";
	
	private static final String NAME_PERM_TASK = "Permission User Group Task";
	
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
		init();
	}
	
	/**
	 * 
	 * @param srvConf
	 * @return
	 * @throws IOException
	 */
	public static ServerConfigXml get(ServerConfig srvConf) throws IOException {
		ConnectorClient client = new ConnectorClient(srvConf);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.getServerConfig(out);
			return new ServerConfigXml(new String(out.toByteArray()));
		}
	}
	
	public Version getPermissionTaskVersion() {
		return permissionTaskVersion;
	}
	
	public void init() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		try {
			sp = spf.newSAXParser();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, EXMSG_CANNOT_PARSE, e);
			return;
		}
		try (InputStream in = Strings.openBuffered(contents)) {
			sp.parse(in, this);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, EXMSG_CANNOT_PARSE, e);
		}		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes
	) throws SAXException {
		if ("plugintypeconfig".equals(qName)) {
			String name = attributes.getValue("name");
			if (NAME_PERM_TASK.equals(name)) {
				permissionTaskVersion =
						Version.parse(attributes.getValue("bundleversion"));
			}
		}
	}
	
}
