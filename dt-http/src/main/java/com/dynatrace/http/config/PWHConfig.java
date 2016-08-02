package com.dynatrace.http.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.encryption.Encryptable;
import com.dynatrace.utils.encryption.Encryption;

@XmlRootElement(name = "pwh")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PWHConfig implements Encryptable {
	
	private String host = "none";
	private String database = "none";
	private Credentials credentials = new Credentials();
	private DatabaseType databaseType = DatabaseType.None;
	
	@XmlAttribute(name = "host")
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@XmlAttribute(name = "database")
	public String getDatabase() {
		return database;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	@XmlElementRef(type = Credentials.class)
	public Credentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
	
	@XmlAttribute(name = "type")
	public DatabaseType getDatabaseType() {
		return databaseType;
	}
	
	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}
	
	@Override
	public void encrypt() {
		Encryption.encrypt(credentials);
	}

	@Override
	public void decrypt() {
		Encryption.decrypt(credentials);
	}
	
	@Override
	public boolean isEncrypted() {
		return Encryption.isEncrypted(credentials);
	}
	
	@Override
	public final String toString() {
		return new StringBuilder(credentials.getUser())
			.append("@").append(host).append("/").append(database).toString();
	}
	
}
