package com.dynatrace.authentication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Authenticator {
	
	/**
	 * Encodes the configured user name and password to using a BASE64 Encoder
	 * to be used for Basic Authentication (HTTP).
	 * 
	 * @param url the {@link URL} a connection is about to be opened for, and
	 * 		for which authentication is required
	 * @param out the {@link OutputStream} to add the credentials to
	 * 
	 * @return {@code true} if this {@link Authenticator} was able to provide
	 * 		authentication for the given {@link URL}, {@code false} otherwise
	 * 
	 * @throws IllegalArgumentException if either the user name or the password
	 * 		are {@code null} or empty
	 */
	boolean encode(URL url, OutputStream out) throws IOException;
	
}
