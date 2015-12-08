package com.dynatrace.sysinfo;


public enum ComponentType {
	server,
	collector,
	agent,
	unknown;
	
	public static ComponentType fromString(String value) {
		if (value == null) {
			return null;
		}
		if (value.equals("Collector")) {
			return ComponentType.collector;
		} else if (value.equals("Server")) {
			return ComponentType.server;
		} else if (value.equals("Agent")) {
			return ComponentType.agent;
		}
		return unknown;
	}
	
}
