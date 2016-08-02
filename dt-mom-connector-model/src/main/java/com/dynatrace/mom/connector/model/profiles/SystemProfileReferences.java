package com.dynatrace.mom.connector.model.profiles;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReferences;

@XmlRootElement(name = SystemProfileReferences.XML_ELEMENT_PROFILES)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class SystemProfileReferences extends AbstractFileReferences<SystemProfileReference> {
	
	public static final String XML_ELEMENT_PROFILES	= "profiles";
	
	public SystemProfileReferences() {
	}
	
	public SystemProfileReferences(String href) {
		setHref(href);
	}
	
	@XmlElementRef(type = SystemProfileReference.class)
	@Override
	public Collection<SystemProfileReference> getFiles() {
		return super.getFiles();
	}
	
	@Override
	public void setFiles(Collection<SystemProfileReference> files) {
		super.setFiles(files);
	}
	
}
