package com.dynatrace.http.ant;

import org.apache.tools.ant.Task;

public class HttpParam extends Task {
	
	private String name = null;
	private String value = null;
	private boolean trim = false;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		if (value == null) {
			return null;
		}
		if (trim) {
			return value.trim();
		}
		return value;
	}
	
	public void setTrim(boolean trim) {
		this.trim = trim;
	}
	
	public void addText(String text) {
		this.value = text;
	}

}
