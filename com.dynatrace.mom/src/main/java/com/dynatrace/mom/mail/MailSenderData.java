/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: MailSenderData.java
 * @date: 23-11-2012
 * @author: cwpl-mzejer
 */
package com.dynatrace.mom.mail;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

/**
 * Data used to compose notification email message.  
 * 
 * @author cwpl-mzejer
 */
public class MailSenderData implements MailData {

    private static final Logger LOGGER = Logger.getLogger(MailSenderData.class.getName());

    private final String from = "reinhard.pilz@dynatrace.com";
    private List<InternetAddress> recipientList;
    private List<InternetAddress> recipientBCCList;
    private String subject;
    private String content;
    private MomUser capellaUser = new MomUser();
    
    /**
     * key - template tag name <br>
     * value - DataHandler of image
     */
    private Map<String, DataHandler> imageMap = new HashMap<String, DataHandler>();
    private boolean isMultipart = true;
    
    public static class Builder {
        private List<String> imageList = new ArrayList<String>();
        
        public Builder() {
        }
        
        public Builder imageList(List<String> imageList) {
        	if (imageList != null) {
        		this.imageList.addAll(imageList);
        	}
        	return this;
        }

        public MailData build() {
            return new MailSenderData(this);
        }
    }    
    
    /**
     * @param user - Customer
     * @param imageMap - Images for multipart email type. Null by default. {@link #imageMap}
     * @param imageList - Image file names list. Default tag names are used starting with 'capellaImage*'. Indexed from 0.
     * @param type - Mail type, determines content of send email
     * @param message - Additional message used e.g. in failover.
     */
    private MailSenderData(Builder builder) {
        recipientList = new ArrayList<InternetAddress>();
        recipientBCCList = new ArrayList<InternetAddress>();
        
        addLogoImage();
        imageMap.putAll(toImageMap(builder.imageList));
        isMultipart = (imageMap.size() > 0);
        
        init();
    }
    
	private void init() {
    	generateRecipientList();
        processMailData();
    }
    
    private Map<String, DataHandler> toImageMap(List<String> imageList) {
    	Map<String, DataHandler> map = new HashMap<String, DataHandler>();
    	for (int i=0; i<imageList.size(); i++) {
    		String fileName = imageList.get(i);
    		map.put("capellaImage" + i, new DataHandler(new FileDataSource(fileName)));
    	}
    	return map;
    }
    
    private void addLogoImage() {
		InputStream is = null;
		try {
			String logoTag = "logoImage.png";
			is = this.getClass().getClassLoader().getResourceAsStream("mail/images/logo.png");
			imageMap.put(logoTag, new DataHandler(new ByteArrayDataSource(is, "application/octet-stream")));
		} catch (IOException e) {
			LOGGER.warning("Cannot attach logo file to email. Error: " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) { /* Suppress */ }
			}
		}
    }
    
	
	private void generateRecipientList() {
		recipientList.clear();
		recipientBCCList.clear();
		try {
			recipientList.add(new InternetAddress(capellaUser.getLogin()));
		} catch (AddressException e) {
			LOGGER.log(Level.SEVERE, "Address exception during email send", e);
		}
	}
	
	/**
	 * Email subject
	 *
	 * @return
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Email body
	 *
	 * @return
	 */
	public String getContent() {
		return content;
	}
	
	private void processMailData() {
		Map<String, Object> parserAttributes = new HashMap<String, Object>();
		
		addImagesAttributeToParser(parserAttributes);
		
    	if (capellaUser != null) {
    		parserAttributes.put("capellaUserLogin", capellaUser.getLogin());
    		parserAttributes.put("capellaUserPassword", capellaUser.getPassword());
		}
		
		subject = "Test Mail Subject";
		content = "Test Mail Content";
	}
	
	private void addImagesAttributeToParser(Map<String, Object> parserAttributes) {
		if (imageMap != null) {
			Map<String, String> images = new HashMap<String, String>();
			for (Entry<String, DataHandler> entry : imageMap.entrySet()) {
				if (!images.containsKey(entry.getKey())) {
					String filename = entry.getValue().getName().isEmpty()?entry.getKey():entry.getValue().getName(); 
					images.put(entry.getKey(), filename);				
				} else {
					LOGGER.warning("Tag name: " + entry.getKey() + " already exist. Change tag name in order to send email with valid data.");
				}
			}
			parserAttributes.put("images", images);	
		}
	}

	/**
	 * Returns configured or discovered Capella address
	 *
	 * @return
	 */
	public String getLocalHostAddress() {
		String addr = null;
		
		try {
			addr = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			LOGGER.severe(e.getMessage());
		}
		
		return addr;
	}

    public String getFrom() {
        return from;
    }

    public List<InternetAddress> getRecipientList() {
        return recipientList;
    }

    public List<InternetAddress> getRecipientBCCList() {
        return recipientBCCList;
    }
    
    @Override
    public String toString() {
    	String ret = "";
    	if (subject != null) {
    		ret += subject + ", ";
    	}
    	if (ret.isEmpty()) {
    		ret = super.toString();
    	}
    	return ret;
    }

	/**
	 * key - template tag name <br>
     * value - DataHandler of image 
	 * @return the imageMap
	 */
	public Map<String, DataHandler> getImageMap() {
		return imageMap;
	}

	/**
	 * @return the isMultipart
	 */
	public boolean isMultipart() {
		return isMultipart;
	}

}
