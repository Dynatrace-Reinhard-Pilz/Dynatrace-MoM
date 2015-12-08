package com.dynatrace.mom.mail;



public class MailConfigImpl implements MailConfig {
	
	private static final boolean SMTP_AUTH_ENABLED = false;
	
	private final String host;
	private final String port;
	private final String username;
	private final String password;
	private final boolean authentication;
	
	public MailConfigImpl() {		
		
		this.host = "mailrelay-ext.gomez.com";
		this.port = "25";
		
		this.username = null;
		this.password = null;
		this.authentication = SMTP_AUTH_ENABLED;
	}
	

	public String getHost() {
		return host;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	public boolean withAuthentication() {
		return authentication;
	}

}
