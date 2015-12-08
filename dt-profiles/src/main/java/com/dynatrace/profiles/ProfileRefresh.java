package com.dynatrace.profiles;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class ProfileRefresh extends ServerOperation<SystemProfiles> {
	
	private static final Logger LOGGER =
			Logger.getLogger(ProfileRefresh.class.getName());
	
	public ProfileRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	protected void handleResult(SystemProfiles profiles) {
		getAttribute(ProfileCollection.class).addAll(profiles);
	}

	@Override
	public Request<SystemProfiles> createRequest() {
		return new ProfilesRequest();
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
