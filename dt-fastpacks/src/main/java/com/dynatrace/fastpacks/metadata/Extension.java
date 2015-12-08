package com.dynatrace.fastpacks.metadata;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.fastpacks.metadata.resources.Resource;

@XmlRootElement(name = "extension")
@XmlAccessorType(XmlAccessType.FIELD)
public class Extension {
	
	private static final String EXTENSION_POINT =
			"com.dynatrace.diagnostics.InstallerContent";
	
	@XmlAttribute(name = "id")
	private String id = null;
	
	@XmlAttribute(name = "point")
	private String point = EXTENSION_POINT;
	
	@XmlElementRef(type = MetaInfo.class)
	private MetaInfo metaInfo = new MetaInfo();
	
	public String getId() {
		return id;
	}
	
	public String getPoint() {
		return point;
	}
	
	public MetaInfo getMetaInfo() {
		return metaInfo;
	}
	
	public String getName() {
		return metaInfo.getName();
	}
	
	public InstallerType getInstallerType() {
		return metaInfo.getInstallerType();
	}
	
	public Collection<Resource> getResources() {
		return metaInfo.getResources();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.metaInfo.setName(name);
	}
	
}
