package com.dynatrace.collectors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.BooleanResult;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class CollectorRestart extends ServerOperation<BooleanResult> {

	private static final Logger LOGGER =
			Logger.getLogger(CollectorRestart.class.getName());
	
	private final String name;
	private final String host;
	
	public CollectorRestart(
		ExecutionContext ctx,
		ServerConfig scfg,
		String name,
		String host
	) {
		super(ctx, scfg);
		this.name = name;
		this.host = host;
	}
	
	private static String encode(String s) {
		try {
			return URLEncoder.encode(
				s,
				StandardCharsets.UTF_8.name()
			).replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			LOGGER.log(Level.WARNING, "Unable to encode '" + s + "'", e);
			return s;
		}
	}

	@Override
	protected void handleResult(BooleanResult result) {
		if (result != null && !result.isValue()) {
			// there should not come back <tt>false</tt> as the implementation
			// of the REST API shows
			setStatus(ConnectionStatus.ERRONEOUS);
		}
		Iterable<CollectorRecord> collectors =
				getContext().getAttribute(CollectorCollection.class);
		CollectorRecord collector =
				DefaultCollectorCollection.get(collectors, name, host);
		if (collector != null) {
			collector.setRestartStatus(RestartStatus.INPROGRESS);
		}
	}

	@Override
	public Request<BooleanResult> createRequest() {
		String encodedName = encode(name);
		String encodedHost = encode(host);
		
		return new RestartCollectorRequest(encodedName + "@" + encodedHost);
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
