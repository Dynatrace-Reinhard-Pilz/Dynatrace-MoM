package com.dynatrace.mom.connector;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.VersionRequest;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.reporting.Availability;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Version;

public class RefreshMoMConnectorVersion extends ServerOperation<Version> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RefreshMoMConnectorVersion.class.getName());

	public RefreshMoMConnectorVersion(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	public Request<Version> createRequest() {
		return new VersionRequest() {
			@Override
			protected String getPath() {
				return "/mom/version";
			}
		};
	}
	
	@Override
	public boolean execute() {
		return super.execute();
	}

	@Override
	protected void handleResult(Version version) {
		MomConnectorAware connectorAware =
				getAttribute(MomConnectorAware.class);
		connectorAware.setMoMConnectorVersion(version);
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
