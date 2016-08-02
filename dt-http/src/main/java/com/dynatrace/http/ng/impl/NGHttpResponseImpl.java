package com.dynatrace.http.ng.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.dynatrace.http.ng.NGHttpResponse;

public class NGHttpResponseImpl implements NGHttpResponse {
	
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	@Override
	public InputStream openStream() {
		return new ByteArrayInputStream(out.toByteArray());
	}

}
