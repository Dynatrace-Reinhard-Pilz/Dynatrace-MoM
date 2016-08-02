package com.dynatrace.mom.connector.model.profiletemplates;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReferences;

@XmlRootElement(
	name = ProfileTemplateReferences.XML_ELEMENT_PROFILE_TEMPLATES
)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ProfileTemplateReferences extends AbstractFileReferences<ProfileTemplateReference> {
	
	public static final String XML_ELEMENT_PROFILE_TEMPLATES
		= "profiletemplates";
	
	public ProfileTemplateReferences() {
	}
	
	public ProfileTemplateReferences(String href) {
		setHref(href);
	}
	
	@XmlElementRef(type = ProfileTemplateReference.class)
	@Override
	public Collection<ProfileTemplateReference> getFiles() {
		return super.getFiles();
	}
	
	@Override
	public void setFiles(Collection<ProfileTemplateReference> files) {
		super.setFiles(files);
	}
	
}
