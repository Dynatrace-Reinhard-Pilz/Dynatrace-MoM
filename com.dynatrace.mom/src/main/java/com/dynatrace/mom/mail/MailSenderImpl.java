package com.dynatrace.mom.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.dynatrace.utils.Iterables;

public class MailSenderImpl extends Authenticator implements MailSender {

	private static final Logger LOG = Logger.getLogger(MailSenderImpl.class.getName());
	
	private final MailConfig config;
	private PasswordAuthentication authentication;
	
	public MailSenderImpl(MailConfig config) {
		this.config = config;
		String username = config.getUsername();
		String password = config.getPassword();
		authentication = new PasswordAuthentication(username, password);
		try {
			FileHandler fh = new FileHandler("./log/mail.%g.log", 10000000, 5, true);
			fh.setLevel(Level.ALL);
			fh.setFormatter(new SimpleFormatter());
			LOG.addHandler(fh);
		} catch (SecurityException e) {
			LOG.severe(e.getMessage());
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	}
	
	private void prepareMultiPartMessage(final Message message, final MailData mailData) throws MessagingException {
		//add html part
		MimeMultipart multipart = new MimeMultipart();
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(mailData.getContent(), "text/html; charset=utf-8");	
		multipart.addBodyPart(htmlPart);
		
		//add images
		for (Map.Entry<String, DataHandler> entry: mailData.getImageMap().entrySet()) {
		    String imageName = entry.getValue().getName().isEmpty()?entry.getKey():entry.getValue().getName();
		    DataHandler dataHandler = entry.getValue();
		    
		    MimeBodyPart imagePart = new MimeBodyPart();				    
    		imagePart.setDataHandler(dataHandler);
    		imagePart.setContentID("<" + imageName + ">");
    		imagePart.setDisposition("inline");
    		imagePart.setFileName(imageName);
    		
    		multipart.addBodyPart(imagePart);
        }
        
        message.setSubject(mailData.getSubject());
        message.setContent(multipart);
	}
	
	private void prepareRecipients(final Message message, final MailData mailData) throws MessagingException {
    	StringBuilder addresses = new StringBuilder();
    	InternetAddress addr[] =  new InternetAddress[mailData.getRecipientList().size()];
    	for (int i = 0; i<mailData.getRecipientList().size(); i++) {
    		addr[i] = mailData.getRecipientList().get(i);
    		addresses.append(mailData.getRecipientList().get(i).getAddress()).append(" ");
    	}
    	StringBuilder addressesBCC = new StringBuilder();
		InternetAddress bccAddr[] = new InternetAddress[mailData.getRecipientBCCList().size()];
        for (int i = 0; i<mailData.getRecipientBCCList().size(); i++) {
            bccAddr[i] = mailData.getRecipientBCCList().get(i);
            addressesBCC.append(mailData.getRecipientBCCList().get(i)).append(" ");
        }
    	message.addRecipients(RecipientType.TO, addr);
		message.addFrom(new InternetAddress[] { new InternetAddress(mailData.getFrom()) });
		message.addRecipients(RecipientType.BCC, bccAddr);
	}
	
	/**
	 * (non-non-Javadoc)
	 * 
	 * @see com.dynatrace.optional.mom.mail.compuware.apm.aas.mail.MailSender#sendEmails()
	 */
	@Override
	public void sendEmails(MailData mailData) throws RuntimeException {
		// Workaround prelude: remember the current system out
		PrintStream stdout = System.out;
    	if (Iterables.isNullOrEmpty(mailData.getRecipientList())) {
    		throw new RuntimeException("Recipient list is empty for email");
    	}

		try {
			System.setOut(new PrintStream( new FileOutputStream(new File("log/mail.0.log"),true) ));
        	Message message = new MimeMessage(getSession());
        	prepareRecipients(message, mailData);
			
			if (mailData.isMultipart()) {
				prepareMultiPartMessage(message, mailData);
			} else {
				message.setSubject(mailData.getSubject());
				message.setContent(mailData.getContent(), "text/html; charset=utf-8");
			}
         
        	Transport.send(message);
        } catch (final MessagingException | FileNotFoundException e) {
       		throw new RuntimeException(e);
		}
        System.setOut(stdout);
    }
	
	private Session getSession() {
		Properties properties = new Properties();

		properties.put("mail.smtp.host", config.getHost());
		properties.put("mail.smtp.port", config.getPort());

		if (config.withAuthentication()) {
			properties.setProperty("mail.smtp.submitter", config.getUsername());
			properties.setProperty("mail.smtp.auth", Boolean.toString(config.withAuthentication()));
		}
		return Session.getInstance(properties, this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return authentication;
	}

}
