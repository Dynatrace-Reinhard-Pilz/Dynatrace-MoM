package com.dynatrace.incidents;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class IncidentRefresh extends ServerOperation<Incident> {
	
	private static final Logger LOGGER =
			Logger.getLogger(IncidentRefresh.class.getName());
	
	private final IncidentRule incidentRule;
	private final IncidentReference incidentRef;
	
	public IncidentRefresh(
		ExecutionContext ctx,
		ServerConfig scfg,
		IncidentRule incidentRule,
		IncidentReference incidentRef
	) {
		super(ctx, scfg);
		this.incidentRef = incidentRef;
		this.incidentRule = incidentRule;
	}
	
	@Override
	protected boolean prepare() {
		if (this.incidentRule.getId().equals("Host Memory Unhealthy")) {
			return false;
		}
		if (this.incidentRule.getId().equals("Host Availability")) {
			return false;
		}
		return super.prepare();
	}

	@Override
	protected void handleResult(Incident incident) {
		if (incident != null) {
			incidentRef.setIncident(incident);
		}
	}

	@Override
	public Request<Incident> createRequest() {
		return new GetIncident(incidentRule, incidentRef);
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
	@Override
	protected void handleException(Throwable exception) {
		if (exception == null) {
			return;
		}
		if (exception instanceof FileNotFoundException) {
			log(Level.WARNING, "Unable to request details for incident reference " + incidentRef.getId() + " of incident rule " + incidentRule.getId());
		} else {
			super.handleException(exception);
		}
	}
	
	@Override
	public String toString() {
		return IncidentRefresh.class.getSimpleName() + " [" + incidentRule.getId() + "/" + incidentRef.getId() + "]";
	}

}
