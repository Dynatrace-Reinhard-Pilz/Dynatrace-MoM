package com.dynatrace.http;

import java.util.Objects;

import com.dynatrace.utils.Objs;

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
		return fromString(s, Protocol.values());
	}
	
	public static Protocol fromString(String s, Protocol...protocols) {
		if (Objs.isEitherNull(s, protocols)) {
			return null;
		}
		String sProtocol = s.toUpperCase();
		for (Protocol protocol : protocols) {
			if (equals(sProtocol, protocol)) {
				return protocol;
			}
		}
		return null;
	}
	
	private static boolean equals(String uCaseProtocol, Protocol protocol) {
		Objects.requireNonNull(uCaseProtocol);
		if (protocol == null) {
			return false;
		}
		if (protocol.name().equals(uCaseProtocol)) {
			return true;
		}
		return false;
	}

}
