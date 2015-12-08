package com.dynatrace.http.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.dynatrace.http.Http;
import com.dynatrace.http.HttpClient;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.UnexpectedResponseCodeException;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.Unchecked;
import com.dynatrace.xml.XMLUtil;

/**
 * Base class for all REST Requests to be executed to the dynaTrace Server
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public abstract class AbstractRequest<T> implements Request<T> {
	
	private static final Logger LOGGER =
			Logger.getLogger(AbstractRequest.class.getName());
	
	static final String ERR_MSG_INVALID_SERVER_CONFIG =
			"Server Config is not valid".intern();
	
	protected static final HttpClient HTTPCLIENT = Http.client();
	
	
	
	protected Logger getLogger() {
		return LOGGER;
	}
	
	protected HttpResponse<T> onFileNotFound(FileNotFoundException e, int code) {
		getLogger().log(Level.WARNING, "Unable to execute request", e);
		return new HttpResponse<T>(code, e);
	}
	
	protected HttpResponse<T> onJAXBException(JAXBException e, int code, String data) {
		getLogger().log(Level.WARNING, data);
		return new HttpResponse<T>(code, e);
	}
	
	/**
	 * Executes the REST Request to the dynaTrace Server
	 * 
	 * @throws IOException if sending the request fails
	 */
	public HttpResponse<T> execute(ServerConfig serverConfig) {
		long start = System.currentTimeMillis();
		getLogger().log(Level.FINEST, MessageFormat.format(
				"Executing {0} {1}...",
				getClass().getSimpleName(),
				serverConfig
		));
		URL url = null;
		try {
			url = serverConfig.createURL(getPath());
		} catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "Unable to execute request", e);
			return new HttpResponse<T>(Integer.MIN_VALUE, e);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int code = Integer.MIN_VALUE;
		try {
			code = HTTPCLIENT.request(
					url,
					getMethod(),
					serverConfig.getCredentials(),
					out
			);
		} catch (SocketTimeoutException e) {
			return new HttpResponse<T>(code, e);
		} catch (ConnectException e) {
			return new HttpResponse<T>(code, e);
		} catch (FileNotFoundException e) {
			onFileNotFound(e, code);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Unable to execute request", e);
			return new HttpResponse<T>(code, e);
		}
		if (code == Integer.MIN_VALUE) {
			getLogger().log(Level.WARNING, "response code: " + code);
			return new HttpResponse<T>(code);
		}
		ResponseCode expected = getExpectedResponseCode();
		if (!expected.matches(code)) {
			getLogger().log(Level.WARNING, "response code: " + code);
			try {
				out.close();
			} catch (IOException e) {
				// ignore
			}
			return new HttpResponse<T>(
				code,
				new UnexpectedResponseCodeException(expected, code, new String(out.toByteArray())));
		}
		try {
			out.close();
		} catch (IOException e) {
			return new HttpResponse<T>(code, e);
		}
		byte[] bytes = out.toByteArray();
		logResponse(bytes);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			Object response = XMLUtil.deserialize(
				in,
				getResultPrototypeClass()
			);
			return new HttpResponse<T>(code, castResult(response));
		} catch (Error | RuntimeException e) {
			return new HttpResponse<T>(code, e);
		} catch (NoRouteToHostException e) {
			return new HttpResponse<T>(code, null, e);
		} catch (IOException e) {
			Throwable cause = e.getCause();
			if (cause instanceof JAXBException) {
				onJAXBException((JAXBException) cause, code, new String(out.toByteArray()));
			}
			return new HttpResponse<T>(code, null, e);
		} finally {
			long end = System.currentTimeMillis();
			getLogger().log(Level.FINEST, MessageFormat.format(
					"    {0} {1} lasted {2} ms",
					getClass().getSimpleName(),
					serverConfig,
					(end - start)
			));
		}
	}
	
	protected void logResponse(byte[] bytes) {
		
	}
	
	protected T castResult(Object o) {
		return Unchecked.cast(o);
	}
	
	protected Class<?> getResultPrototypeClass() {
		return getResultPrototype().getClass();
	}
	
	protected abstract String getPath();
	protected abstract Method getMethod();
	protected abstract ResponseCode getExpectedResponseCode();
	protected abstract T getResultPrototype();

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractRequest<?> other = (AbstractRequest<?>) obj;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		return true;
	}	
}
