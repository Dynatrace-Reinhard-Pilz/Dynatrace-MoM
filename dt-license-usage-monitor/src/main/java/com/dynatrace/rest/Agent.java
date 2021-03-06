package com.dynatrace.rest;

import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Agent {

	private final Element element;
	
	public Agent(Element element) {
		Objects.requireNonNull(element);
		this.element = element;
	}
	
	private String getElementValue(String tagName) {
		return getElementValue(element, tagName);
	}
	
	private String getElementValue(Element parent, String tagName) {
		Objects.requireNonNull(parent);
		Objects.requireNonNull(tagName);
		NodeList elements = parent.getElementsByTagName(tagName);
		if (elements == null) {
			return null;
		}
		if (elements.getLength() == 0) {
			return null;
		}
		int len = elements.getLength();
		for (int i = 0; i < len; i++) {
			Element element = (Element) elements.item(i);
			if (element.getParentNode() != parent) {
				continue;
			}
			Node firstChild = element.getFirstChild();
			if (firstChild == null) {
				return null;
			}
			if (firstChild.getNodeType() != Node.TEXT_NODE) {
				return null;
			}
			return firstChild.getNodeValue();
		}
		return "unknown";
	}
	
	private int getIntElementValue(String tagName) {
		String value = getElementValue(tagName);
		if (value == null) {
			return 0;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private boolean getBooleanElementValue(String tagName) {
		String value = getElementValue(tagName);
		if (value == null) {
			return false;
		}
		return value.trim().toLowerCase().equals("true");
	}
	
	public String getInstanceName() {
		return getElementValue("agentInstanceName");
	}
	
//	public boolean isMasterAgent() {
//		if (!"Web Server".equals(getTechnologyType())) {
//			return false;
//		}
//		String instanceName = getInstanceName();
//		if (instanceName == null) {
//			return false;
//		}
//		return !instanceName.contains("[");
//	}
	
	public String getAgentGroup() {
		return getElementValue("agentGroup");
	}
	
	public String getName() {
		return getElementValue("name");
	}
	
	public String getSystemProfile() {
		return getElementValue("systemProfile");
	}
	
	public String getTechnologyType() {
		return getElementValue("technologyType");
	}
	
	public String getCollector() {
		NodeList collectorInformationElements =
				element.getElementsByTagName("collectorinformation");
		if (collectorInformationElements == null) {
			return null;
		}
		if (collectorInformationElements.getLength() == 0) {
			return null;
		}
		Element collectorInformationElement =
				(Element) collectorInformationElements.item(0);
		String name = getElementValue(collectorInformationElement, "name");
		if (name == null) {
			return null;
		}
		String host = getElementValue(collectorInformationElement, "host");
		if (host == null) {
			return null;
		}
		return name + "@" + host;
	}
	
	public String getOperatingSystem() {
		NodeList agentPropertiesElements =
				element.getElementsByTagName("agentProperties");
		if (agentPropertiesElements == null) {
			return null;
		}
		if (agentPropertiesElements.getLength() == 0) {
			return null;
		}
		Element agentPropertiesElement =
				(Element) agentPropertiesElements.item(0);
		return getElementValue(agentPropertiesElement, "operatingSystem");
	}
	
	public String getVersion() {
		NodeList agentPropertiesElements =
				element.getElementsByTagName("agentProperties");
		if (agentPropertiesElements == null) {
			return null;
		}
		if (agentPropertiesElements.getLength() == 0) {
			return null;
		}
		Element agentPropertiesElement =
				(Element) agentPropertiesElements.item(0);
		return getElementValue(agentPropertiesElement, "agentVersion");
	}
	
	public String getHost() {
		return getElementValue("host");
	}
	
	public int getAgentId() {
		return getIntElementValue("agentId");
	}
	
	public boolean isConnected() {
		return getBooleanElementValue("connected");
	}
	
	public boolean isLicenseOk() {
		return getElementValue("licenseInformation") != null && getBooleanElementValue("licenseOk");
	}
}
