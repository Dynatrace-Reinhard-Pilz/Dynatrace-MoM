package com.dynatrace;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import com.dynatrace.http.HttpClient;
import com.dynatrace.http.config.Credentials;

public class VersionTEst implements HostnameVerifier, X509TrustManager {
	
	public static final String HEADER_CONTENT_TYPE =
			"Content-Type".intern();
	public static final String LINE_FEED =
			"\r\n".intern();
	public static final String HEADER_AUTHORIZATION =
			"Authorization".intern();
	public static final String BASIC =
			"Basic ".intern();
	public static final String HEADER_CONTENT_DISPOSITION =
			"Content-Disposition".intern();
	public static final String HEADER_TRANSFER_ENCODING =
			"Content-Transfer-Encoding".intern();

	@Test
	public void testFoo() throws Exception {
		String sUrl = "https://localhost:8021/rest/management/version";
		URL url = new URL(sUrl);
		HttpURLConnection con =
				(HttpURLConnection) url.openConnection();
		con.setConnectTimeout(5000);
		con.setReadTimeout(10000);
		handleSecurity(con);
		con.setRequestMethod("GET");
		Credentials credentials = new Credentials("admin", "admin");
		setCredentials(con, credentials);
		con.connect();
		final int contentLength = con.getContentLength();
		int responseCode = con.getResponseCode();
	}
	
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
