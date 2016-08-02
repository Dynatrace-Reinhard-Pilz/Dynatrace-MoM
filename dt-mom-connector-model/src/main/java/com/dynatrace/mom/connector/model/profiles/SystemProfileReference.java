package com.dynatrace.mom.connector.model.profiles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReference;

@XmlRootElement(name = SystemProfileReference.XML_ELEMENT_PROFILE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class SystemProfileReference extends AbstractFileReference {
	
	public static final String XML_ELEMENT_PROFILE	= "profile";

	public SystemProfileReference() {
		
	}
	
	public SystemProfileReference(String id, String href) {
		setId(id);
		setHref(href);
	}
	
}
