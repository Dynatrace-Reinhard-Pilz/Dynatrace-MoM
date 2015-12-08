package com.dynatrace.http;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public abstract class MulitServerOperation<I,T> extends ServerOperation<T> {
	
	public MulitServerOperation(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	protected abstract Iterable<I> getItems();
	protected abstract Request<T> createRequest(I item);
	
	@Override
	protected void handleResult(T data) {
	}
	
	protected abstract void handleResult(I input, T data);
	
	@Override
	public boolean execute() {
		boolean success = true;
		Iterable<I> items = getItems();
		for (I item : items) {
			T result = executeRequest(createRequest(item));
			if (result != null) {
				handleResult(item, result);
			} else {
				success = false;
			}
		}
		return success;
	}
	
	@Override
	public Request<T> createRequest() {
		return null;
	}

}
