package com.dynatrace.mom.mail;

import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.InternetAddress;

public interface MailData {

    public String getFrom();

    public List<InternetAddress> getRecipientList();

	public List<InternetAddress> getRecipientBCCList();

	public boolean isMultipart();

	public String getSubject();
	
	public String getContent();

	public Map<String, DataHandler> getImageMap();

}
