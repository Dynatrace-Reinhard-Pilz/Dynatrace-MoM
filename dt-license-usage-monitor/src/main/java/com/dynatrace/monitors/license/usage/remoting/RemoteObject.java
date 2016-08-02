package com.dynatrace.monitors.license.usage.remoting;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.AbstractRequest;

public abstract class RemoteObject<REF, DATA> {

	private final ServerConfig serverConfig;
	private final REF ref;
	private DATA data = null;
	
	public RemoteObject(REF ref, ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.ref = ref;
	}
	
	protected abstract AbstractRequest<DATA> createRequest(REF ref);
	
	protected final DATA getData() {
		if (data != null) {
			return data;
		}
		AbstractRequest<DATA> request = createRequest(ref);
		HttpResponse<DATA> response = request.execute(serverConfig);
		return response.getData();
	}
	
	protected final REF getRef() {
		return ref;
	}
	
	protected final ServerConfig getServerConfig() {
		return serverConfig;
	}
}
