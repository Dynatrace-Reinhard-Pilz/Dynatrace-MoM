package com.dynatrace.http.ng;

import java.io.InputStream;
import java.io.OutputStream;

public interface NGHttpResponse {

	OutputStream getOutputStream();
	InputStream openStream();
}
