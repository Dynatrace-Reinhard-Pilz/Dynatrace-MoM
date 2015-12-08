package com.dynatrace.fixpacks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dt:fix")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Fix {

	@XmlAttribute(name = "branch")
	private String branch = null;
	@XmlAttribute(name = "id")
	private String id = null;
	@XmlElement(name = "dt:short_description")
	private String description = null;
	
	public final String getBranch() {
		return branch;
	}
	
	public final void setBranch(final String branch) {
		this.branch = branch;
	}
	
	public final String getId() {
		return id;
	}
	
	public final void setId(final String id) {
		this.id = id;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public final void setDescription(final String description) {
		this.description = description;
	}
}
