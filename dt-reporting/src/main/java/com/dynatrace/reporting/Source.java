package com.dynatrace.reporting;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*	
	<source name="dynaTrace Self-Monitoring" filtersummary="last 30 minutes (auto)"></source>
*/	  

@XmlRootElement(name = "source")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class Source {

	private String name = null;
	private String filterSummary = null;
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public final String getName() {
		return name;
	}
	
	public final void setFilterSummary(final String filterSummary) {
		this.filterSummary = filterSummary;
	}
	
	@XmlAttribute(name = "filtersummary")
	public final String getFilterSummary() {
		return filterSummary;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
