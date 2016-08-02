package com.dynatrace.http.ng.impl;

import java.io.InputStream;
import java.net.URL;

import com.dynatrace.http.Method;
import com.dynatrace.http.ng.HttpRequest;

public class NGHttpRequestImpl implements HttpRequest {
	
	private final URL url;
	private final Method method;
	
	public NGHttpRequestImpl(URL url, Method method) {
		this.url = url;
		this.method = method;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public InputStream getRequestBody() {
		return null;
	}

}
