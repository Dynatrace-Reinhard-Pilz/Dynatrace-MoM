package com.dynatrace.monitors.license.usage.rest.responses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*

<systemprofile isrecording="false" id="easyTravel" href="http://localhost:8020/rest/management/profiles/easyTravel"/>

 */
@XmlRootElement(name = "systemprofile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlProfileRef {

	private String id = null;
	private boolean isRecording = false;
	private String href = null;
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name = "isrecording")
	public boolean isRecording() {
		return isRecording;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
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
		XmlProfileRef other = (XmlProfileRef) obj;
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
