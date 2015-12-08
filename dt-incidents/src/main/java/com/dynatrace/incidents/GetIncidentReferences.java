package com.dynatrace.incidents;

import java.util.logging.Logger;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.utils.Strings;

public class GetIncidentReferences extends AbstractRequest<XmlIncidentRule> {
	
	private static final Logger LOGGER =
			Logger.getLogger(GetIncidentReferences.class.getName());
	
	private static final String SELF_MONITORING_PROFILE = "dynaTrace Self-Monitoring";
	private final IncidentRule incidentRule;
	
	public GetIncidentReferences(IncidentRule incidentRule) {
		this.incidentRule = incidentRule;
	}
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}
	
	@Override
	protected void logResponse(byte[] bytes) {
//		if (getPath().contains("Performance") && getPath().contains("Warehouse") && getPath().contains("Offline")) {
//			LOGGER.log(Level.INFO, getPath());
//			LOGGER.log(Level.INFO, new String(bytes));
//		}
	}

	@Override
	public String getPath() {
		return new StringBuilder("/rest/management/profiles/")
			.append(Strings.encode(SELF_MONITORING_PROFILE))
			.append("/incidentrules/")
			.append(Strings.encode(incidentRule.getName()))
			.append("/incidents")
			.toString();
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
	protected XmlIncidentRule getResultPrototype() {
		return new XmlIncidentRule();
	}

}
