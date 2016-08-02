package com.dynatrace.mom.connector.model.dashboards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReference;

@XmlRootElement(name = DashboardReference.XML_ELEMENT_DASHBOARD)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class DashboardReference extends AbstractFileReference {
	
	public static final String XML_ELEMENT_DASHBOARD = "dashboard";
	private static final String XML_ATTRIBUTE_NAME	= "name";
	private static final String XML_ATTRIBUTE_KEY	= "key";
	
	private String name = null;
	private String key = null;

	public DashboardReference() {
		
	}
	
	public DashboardReference(String id, String name, String href) {
		setId(id);
		setHref(href);
	}
	
	@XmlAttribute(name = DashboardReference.XML_ATTRIBUTE_NAME)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = DashboardReference.XML_ATTRIBUTE_KEY)
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
}
