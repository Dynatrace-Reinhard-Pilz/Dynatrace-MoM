package com.dynatrace.monitors.license.usage.rest.requests;

import java.net.MalformedURLException;
import java.net.URL;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfile;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfileRef;

public final class ProfileRequest extends AbstractRequest<XmlProfile> {
	
	private final XmlProfileRef profileRef;
	
	public ProfileRequest(XmlProfileRef profileRef) {
		this.profileRef = profileRef;
	}

	@Override
	protected String getPath() {
		try {
			return new URL(profileRef.getHref()).getPath();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	protected Method getMethod() {
		return Method.GET;
	}

	@Override
	protected ResponseCode getExpectedResponseCode() {
		return ResponseCode.OK;
	}

	@Override
	protected XmlProfile getResultPrototype() {
		return new XmlProfile();
	}

}
