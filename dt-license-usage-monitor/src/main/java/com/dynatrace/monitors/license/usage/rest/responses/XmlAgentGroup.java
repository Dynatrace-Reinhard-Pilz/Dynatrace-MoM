package com.dynatrace.monitors.license.usage.rest.responses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "agentgroup")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlAgentGroup {

	private String name = null;
	private String description = null;
	private XmlAgentMappingsRef agentMappingsRef = null;

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	
	@XmlElementRef(type = XmlAgentMappingsRef.class)
	public XmlAgentMappingsRef getAgentMappingsRef() {
		return agentMappingsRef;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setAgentMappingsRef(XmlAgentMappingsRef agentMappingsRef) {
		this.agentMappingsRef = agentMappingsRef;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		XmlAgentGroup other = (XmlAgentGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
