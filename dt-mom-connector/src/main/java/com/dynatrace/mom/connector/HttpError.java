package com.dynatrace.mom.connector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = HttpError.XML_ELEMENT_HTTP_ERROR)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class HttpError {
	
	public static final String XML_ELEMENT_HTTP_ERROR = "httperror";
	private static final String ATTRIBUTE_STATUS = "status";

	private int status = 0;
	private String message = null;
	
	public HttpError() {
		
	}
	
	public HttpError(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	@XmlAttribute(name = HttpError.ATTRIBUTE_STATUS)
	public int getStatus() {
		return status;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@XmlValue
	public String getMessage() {
		return message;
	}
}
