package com.dynatrace.http.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.http.Protocol;

/**
 * A configuration object holding the necessary information to connect to and
 * authenticate against a HTTP server
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@XmlRootElement(name = "server")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "connectionConfig", Credentials.TAG })
public class ServerConfig {
	
	private Credentials credentials = null;
	private ConnectionConfig connectionConfig = null;
	
	public ServerConfig() {
	}
	
	public ServerConfig(
		ConnectionConfig connectionConfig,
		Credentials credentials
	) {
		this.credentials = credentials;
		this.connectionConfig = connectionConfig;
	}
	
	public String getHost() {
		if (connectionConfig == null) {
			return null;
		}
		return connectionConfig.getHost();
	}
	
	public int getPort() {
		if (connectionConfig == null) {
			return 0;
		}
		return connectionConfig.getPort();
	}
	
	/**
	 * @return the {@link Credentials} to use for Basic Authentication when
	 * 		opening a connection to the HTTP server or {@code null} if no
	 * 		authentication is required
	 */
	@XmlElement(name = Credentials.TAG)
	public Credentials getCredentials() {
		return credentials;
	}
	
	/**
	 * Sets the {@link Credentials} to use for Basic Authentication when
	 * 		opening a connection to the HTTP server
	 * 
	 * @param credentials the {@link Credentials} to use for
	 * 		Basic Authentication when opening a connection to the HTTP server
	 * 		or {@code null} if no authentication is expected to be required
	 */
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
	
	public void setCredentials(String user, String pass) {
		this.credentials = new Credentials(user, pass);
	}
	
	/**
	 * @return the {@link ConnectionConfig} holding address, protocol and port
	 * 		to connect to when opening a connection to the HTTP server
	 */
	@XmlElement(name = ConnectionConfig.TAG)
	public ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}
	
	/**
	 * Sets the {@link ConnectionConfig} holding address, protocol and port
	 * 		to connect to when opening a connection to the HTTP server
	 * 
	 * @param connectionConfig the {@link ConnectionConfig} holding address,
	 * 		protocol and port to connect to when opening a connection to the
	 * 		HTTP server
	 */
	public void setConnectionConfig(ConnectionConfig connectionConfig) {
		this.connectionConfig = connectionConfig;
	}
	
	public void setConnectionConfig(Protocol protocol, String host, int port) {
		this.connectionConfig = new ConnectionConfig(protocol, host, port);
	}
	
	/**
	 * Checks if the configured {@link ConnectionConfig} is defined and valid
	 * and if the configured {@link Credentials} if configured are valid.
	 * 
	 * @return {@code true} if there is a {@link ConnectionConfig} set and valid
	 * 		and the {@link Credentials} are valid if defined, {@code false}
	 * 		otherwise.
	 */
	public boolean isValid() {
		if (connectionConfig == null) {
			return false;
		}
		if (!connectionConfig.isValid()) {
			return false;
		}
		if (credentials != null) {
			return credentials.isValid();
		}
		return true;
	}
	
	public URL createURL(String path) throws MalformedURLException {
		return connectionConfig.createURL(path);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(connectionConfig.getHost()).append(":").append(connectionConfig.getPort()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionConfig == null) ? 0 : connectionConfig.hashCode());
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
		ServerConfig other = (ServerConfig) obj;
		if (connectionConfig == null) {
			if (other.connectionConfig != null)
				return false;
		} else if (!connectionConfig.equals(other.connectionConfig))
			return false;
		return true;
	}
	
}
