package com.dynatrace.mom.connector.model.profiletemplates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReference;

@XmlRootElement(name = ProfileTemplateReference.XML_ELEMENT_PROFILE_TEMPLATE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ProfileTemplateReference extends AbstractFileReference {
	
	public static final String XML_ELEMENT_PROFILE_TEMPLATE	= "profiletemplate";

	public ProfileTemplateReference() {
		
	}
	
	public ProfileTemplateReference(String id, String href) {
		setId(id);
		setHref(href);
	}
	
}
