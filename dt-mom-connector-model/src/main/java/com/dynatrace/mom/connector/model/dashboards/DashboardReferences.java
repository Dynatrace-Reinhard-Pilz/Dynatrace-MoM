package com.dynatrace.mom.connector.model.dashboards;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.AbstractFileReferences;

@XmlRootElement(name = DashboardReferences.XML_ELEMENT_DASHBOARDS)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class DashboardReferences extends AbstractFileReferences<DashboardReference> {
	
	public static final String XML_ELEMENT_DASHBOARDS = "dashboards";
	
	public DashboardReferences() {
	}
	
	public DashboardReferences(String href) {
		setHref(href);
	}
	
	@Override
	@XmlElementRef(type = DashboardReference.class)
	public Collection<DashboardReference> getFiles() {
		return super.getFiles();
	}
	
	@Override
	public void setFiles(Collection<DashboardReference> files) {
		super.setFiles(files);
	}
	
}
