package com.dynatrace.http.ng;

public interface HttpResponseConsumer<T> {

	void onResponseStatus(int status);
	void onResponse(T response);
	void onError(HttpError error);
	void onException(Throwable throwable);
	
}
