package com.dynatrace.monitors.license.usage.rest.responses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*

<agentgroupreference name="Business Backend Server (Java)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Business%20Backend%20Server%20(Java)"/>

 */
@XmlRootElement(name = "agentgroupreference")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlAgentGroupRef {

	private String name = null;
	private String href = null;
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setHref(String href) {
		this.href = href;
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
		XmlAgentGroupRef other = (XmlAgentGroupRef) obj;
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
