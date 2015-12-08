package com.dynatrace.fixpacks;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fixpacks")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlFixPackList {

	private Collection<XmlFixPack> fixPacks = null;
	private String href = null;

	@XmlElementRef(name = XmlFixPack.TAG, type = XmlFixPack.class)
	public Collection<XmlFixPack> getFixPacks() {
		return this.fixPacks;
	}
	
	public void setFixPacks(Collection<XmlFixPack> fixPacks) {
		this.fixPacks = fixPacks;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
}
