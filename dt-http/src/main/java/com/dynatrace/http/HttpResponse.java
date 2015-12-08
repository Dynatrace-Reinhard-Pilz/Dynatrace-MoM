package com.dynatrace.http;

/**
 * A generic Object holding the results of an HTTP request
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> the type of object deserialized from the HTTP response body
 */
public class HttpResponse<T> {
	
	/**
	 * the HTTP response code
	 */
	private final int status;
	
	/**
	 * the deserialized HTTP response body
	 */
	private final T data;
	
	/**
	 * an {@link Throwable} which might have occurred when sending the HTTP
	 * request
	 */
	private final Throwable exception;
	
	/**
	 * c'tor
	 * 
	 * @param status the HTTP response code
	 * @param data the object deserialized from the HTTP response body or
	 * 		{@code null} if there was no response body to deserialize
	 * @param exception in case the HTTP request failed a potential
	 * 		{@link Throwable} serving as information about why it failed
	 */
	public HttpResponse(int status, T data, Throwable exception) {
		this.status = status;
		this.data = data;
		this.exception = exception;
	}
	
	public HttpResponse(int status) {
		this(status, null, null);
	}
	
	public HttpResponse(int status, T data) {
		this(status, data, null);
	}
	
	public HttpResponse(int status, Throwable exception) {
		this(status, null, exception);
	}
	
	/**
	 * @return the HTTP response code returned by the HTTP server
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @return an object deserialized from the HTTP response body or
	 * 		{@code null} in case the response body did not represent serialized
	 * 		data.
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * @return a {@link Throwable} which led to the HTTP request to fail or
	 * 		{@code null} if the HTTP request was successful
	 */
	public Throwable getException() {
		return exception;
	}
}
