package com.dynatrace.sysinfo;

import java.util.Objects;

public class ComponentHashKey {
	
	public final ComponentType type;
	public final String host;
	public final String pid;
	
	public ComponentHashKey(ComponentType type, String host, String pid) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(host);
		Objects.requireNonNull(pid);
		this.type = type;
		this.host = host;
		this.pid = pid;
	}
	
	public ComponentType getType() {
		return type;
	}
	
	public String getHost() {
		return host;
	}
	
	public String getPid() {
		return pid;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(type.toString()).append("[").append(pid).append("]@").append(host).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentHashKey other = (ComponentHashKey) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
