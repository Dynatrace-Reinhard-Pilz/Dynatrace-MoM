package com.dynatrace.monitors.license.usage;

import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.profiles.metainfo.Metaable;

public final class MetaAgentGroup implements Metaable {

	private final String id;
	private final String description;
	
	public MetaAgentGroup(String id, String description) {
		this.id = id;
		this.description = description;
	}

	@Override
	public MetaInfo getMetaInfo() {
		return MetaInfo.parse(description);
	}

	@Override
	public String getMetaInfo(String key) {
		MetaInfo metaInfo = getMetaInfo();
		if (metaInfo == null) {
			return null;
		}
		return metaInfo.get(key);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MetaAgentGroup other = (MetaAgentGroup) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return id;
	}
	

}
