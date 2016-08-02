package com.dynatrace.http.ng;

import java.io.InputStream;
import java.net.URL;

import com.dynatrace.http.Method;

public interface HttpRequest {

	URL getUrl();
	Method getMethod();
	InputStream getRequestBody();
	
}
