package com.dynatrace.http;

import com.dynatrace.http.impl.JdkHttpClient;

public class Http {

	public static HttpClient client() {
		return new JdkHttpClient();
	}
	
}
