package com.dynatrace.mom.mail;

public interface MailConfig {
	
	public String getHost();
	
	public String getPort();
	
	public String getUsername();
	
	public String getPassword();

	public boolean withAuthentication();
	
}
