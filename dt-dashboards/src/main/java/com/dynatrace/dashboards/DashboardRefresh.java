package com.dynatrace.dashboards;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class DashboardRefresh extends ServerOperation<Dashboards> {
	
	private static final Logger LOGGER =
			Logger.getLogger(DashboardRefresh.class.getName());
	
	public DashboardRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	protected void handleResult(Dashboards dashboards) {
		getAttribute(DashboardCollection.class).addAll(dashboards);
	}

	@Override
	public Request<Dashboards> createRequest() {
		return new DashboardsRequest();
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
