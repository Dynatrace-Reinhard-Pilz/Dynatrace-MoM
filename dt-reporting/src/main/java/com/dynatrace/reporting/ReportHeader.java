package com.dynatrace.reporting;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*
	<reportheader>
	  <reportdetails>
	    <user>admin</user>
	  </reportdetails>
	</reportheader>
*/

@XmlRootElement(name = "reportheader")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ReportHeader {

	private ReportDetails reportDetails = null;
	
	public final void setReportDetails(final ReportDetails reportDetails) {
		this.reportDetails = reportDetails;
	}
	
	@XmlElement(name = "reportdetails")
	public final ReportDetails getReportDetails() {
		return reportDetails;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
