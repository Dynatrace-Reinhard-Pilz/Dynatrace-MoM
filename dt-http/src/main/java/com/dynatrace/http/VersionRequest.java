package com.dynatrace.http;

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

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.utils.Version;
import com.dynatrace.xml.XMLUtil;

/**
 * Retrieves the Build Version of a dynaTrace Server using the REST API
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class VersionRequest extends AbstractRequest<Version> {

	private String COMMAND = "/rest/management/version".intern();
	
	@XmlRootElement(name = "result")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class VersionResult {
		
		@XmlAttribute(name = "value")
		public String value = null;

	}
	
	public HttpResponse<Version> execute(ServerConfig serverConfig) {
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
			return new HttpResponse<Version>(Integer.MIN_VALUE, e);
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
			return new HttpResponse<Version>(code, e);
		} catch (ConnectException e) {
			return new HttpResponse<Version>(code, e);
		} catch (FileNotFoundException e) {
			onFileNotFound(e, code);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Unable to execute request", e);
			return new HttpResponse<Version>(code, e);
		}
		if (code == Integer.MIN_VALUE) {
			getLogger().log(Level.WARNING, "response code: " + code);
			return new HttpResponse<Version>(code);
		}
		ResponseCode expected = getExpectedResponseCode();
		if (!expected.matches(code)) {
			getLogger().log(Level.WARNING, "response code: " + code);
			try {
				out.close();
			} catch (IOException e) {
				// ignore
			}
			return new HttpResponse<Version>(
				code,
				new UnexpectedResponseCodeException(expected, code, new String(out.toByteArray()), this.getPath()));
		}
		try {
			out.close();
		} catch (IOException e) {
			return new HttpResponse<Version>(code, e);
		}
		byte[] bytes = out.toByteArray();
		logResponse(bytes);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			Object response = XMLUtil.deserialize(
				in,
				getResultPrototypeClass()
			);
			return new HttpResponse<Version>(code, castResult(response));
		} catch (Error | RuntimeException e) {
			return new HttpResponse<Version>(code, e);
		} catch (NoRouteToHostException e) {
			return new HttpResponse<Version>(code, null, e);
		} catch (IOException e) {
			Throwable cause = e.getCause();
			if (cause instanceof JAXBException) {
				onJAXBException((JAXBException) cause, code, new String(out.toByteArray()));
			}
			return new HttpResponse<Version>(code, null, e);
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
	

	@Override
	protected String getPath() {
		return COMMAND;
	}

	@Override
	protected final Method getMethod() {
		return Method.GET;
	}

	@Override
	protected final ResponseCode getExpectedResponseCode() {
		return ResponseCode.OK;
	}

	@Override
	protected final Version getResultPrototype() {
		return Version.UNDEFINED;
	}
	
	@Override
	protected Class<?> getResultPrototypeClass() {
		return VersionResult.class;
	}
	
	@Override
	protected Version castResult(Object o) {
		VersionResult versionResult = (VersionResult) o;
		if (versionResult == null) {
			return null;
		}
		if (versionResult.value == null) {
			return null;
		}
		return Version.parse(versionResult.value);
	}
	
	@Override
	protected HttpResponse<Version> onJAXBException(JAXBException e, int code, String data) {
		return new HttpResponse<Version>(code);
	}
}
