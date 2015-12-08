package com.dynatrace.fixpacks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = XmlFixPack.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlFixPack {
	
	public static final String TAG = "fixpack";
	
	private String href = null;
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}

}
