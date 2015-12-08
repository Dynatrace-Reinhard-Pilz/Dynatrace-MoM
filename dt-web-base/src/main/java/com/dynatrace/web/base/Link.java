package com.dynatrace.web.base;

public final class Link {

	private final String text;
	private final String url;
	
	public Link(String text, String url) {
		this.text = text;
		this.url = url;
	}
	
	public String getText() {
		return text;
	}
	
	public String getUrl() {
		return url;
	}
}
