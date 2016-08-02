package com.dynatrace.monitors.license.usage.rest.responses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*

<action id="configurations" href="http://localhost:8020/rest/management/profiles/easyTravel/configurations"/>

 */
@XmlRootElement(name = "action")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlAction {

	private String id = null;
	private String href = null;
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		XmlAction other = (XmlAction) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
