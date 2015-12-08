package com.dynatrace.license;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "agent")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class LicensedAgent {
	
	public static final int NONE = -1;
	
	private String name = null;
	private int count = NONE;
	
	public final String getName() {
		return name;
	}
	
	@XmlAttribute(name = "name")
	public final void setName(final String name) {
		this.name = name;
	}
	
	public final void setCount(final int count) {
		this.count = count;
	}
	
	@XmlValue
	public final int getCount() {
		return count;
	}

}
