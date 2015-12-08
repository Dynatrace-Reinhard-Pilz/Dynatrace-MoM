package com.dynatrace.mom.mail;

public class MailSenderTest {

	public static void main(String[] args) throws Exception {
		MailSenderTest test = new MailSenderTest();
		test.execute();
	}
	
	public void execute() throws Exception {
		MailConfigImpl config = new MailConfigImpl();
		MailSenderImpl mailSender = new MailSenderImpl(config);
		MailSenderData.Builder builder = new MailSenderData.Builder();
		mailSender.sendEmails(builder.build());
	}
}
