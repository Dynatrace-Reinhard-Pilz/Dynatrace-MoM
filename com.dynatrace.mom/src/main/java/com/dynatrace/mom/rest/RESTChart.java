package com.dynatrace.mom.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = RESTChart.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class RESTChart extends RESTReferrable {

	public static final String TAG = "chart";
	
	private String name = null;
	
	RESTChart() {
		this(null, null);
	}
	
	public RESTChart(final String name, final HrefGenerator hrefGenerator) {
		super(hrefGenerator);
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final String getHrefPart() {
		return getName();
	}

}
