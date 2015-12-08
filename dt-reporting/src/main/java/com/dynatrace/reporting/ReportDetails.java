package com.dynatrace.reporting;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*
	<reportdetails>
	  <user>admin</user>
	</reportdetails>
*/

@XmlRootElement(name = "reportdetails")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ReportDetails {
	
	private String user = null;
	
	public final void setUser(final String user) {
		this.user = user;
	}

	@XmlElement(name = "user")
	public final String getUser() {
		return user;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
