package com.dynatrace.sysinfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dynatrace.utils.Version;

public class ComponentProperties {
	
	private static final Logger LOGGER =
			Logger.getLogger(ComponentProperties.class.getName());
	
	private final ComponentHashKey key;
	private final Properties properties = new Properties();
	private String name = null;
	private Version version = Version.UNDEFINED;
	
	public ComponentProperties(ComponentHashKey key) {
		Objects.requireNonNull(key);
		this.key = key;
	}
	
	public long getStartupTime() {
		String sTime = properties.getProperty("eclipse.startTime");
		if (sTime == null) {
			return 0L;
		}
		try {
			return Long.parseLong(sTime);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Invalid Collector Startup Time " + sTime, e);
			return 0L;
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setVersion(Version version) {
		if (version == null) {
			return;
		}
//		LOGGER.log(Level.INFO, "Setting Version of " + this.toString() + " to " + version);
		this.version = version;
	}
	
	public void tryVersion(Version version) {
		if (version == null) {
			return;
		}
		if (this.version == null) {
			this.version = version;
		}
		if (this.version.compareTo(version) < 0) {
			setVersion(version);
		}
	}
	
	public Version getVersion() {
		return version;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public ComponentType getType() {
		return key.getType();
	}
	
	public String getHost() {
		return key.getHost();
	}
	
	public String getPid() {
		return key.getPid();
	}
	
	@Override
	public String toString() {
		return key.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentProperties other = (ComponentProperties) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	public static void handleVersionPropertyToken(ComponentProperties componentProperties, String token) {
		if (token == null) {
			return;
		}
		if (!token.endsWith(".jar")) {
			return;
		}
		String jarLessToken = token.substring(0, token.length() - ".jar".length());
		int idx = jarLessToken.lastIndexOf("_");
		if (idx == -1) {
			return;
		}
		String sVerion = jarLessToken.substring(idx + 1);
		Version version = Version.parse(sVerion, false);
		componentProperties.tryVersion(version);
	}
	
	public static void handleVersionProperty(ComponentProperties componentProperties, String propertyName) {
		String propertyValue = componentProperties.getProperties().getProperty(propertyName);
		if (propertyValue == null) {
			return;
		}
		StringTokenizer strTok = new StringTokenizer(propertyValue, ",");
		while (strTok.hasMoreTokens()) {
			ComponentProperties.handleVersionPropertyToken(componentProperties, strTok.nextToken().trim());
		}
	}
	
	public static void handleComponentProperties(ComponentProperties componentProperties, ZipEntry zipEntry, ZipInputStream zis) {
		try {
			componentProperties.getProperties().load(zis);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to resolve component.properties into Properties object");
		}
		ComponentProperties.handleVersionProperty(componentProperties, "osgi.frameworkClassPath");
		ComponentProperties.handleVersionProperty(componentProperties, "osgi.bundles");
	}
	
	public static void handleServerConfigXml(final ComponentProperties componentProperties, InputStream in) {
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
				if ("server".equals(qName)) {
					String fqdn = attributes.getValue("fqdn");
					if (fqdn != null) {
						fqdn = attributes.getValue("name");
					}
					if (fqdn != null) {
						componentProperties.setName(fqdn);
					}
				}
			}
			
		};
		try {
			saxParser.parse(new BufferedInputStream(in) {
				@Override
				public void close() throws IOException {
				}
			}, handler);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse collector.config.xml", e);
		}
	}
	
	
	public static void handleCollectorConfigXml(final ComponentProperties componentProperties, InputStream in) {
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
				if ("dynatrace".equals(qName)) {
					componentProperties.tryVersion(
						Version.parse(
							attributes.getValue("version"), false
						)
					);
				} else if ("collectorconfig".equals(qName)) {
					componentProperties.setName(attributes.getValue("name"));
				}
			}
			
		};
		try {
			saxParser.parse(new BufferedInputStream(in) {
				@Override
				public void close() throws IOException {
				}
			}, handler);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse collector.config.xml", e);
		}
	}
	
	public static ComponentProperties resolveProperties(
		Map<ComponentHashKey, ComponentProperties> props,
		String entryName
	) {
		if (entryName == null) {
			return null;
		}
		ComponentType type = ComponentType.unknown;
		StringTokenizer strTok = new StringTokenizer(entryName, "/");
		// resolve component type
		if (strTok.hasMoreTokens()) {
			type = ComponentType.fromString(strTok.nextToken());
		}
		if (type == ComponentType.unknown) {
			LOGGER.log(Level.FINE, "ComponentType unknown for " + entryName);
			return null;
		}
		// resolve host
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String host = strTok.nextToken();
		// resolve unknown entry
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		strTok.nextToken();
		// resolve pid
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String pid = strTok.nextToken();
		ComponentHashKey key = new ComponentHashKey(type, host, pid);
		ComponentProperties componentProps = props.get(key);
		if (componentProps == null) {
			componentProps = new ComponentProperties(key);
			props.put(key, componentProps);
		}
		return componentProps;
	}
	
}