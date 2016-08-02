package foo;

import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

final class TrustAllCertsManager implements HostnameVerifier, X509TrustManager {
	
	private static final TrustAllCertsManager INSTANCE =
		new TrustAllCertsManager();
	
	private static final TrustManager[] TRUST_ALL_CERTS =
		new TrustManager[ ] { INSTANCE };
	
	private static final SSLSocketFactory SOCKETFACTORY = createSocketFactory();
	
	private static SSLSocketFactory createSocketFactory() {
		try {
			SSLContext sslCtx = SSLContext.getInstance("SSL");
	        sslCtx.init(
	        	null,
	        	TRUST_ALL_CERTS,
	        	new SecureRandom()
	        );
	        return sslCtx.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError(e.getMessage());
		} catch (KeyManagementException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	static void handleSecurity(HttpURLConnection con) {
		if (con instanceof HttpsURLConnection) {
			TrustAllCertsManager.handleSecurity((HttpsURLConnection) con);
		}		
	}
	
	static void handleSecurity(HttpsURLConnection con) {
        con.setSSLSocketFactory(SOCKETFACTORY);
		con.setHostnameVerifier(INSTANCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verify(String hostName, SSLSession sslSession) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkClientTrusted(X509Certificate[] certs, String authType)
			throws CertificateException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkServerTrusted(X509Certificate[] certs, String authType)
			throws CertificateException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final X509Certificate[] getAcceptedIssuers() {
		return null;
	}
	
}
