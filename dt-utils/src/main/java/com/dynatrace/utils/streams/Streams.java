package com.dynatrace.utils.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.Objects;

import com.dynatrace.utils.Closeables;

public class Streams {
	
	public synchronized int request(
		URL url,
		Method m,
		Authenticator auth,
		OutputStream out
	)
		throws IOException
	{
		Objects.requireNonNull(url);
		Objects.requireNonNull(m);
		
		int responseCode = Integer.MIN_VALUE;
		InputStream in = null;
		try {
			HttpURLConnection con =
					(HttpURLConnection) url.openConnection();
			con.setConnectTimeout(50000);
			con.setReadTimeout(50000);
			con.setRequestMethod(m.name());
			con.connect();
			
			final int contentLength = con.getContentLength();
			responseCode = con.getResponseCode();
			try {
				in = con.getInputStream();
			} catch (IOException ioe) {
				in = con.getErrorStream();
			}
			if (contentLength > 0) {
				int maxBufferSize = 1024 * 1024 * 10;
				Closeables.copy(maxBufferSize, in, out, contentLength);
			} else {
				Closeables.copy(in, out);
			}
		} catch (NoRouteToHostException nrthe) {
			return Integer.MIN_VALUE;
		} catch (IOException e) {
			if (responseCode != Integer.MIN_VALUE) {
				return responseCode;
			}
			throw e;
		} finally {
			Closeables.close(in);
		}
		return responseCode;
	}	
}
