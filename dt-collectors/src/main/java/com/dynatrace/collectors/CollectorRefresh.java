package com.dynatrace.collectors;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class CollectorRefresh extends ServerOperation<CollectorInfos> {
	
	private static final Logger LOGGER =
			Logger.getLogger(CollectorRefresh.class.getName());
	
	public CollectorRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	@Override
	protected void handleResult(CollectorInfos collectorInfos) {
		CollectorCollection collectors =
				getAttribute(CollectorCollection.class);
		if (collectors != null) {
			collectors.addAll(collectorInfos);
		}
	}

	@Override
	public Request<CollectorInfos> createRequest() {
		return new CollectorsRequest();
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
