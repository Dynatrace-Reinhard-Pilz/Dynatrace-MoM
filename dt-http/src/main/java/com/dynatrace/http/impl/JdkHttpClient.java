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
import java.net.NoRouteToHostException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.dynatrace.http.HttpClient;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.UploadResult;
import com.dynatrace.http.config.Credentials;
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
public final class JdkHttpClient
	implements HttpClient, HostnameVerifier, X509TrustManager {
	
	private static final Logger LOGGER =
			Logger.getLogger(JdkHttpClient.class.getName());
	
	public synchronized <T> HttpResponse<T> request(
		URL url,
		Method method,
		Credentials credentials,
		Class<T> responseClass
	) throws IOException {
		ByteArrayOutputStream out = null;
		InputStream in = null;
		int status = 0;
		T response = null;
		try {
			out = new ByteArrayOutputStream();
			status = request(url, method, credentials, out);
			if ((status == HttpURLConnection.HTTP_FORBIDDEN) || (status == HttpURLConnection.HTTP_UNAUTHORIZED)) {
				String missingPermission = Unauthorized.getMissingPermission(new String(out.toByteArray()));
				if (missingPermission == null) {
					missingPermission = "<unknown permission>";
				}
				return new HttpResponse<T>(
					status,
					null,
					new PermissionDeniedException(missingPermission)
				);
			}
			in = new ByteArrayInputStream(out.toByteArray());
			response = XMLUtil.<T>deserialize(in, responseClass);
			return new HttpResponse<T>(status, response, null);
		} catch (IOException e) {
//			LOGGER.log(Level.WARNING, "Unable to get response for " + url, e);
			return new HttpResponse<T>(status, response, e);
		} finally {
			Closeables.closeQuietly(in);
			Closeables.closeQuietly(out);
		}
	}
	
	/**
	 * Executes a request to the given {@link URL} using the given
	 * {@link Method} an optionally performs Basic Authentication with the
	 * given {@link Credentials}.
	 * 
	 * @param url the {@link URL} to send the request to
	 * @param method the {@link Method} to use for the request
	 * @param credentials the user credentials to be used for
	 * 		Basic Authentication or {@code null} if no authentication is
	 * 		expected to be required
	 * @param out the {@link OutputStream} where the response delivered by the
	 * 		HTTP Server should be streamed into.
	 * 
	 * @return the HTTP Response code delivered by the Server
	 * 
	 * @throws IOException if opening the connection to the HTTP Server fails
	 * @throws NullPointerException if the given {@link URL} or the given
	 * 		{@link Method} are {@code null}.
	 */
	@Override
	public synchronized int request(
		URL url,
		Method method,
		Credentials credentials,
		OutputStream out
	)
			throws IOException
	{
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		int responseCode = Integer.MIN_VALUE;
		InputStream in = null;
		try {
			HttpURLConnection con =
					(HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(10000);
			handleSecurity(con);
			con.setRequestMethod(method.name());
			setCredentials(con, credentials);
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
	
	/**
	 * Prepares the given {@link HttpURLConnection} to authenticate via
	 * Basic Authentication using the given {@link Credentials}.
	 * 
	 * @param conn the {@link HttpURLConnection} to prepare for
	 * 		Basic Authentication
	 * @param cred the {@link Credentials} providing user name and
	 * 		password for Basic Authentication
	 * 
	 * @throws NullPointerException if the given {@link HttpURLConnection} is
	 * 		{@code null}.
	 * @throws IllegalArgumentException if the given {@link Credentials} either
	 * 		don't contain a user name or password
	 */
	private void setCredentials(HttpURLConnection conn, Credentials cred) {
		Objects.requireNonNull(conn);
		if (cred == null) {
			return;
		}
		conn.setRequestProperty(
				HEADER_AUTHORIZATION,
				BASIC + cred.encode()
		);
	}
	
	private void handleSecurity(HttpURLConnection conn) {
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			// Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] { this };

			try {
				SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		        httpsConn.setSSLSocketFactory(sc.getSocketFactory());
			} catch (NoSuchAlgorithmException e) {
				// ignore
			} catch (KeyManagementException e) {
				// ignore
			}
			httpsConn.setHostnameVerifier(this);
		}		
	}	
	
	@Override
	public UploadResult upload(
		URL url,
		Credentials credentials,
		String fileName,
		InputStream inputStream
	) throws IOException {
		LOGGER.log(Level.FINER, "File Upload to " + url.toString());
		HttpURLConnection con = null;
		OutputStream outputStream = null;
		PrintWriter writer = null;
		
		String boundary = "---------------------------" + System.currentTimeMillis();

		con = (HttpURLConnection) url.openConnection();
		handleSecurity(con);
		
		String encoding = credentials.encode();
		con.setRequestProperty("Authorization", "Basic " + encoding);		
		con.setUseCaches(false);
		con.setDoOutput(true); // indicates POST method
		con.setDoInput(true);
		con.setRequestProperty(HEADER_CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
		outputStream = con.getOutputStream();
		writer = new PrintWriter(
			new OutputStreamWriter(
				outputStream, Charset.defaultCharset().name()
			),
			true
		);
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append(HEADER_CONTENT_DISPOSITION + ": form-data; name=\"file\"; filename=\"" + fileName + "\"").append(LINE_FEED);
		writer.append(HEADER_CONTENT_TYPE + ": application/octet-stream").append(LINE_FEED);
		writer.append(HEADER_TRANSFER_ENCODING + ": binary").append(LINE_FEED);
		
		writer.append(LINE_FEED);
		writer.flush();

		long bytes = Closeables.copy(inputStream, outputStream);
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
		if (status == HttpURLConnection.HTTP_CREATED) {
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
		} else if (status == HttpURLConnection.HTTP_FORBIDDEN) {
			String errorString = null;
			try (
				InputStream in = con.getErrorStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
			) {
				Closeables.copy(in, out);
				errorString = new String(out.toByteArray());
			}
			if (errorString != null) {
				String missingPermission = Unauthorized.getMissingPermission(errorString);
				if (missingPermission != null) {
					throw new PermissionDeniedException(missingPermission);
				}
			}
			throw new IOException("Server returned non-OK status: " + status);
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}
		return new UploadResult(status, response, headers);		
	}	
	
	@Override
	public boolean verify(String hostName, SSLSession sslSession) {
		return true;
	}


	@Override
	public void checkClientTrusted(X509Certificate[] certs, String authType)
			throws CertificateException {
	}


	@Override
	public void checkServerTrusted(X509Certificate[] certs, String authType)
			throws CertificateException {
	}


	@Override
	public final X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
