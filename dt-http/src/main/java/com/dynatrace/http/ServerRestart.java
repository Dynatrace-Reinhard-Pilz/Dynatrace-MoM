package com.dynatrace.http;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.BooleanResult;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.RestartServerRequest;
import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class ServerRestart extends ServerOperation<BooleanResult> {

	private static final Logger LOGGER = Logger.getLogger(
			ServerRestart.class.getName()
	);
	
	public ServerRestart(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
	@Override
	protected ConnectionStatus getDefaultConnectionStatus() {
		return ConnectionStatus.RESTARTING;
	}
	
	@Override
	protected boolean prepare() {
		ConnectionStatus connectionStatus =
				getContext().getAttribute(ConnectionAware.class).getConnectionStatus();
		switch (connectionStatus) {
		case RESTARTSCHEDULED:
			return true;
		case ONLINE:
		case ERRONEOUS:
		case OFFLINE:
		case RESTARTING:
		case UNREACHABLE:
			LOGGER.log(Level.INFO, "Execution of " + this.getClass().getSimpleName() + " for " + getConnectionConfig().getHost() + " discarded because status is " + connectionStatus);
			return false;
		}
		return true;
	}

	@Override
	protected void handleResult(BooleanResult result) {
		if (!result.isValue()) {
			setStatus(ConnectionStatus.ERRONEOUS);
			return;
		}
		
		log(Level.INFO,	"Polling until Server is not reachable anymore ...");
		while (requestVersion() != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}

		log(Level.INFO, "Polling until Server is reachable again ...");
		while (requestVersion() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
		updateVersion();
		setStatus(ConnectionStatus.ONLINE);
		return;
	}

	@Override
	public Request<BooleanResult> createRequest() {
		return new RestartServerRequest();
	}

}
