package com.dynatrace.http.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.http.Protocol;
import com.dynatrace.utils.Strings;

/**
 * A configuration object holding the necessary information to open a HTTP
 * connection to a server
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = ConnectionConfig.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
		propOrder = {
				ConnectionConfig.ATTRIBUTE_PROTOCOL,
				ConnectionConfig.ATTRIBUTE_HOST,
				ConnectionConfig.ATTRIBUTE_PORT
		}
)
public final class ConnectionConfig {
	
	private static final int UNDEFINED_PORT = 0;

	public static final String TAG = "connection";
	static final String ATTRIBUTE_PROTOCOL	= "protocol";
	static final String ATTRIBUTE_HOST		= "host";
	static final String ATTRIBUTE_PORT		= "port";
	
	private Protocol protocol = null;
	private String host = null;
	private int port = UNDEFINED_PORT;
	
	public ConnectionConfig() {
		
	}
	
	public ConnectionConfig(Protocol protocol, String host, int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * @return the {@link Protocol} to use for HTTP connections
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_PROTOCOL)
	public Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Sets the {@link Protocol} to use for HTTP connections
	 * 
	 * @param protocol the {@link Protocol} to use for HTTP connections
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * @return the host name of IP address of the HTTP server
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_HOST)
	public String getHost() {
		return host;
	}
	
	/**
	 * Sets the host name or IP address of the HTTP server
	 * 
	 * @param host the host name or IP address of the HTTP server
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @return the port the HTTP server is expected to listen at
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_PORT)
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port the HTTP server is expected to listen at
	 * 
	 * @param port the port the HTTP server is expected to listen at
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Generates a {@link URL} based on the configured {@link Protocol}, host
	 * and port and the given server absolute path.
	 * 
	 * @param path the server absolute path of the {@link URL} to create
	 * 
	 * @return a {@link URL} referring to a request path on the server
	 * 		configured
	 * 
	 * @throws MalformedURLException if the {@link URL} to create would be
	 * 		invalid
	 * @throws NullPointerException if the given path is {@code null}
	 */
	public URL createURL(String path) throws MalformedURLException {
		Objects.requireNonNull(path);
		return new URL(protocol.name(), host, port, path);
	}
	
	/**
	 * Checks if the configured {@link Protocol}, host and port are valid.
	 * 
	 * @return {@code true} if the configured {@link Protocol}, host and port
	 * 		are valid, {@code false} otherwise
	 */
	@XmlTransient
	public boolean isValid() {
		if (protocol == null) {
			return false;
		}
		if (Strings.isNullOrEmpty(host)) {
			return false;
		}
		if (port == UNDEFINED_PORT) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		return new StringBuilder(protocol.name()).append(Strings.COLON).append(Strings.SLASH).append(Strings.SLASH).append(host).append(Strings.COLON).append(port).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionConfig other = (ConnectionConfig) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		if (protocol != other.protocol)
			return false;
		return true;
	}
	
	public static String toString(ConnectionConfig connectionConfig) {
		String host = connectionConfig.getHost();
		int port = connectionConfig.getPort();
		return "[" + host + ":" + port + "]";
	}
	
}
