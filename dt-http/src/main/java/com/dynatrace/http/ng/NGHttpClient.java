package com.dynatrace.http.ng;

import java.io.IOException;
import java.net.URL;

import com.dynatrace.http.Method;

public interface NGHttpClient {

	void request(URL url, Method method) throws IOException;
	void request(HttpRequest request) throws IOException;
	
}
