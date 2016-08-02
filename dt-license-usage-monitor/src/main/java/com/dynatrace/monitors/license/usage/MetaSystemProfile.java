package com.dynatrace.monitors.license.usage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.profiles.metainfo.Metaable;

/*
<systemprofile
	isrecording="<true>|false>"
	id="<profilename>"
	href="http(s)://<server>:<port>/rest/management/profiles/<profilename>"
/>
*/
@XmlRootElement(name = "systemprofile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class MetaSystemProfile implements Metaable {
	
	private String id;
	private String description;
	private boolean isRecording = false;
	private String href = null;
	
	public MetaSystemProfile() {
		
	}
	
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
	public MetaInfo getMetaInfo() {
		return MetaInfo.parse(description);
	}

	@Override
	public String getMetaInfo(String key) {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		MetaSystemProfile other = (MetaSystemProfile) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
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
