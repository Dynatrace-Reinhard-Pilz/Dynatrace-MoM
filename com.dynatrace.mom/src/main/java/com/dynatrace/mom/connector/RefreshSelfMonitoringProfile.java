package com.dynatrace.mom.connector;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.reporting.Availability;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Version;

public class RefreshSelfMonitoringProfile extends ServerOperation<SystemProfile> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RefreshSelfMonitoringProfile.class.getName());

	public RefreshSelfMonitoringProfile(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	public Request<SystemProfile> createRequest() {
		return new RefreshSelfMonitoringRequest();
	}
	
	@Override
	public boolean execute() {
		return super.execute();
	}
	
	public void onSystemProfile(SystemProfile systemProfile) {
		// subclasses may override
	}

	@Override
	protected void handleResult(SystemProfile profile) {
		if (profile == null) {
			return;
		}
		if (!profile.isAvailableLocally()) {
			return;
		}
		if (!profile.isSelfMonitoringProfile()) {
			return;
		}
		onSystemProfile(profile);
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
	@Override
	public void onFailure() {
		MomConnectorAware connectorAware =
				getAttribute(MomConnectorAware.class);
		connectorAware.setMoMConnectorVersion(Version.UNDEFINED);
		connectorAware.setMoMConnectorAvailability(Availability.Unavailable);
	}

}
