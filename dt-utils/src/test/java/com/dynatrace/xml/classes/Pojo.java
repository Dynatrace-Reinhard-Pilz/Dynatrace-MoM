package com.dynatrace.xml.classes;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pojo")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pojo {
	
	private String field = null;
	
	public Pojo() {
		this.field = UUID.randomUUID().toString();
	}
	
	public Pojo(String field) {
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
}

