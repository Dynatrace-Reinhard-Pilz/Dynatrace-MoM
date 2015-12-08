package com.dynatrace.profiles;

import java.util.Objects;

import org.w3c.dom.Element;

import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.profiles.metainfo.Metaable;

public class AgentGroup implements Metaable {

	private final Element element;
	
	public AgentGroup(Element element) {
		Objects.requireNonNull(element);
		this.element = element;
	}
	
	public String getDescription() {
		return element.getAttribute("description");
	}
	
	@Override
	public MetaInfo getMetaInfo() {
		String description = getDescription();
		if (description == null) {
			return null;
		}
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
	
}
