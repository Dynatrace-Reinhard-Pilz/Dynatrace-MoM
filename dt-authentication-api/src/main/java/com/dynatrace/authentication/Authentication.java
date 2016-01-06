package com.dynatrace.authentication;

import java.io.InputStream;

public interface Authentication {

	InputStream getUser();
	InputStream getPass();
	
}
