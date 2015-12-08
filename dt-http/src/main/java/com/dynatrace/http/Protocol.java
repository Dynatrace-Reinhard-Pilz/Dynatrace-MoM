package com.dynatrace.http;

/**
 * The protocols the implementation of the {@link HttpClient} is
 * supposed to support
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public enum Protocol {
	
	HTTP, HTTPS;
	
	public static Protocol fromString(String s) {
		if (s == null) {
			return null;
		}
		Protocol[] protocols = Protocol.values();
		for (Protocol protocol : protocols) {
			if (s.toUpperCase().equals(protocol.name())) {
				return protocol;
			}
		}
		return null;
	}

}
