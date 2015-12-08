package com.dynatrace.mom.rest;

public final class FixedHrefGenerator implements HrefGenerator {

	private final String url;
	
	public FixedHrefGenerator(final String url) {
		this.url = url;
	}

	@Override
	public final String getHref(final String value) {
		return url;
	}
}
