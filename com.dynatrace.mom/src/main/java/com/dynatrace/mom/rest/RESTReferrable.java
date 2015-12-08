package com.dynatrace.mom.rest;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class RESTReferrable {

	private final HrefGenerator hrefGenerator;
	
	RESTReferrable() {
		this(null);
	}
	
	public RESTReferrable(final HrefGenerator hrefGenerator) {
		this.hrefGenerator = hrefGenerator;
	}
	
	@XmlAttribute(name = "href")
	public final String getHref() {
		return hrefGenerator.getHref(getHrefPart());
	}
	
	protected abstract String getHrefPart();

}
