package com.dynatrace.incidents;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.utils.Strings;

public class GetIncident extends AbstractRequest<Incident> {
	
	private static final Logger LOGGER =
			Logger.getLogger(GetIncident.class.getName());
	
	private static final String SELF_MONITORING_PROFILE =
			"dynaTrace Self-Monitoring";
	private final IncidentRule incidentRule;
	private final IncidentReference incidentReference;
	
	// https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/4f0198bc-60ac-489d-ad40-69e55f51800f
		
	public GetIncident(
		IncidentRule incidentRule,
		IncidentReference incidentReference
	) {
		this.incidentRule = incidentRule;
		this.incidentReference = incidentReference;
	}
	
	@Override
	protected void logResponse(byte[] bytes) {
//		LOGGER.log(Level.INFO, getPath());
//		LOGGER.log(Level.INFO, new String(bytes));
	}
	
	@Override
	protected HttpResponse<Incident> onFileNotFound(FileNotFoundException e, int code) {
		return new HttpResponse<Incident>(code);
	}

	@Override
	public String getPath() {
		return new StringBuilder("/rest/management/profiles/")
			.append(Strings.encode(SELF_MONITORING_PROFILE))
			.append("/incidentrules/")
			.append(Strings.encode(incidentRule.getName()))
			.append("/incidents/")
			.append(incidentReference.getId())
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
	protected Incident getResultPrototype() {
		return new Incident();
	}
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
