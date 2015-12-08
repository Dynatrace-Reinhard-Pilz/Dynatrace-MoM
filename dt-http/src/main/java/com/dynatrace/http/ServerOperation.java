package com.dynatrace.http;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public abstract class ServerOperation<T> extends AbstractServerOperation {
			
	public ServerOperation(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	public abstract Request<T> createRequest();
	protected abstract void handleResult(T data);
	protected abstract Logger logger();
	
	@Override
	public void log(Level level, String message) {
		logger().log(level, message);
	}
	
	@Override
	public void log(Level level, String message, Throwable throwable) {
		logger().log(level, message, throwable);
	}
	
	@Override
	public void log(Level level, String msg, Object...params) {
		logger().log(level, msg, params);
	}
	
	protected T executeRequest(Request<T> request) {
		try {
			HttpResponse<T> httpResponse = request.execute(getServerConfig());
			Throwable exception = httpResponse.getException();
			if (exception != null) {
				if (exception instanceof NoRouteToHostException) {
					setStatus(ConnectionStatus.UNREACHABLE);
					return null;
				} else if (exception instanceof ConnectException) {
					setStatus(ConnectionStatus.OFFLINE);
					return null;
				} else if (exception instanceof SocketException) {
					setStatus(ConnectionStatus.OFFLINE);
					return null;
				} else {
					handleException(exception);
					return null;
				}
			}
			if (Thread.currentThread().isInterrupted()) {
				return null;
			}
			T data = httpResponse.getData();
			if (data != null) {
				setStatus(getDefaultConnectionStatus());
				handleResult(data);
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	protected ConnectionStatus getDefaultConnectionStatus() {
		return ConnectionStatus.ONLINE;
	}
	
	protected void handleException(Throwable exception) {
		setStatus(ConnectionStatus.ERRONEOUS);
		ServerConfig serverConfig = getServerConfig();
		ConnectionConfig connConfig = serverConfig.getConnectionConfig();
		logger().log(Level.SEVERE, ConnectionConfig.toString(connConfig) + " Server Operation failed", exception);
	}
	
	@Override
	public boolean execute() {
//		logger().log(Level.INFO, "Executing " + this.getClass().getSimpleName());
		return (executeRequest(createRequest()) != null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		Request<T> request = createRequest();
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		result = prime * result + ((getServerConfig() == null) ? 0 : getServerConfig().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerOperation<?> other = (ServerOperation<?>) obj;
		Request<T> request = createRequest();
		Request<?> otherRequest = other.createRequest();
		if (request == null) {
			if (otherRequest != null)
				return false;
		} else if (!request.equals(otherRequest))
			return false;
		if (getServerConfig() == null) {
			if (other.getServerConfig() != null)
				return false;
		} else if (!getServerConfig().equals(other.getServerConfig()))
			return false;
		return true;
	}
	
	

}
