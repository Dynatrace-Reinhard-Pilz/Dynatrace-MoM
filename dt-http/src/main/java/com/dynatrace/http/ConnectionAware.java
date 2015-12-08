package com.dynatrace.http;

public interface ConnectionAware {

	ConnectionStatus getConnectionStatus();
	void setConnectionStatus(ConnectionStatus status);
}
