package com.dynatrace.http;

import java.util.logging.Logger;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

public class VersionRefresh extends ServerOperation<Version> {
	
	private static final Logger LOGGER =
			Logger.getLogger(VersionRefresh.class.getName());
	
	public VersionRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	protected boolean prepare() {
		ConnectionStatus status =
		getAttribute(ConnectionAware.class).getConnectionStatus();
		if (status == null) {
			return true;
		}
		switch (status) {
		case ONLINE:
		case OFFLINE:
		case RESTARTSCHEDULED:
		case RESTARTING:
		case UNREACHABLE:
			return true;
		case ERRONEOUS:
			return true;
		}
		return true;
	}
	
	@Override
	protected void handleResult(Version version) {
		Versionable versionable = getAttribute(Versionable.class);
		versionable.updateVersion(version);
		ConnectionStatus status = getStatus();
		if ((status != ConnectionStatus.RESTARTSCHEDULED) && (status != ConnectionStatus.RESTARTING)) {
			setStatus(ConnectionStatus.ONLINE);
		}
	}
	
	@Override
	public Request<Version> createRequest() {
		return new VersionRequest();
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
}
