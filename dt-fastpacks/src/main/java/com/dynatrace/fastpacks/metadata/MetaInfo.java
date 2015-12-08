package com.dynatrace.fastpacks.metadata;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.fastpacks.metadata.resources.Resource;

@XmlRootElement(name = MetaInfo.XML_ELEMENT_METAINFO)
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaInfo {
	
	public static final String XML_ELEMENT_METAINFO = "metainfo";
	public static final String XML_ATTRIBUTE_NAME = "name";
	public static final String XML_ATTRIBUTE_INSTALLER_TYPE = "installer_type";
	
	@XmlAttribute(name = MetaInfo.XML_ATTRIBUTE_NAME)
	private String name = null;
	
	@XmlAttribute(name = MetaInfo.XML_ATTRIBUTE_INSTALLER_TYPE)
	private InstallerType installerType = InstallerType.resourcepack;
	
	@XmlElementRef(type = Resource.class)
	private Collection<Resource> resources = new ArrayList<Resource>(0);
	
	
	public MetaInfo() {
	}
	
	public MetaInfo(String name) {
		this.name = name;
	}
	
	public InstallerType getInstallerType() {
		return installerType;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<Resource> getResources() {
		return resources;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
}

