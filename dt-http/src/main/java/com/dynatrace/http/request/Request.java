package com.dynatrace.http.request;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.config.ServerConfig;

public interface Request<T> {

	HttpResponse<T> execute(ServerConfig srvConf);
	int hashCode();
	boolean equals(Object o);
	
}
