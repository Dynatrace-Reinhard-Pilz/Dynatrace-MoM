package com.dynatrace.http.ng.impl;

import static com.dynatrace.utils.Unchecked.cast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.dynatrace.authentication.Authenticator;
import com.dynatrace.http.Method;
import com.dynatrace.http.ng.HttpRequest;
import com.dynatrace.http.ng.NGHttpClient;

public class NGHttpClientImpl implements NGHttpClient {
	
	private Authenticator authenticator = null;

	@Override
	public void request(URL url, Method method)	throws IOException {
		request(new NGHttpRequestImpl(url, method));
	}

	@Override
	public void request(HttpRequest request) throws IOException {
		URL url = request.getUrl();
		Method method = request.getMethod();
		HttpURLConnection con = cast(url.openConnection());
		con.setRequestMethod(method.name());
		con.connect();
		try (InputStream in = con.getInputStream()) {
			
		}
	}

}
