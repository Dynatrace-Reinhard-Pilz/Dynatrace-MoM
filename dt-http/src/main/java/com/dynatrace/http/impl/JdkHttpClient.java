package com.dynatrace.http.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.authentication.Authenticator;
import com.dynatrace.http.HttpClient;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseVerifier;
import com.dynatrace.http.UploadResult;
import com.dynatrace.http.VerificationException;
import com.dynatrace.http.permissions.PermissionDeniedException;
import com.dynatrace.http.permissions.Unauthorized;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Iterables;
import com.dynatrace.xml.XMLUtil;

/**
 * A minimalistic HTTP Client
 * 
 * @author Reinhard Pilz
 *
 */
public final class JdkHttpClient implements HttpClient {
	
	private static final Logger LOGGER =
			Logger.getLogger(JdkHttpClient.class.getName());
	
	private ResponseVerifier verifier = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized <T> HttpResponse<T> request(
		URL url,
		Method m,
		Authenticator auth,
		Class<T> responseClass
	) throws IOException {
		ByteArrayOutputStream out = null;
		InputStream in = null;
		int status = 0;
		T response = null;
		try {
//			System.out.println(url);
			out = new ByteArrayOutputStream();
			status = request(url, m, auth, out);
//			System.out.println("status: " + status);
			switch (status) {
			case HttpURLConnection.HTTP_FORBIDDEN:
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				String missingPermission = Unauthorized.getMissingPermission(
					new String(out.toByteArray())
				);
				if (missingPermission == null) {
					missingPermission = "<unknown permission>";
				}
				return new HttpResponse<T>(
					status,
					null,
					new PermissionDeniedException(missingPermission)
				);
			default:
//				System.out.println("JdkHttpClient" + "." + "A");
				in = new ByteArrayInputStream(out.toByteArray());
//				System.out.println("JdkHttpClient" + "." + "B");
				try {
					response = XMLUtil.<T>deserialize(in, responseClass);
//					System.out.println("JdkHttpClient" + "." + "response: " + response);
				} catch (RuntimeException re) {
					re.printStackTrace(System.err);
					throw re;
				} catch (Error err) {
					err.printStackTrace(System.err);
					throw err;
				}
//				System.out.println("JdkHttpClient" + "." + "C");
				HttpResponse<T> httpResponse = new HttpResponse<T>(status, response, null);
//				System.out.println("JdkHttpClient" + "." + "D");
				return httpResponse;
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to get response for " + url, e);
			return new HttpResponse<T>(status, response, e);
		} catch (RuntimeException re) {
			re.printStackTrace(System.err);
			throw re;
		} catch (Error err) {
			err.printStackTrace(System.err);
			throw err;
		} finally {
			Closeables.close(in);
			Closeables.close(out);
		}
	}
	
	private URL resolveRedirectURL(HttpURLConnection con) {
		Objects.requireNonNull(con);
		String locationHeader = con.getHeaderField("Location");
		if (locationHeader == null) {
			return null;
		}
		try {
			return new URL(locationHeader);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
//			System.out.println(url.toString());
			con.setConnectTimeout(50000);
			con.setReadTimeout(50000);
			TrustAllCertsManager.handleSecurity(con);
			con.setRequestMethod(m.name());
			setCredentials(con, auth);
			con.connect();
			URL redirectURL = resolveRedirectURL(con);
			if (redirectURL != null) {
				return request(redirectURL, m, auth, out);
			}
			final int contentLength = con.getContentLength();
			responseCode = con.getResponseCode();
			try {
				in = con.getInputStream();
			} catch (IOException ioe) {
				ioe.printStackTrace(System.err);
				if (in == null) {
					in = con.getErrorStream();
				}
			}
			if (verifier != null) {
				verifier.verifyResponseHeader(url, "Content-Type", con.getHeaderField("Content-Type"));
			}
			if (contentLength > 0) {
				int maxBufferSize = 1024 * 1024 * 10;
				Closeables.copy(maxBufferSize, in, out, contentLength);
			} else {
				Closeables.copy(in, out);
			}
		} catch (NoRouteToHostException nrthe) {
			return Integer.MIN_VALUE;
		} catch (VerificationException e) {
			throw e;
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
	
	/**
	 * Prepares the given {@link HttpURLConnection} to authenticate via
	 * Basic Authentication using the given {@link Authenticator}.
	 * 
	 * @param con the {@link HttpURLConnection} to prepare for
	 * 		Basic Authentication
	 * @param auth the {@link Authenticator} providing user name and
	 * 		password for Basic Authentication
	 * 
	 * @throws NullPointerException if the given {@link HttpURLConnection} is
	 * 		{@code null}.
	 * @throws IllegalArgumentException if the given {@link Authenticator}
	 * 		either don't contain a user name or password
	 */
	private void setCredentials(HttpURLConnection con, Authenticator auth)
		throws IOException
	{
		Objects.requireNonNull(con);
		if (auth == null) {
			return;
		}
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			baos.write(BASIC.getBytes());
			auth.encode(con.getURL(), baos);
			con.setRequestProperty(
				HEADER_AUTHORIZATION,
				new String(baos.toByteArray())
			);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadResult upload(
		URL url,
		Authenticator auth,
		String fileName,
		InputStream is
	) throws IOException {
		LOGGER.log(Level.FINER, "File Upload to " + url.toString());
		HttpURLConnection con = null;
		OutputStream outputStream = null;
		PrintWriter writer = null;
		
		String boundary = "---------------------------"
				+ System.currentTimeMillis();

		con = (HttpURLConnection) url.openConnection();
		TrustAllCertsManager.handleSecurity(con);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			baos.write("Basic ".getBytes());
			auth.encode(url, baos);
			con.setRequestProperty(
				"Authorization",
				new String(baos.toByteArray())
			);		
		}
		con.setUseCaches(false);
		con.setDoOutput(true); // indicates POST method
		con.setDoInput(true);
		con.setRequestProperty(
			HEADER_CONTENT_TYPE,
			"multipart/form-data; boundary=" + boundary
		);
		outputStream = con.getOutputStream();
		writer = new PrintWriter(
			new OutputStreamWriter(
				outputStream, Charset.defaultCharset().name()
			),
			true
		);
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append(HEADER_CONTENT_DISPOSITION);
			writer.append(": form-data; name=\"file\"; filename=\"");
			writer.append(fileName);
			writer.append("\"");
			writer.append(LINE_FEED);
		writer.append(HEADER_CONTENT_TYPE);
			writer.append(": application/octet-stream");
			writer.append(LINE_FEED);
		writer.append(HEADER_TRANSFER_ENCODING);
			writer.append(": binary");
			writer.append(LINE_FEED);
		
		writer.append(LINE_FEED);
		writer.flush();

		long bytes = Closeables.copy(is, outputStream);
		LOGGER.log(Level.FINEST, bytes + " bytes streamed");
		outputStream.flush();

		writer.append(LINE_FEED);
		writer.flush();
		
		List<String> response = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();

		writer.append(LINE_FEED);
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = con.getResponseCode();
		switch (status) {
		case HttpURLConnection.HTTP_CREATED:
			Map<String, List<String>> headerFields = con.getHeaderFields();
			if (!Iterables.isNullOrEmpty(headerFields)) {
				for (String key : headerFields.keySet()) {
					List<String> values = headerFields.get(key);
					if (!Iterables.isNullOrEmpty(values)) {
						for (String value : values) {
							headers.put(key, value);
							break;
						}
					}
				}
			}
			try (InputStream in = con.getInputStream();
				Reader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
			) {
				String line = null;
				while ((line = br.readLine()) != null) {
					response.add(line);
				}
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				con.disconnect();
			}
			return new UploadResult(status, response, headers);		
		case HttpURLConnection.HTTP_FORBIDDEN:
			String errorString = null;
			try (
				InputStream in = con.getErrorStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
			) {
				Closeables.copy(in, out);
				errorString = new String(out.toByteArray());
			}
			if (errorString != null) {
				String missingPermission = Unauthorized.getMissingPermission(
					errorString
				);
				if (missingPermission != null) {
					throw new PermissionDeniedException(missingPermission);
				}
			}
			throw new IOException("Server returned non-OK status: " + status);
		default:
			throw new IOException("Server returned non-OK status: " + status);
		}
	}

	@Override
	public void setResponseVerifier(ResponseVerifier verifier) {
		this.verifier = verifier;
	}	

}
