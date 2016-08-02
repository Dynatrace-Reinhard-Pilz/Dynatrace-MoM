package com.dynatrace.profiles.metainfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Logger;

import com.dynatrace.utils.Iterables;

public final class MetaInfo {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(MetaInfo.class.getName());
	
	private final Map<String, String> metaInfos = new HashMap<>();
	
	public boolean contains(String key) {
		Objects.requireNonNull(key);
		return metaInfos.containsKey(key);
	}
	
	public static MetaInfo merge(MetaInfo... metaInfos) {
		if (Iterables.isNullOrEmpty(metaInfos)) {
			return new MetaInfo();
		}
		MetaInfo mergedMetaInfo = new MetaInfo();
		for (MetaInfo metaInfo : metaInfos) {
			mergedMetaInfo.add(metaInfo);
		}
		return mergedMetaInfo;
	}
	
	private void add(MetaInfo metaInfo) {
		if (metaInfo == null) {
			return;
		}
		for (Entry<String, String> entry : metaInfo.metaInfos.entrySet()) {
			String key = entry.getKey();
			if (key == null) {
				continue;
			}
			String value = entry.getValue();
			if (value == null) {
				continue;
			}
			metaInfos.put(key, value);
		}
	}
	
	public String get(String key) {
		Objects.requireNonNull(key);
		return metaInfos.get(key);
	}
	
	
	public Iterable<String> keys() {
		return new ArrayList<String>(metaInfos.keySet());
	}
	
	public int size() {
		return metaInfos.size();
	}
	
	public boolean isEmpty() {
		return metaInfos.isEmpty();
	}
	
	private MetaInfo() {
	}
	
	private MetaInfo(String s) {
		if (s == null) {
			return;
		}
		String text = s;
		int metaIdx = text.indexOf("{meta");
		while (metaIdx >= 0) {
			text = text.substring(metaIdx + "{meta".length());
			int closeIdx = text.indexOf('}');
			if (closeIdx < 0) {
				return;
			}
			String subText = text.substring(0, closeIdx);
			add(subText);
			text = text.substring(closeIdx + 1);
			metaIdx = text.indexOf("{meta");
		}
		return;
	}

	public static MetaInfo parse(String s) {
		return new MetaInfo(s);
	}
	
	private void add(String s) {
		if (s == null) {
			return;
		}
		String text = s.trim();
		int idxEq = text.indexOf('=');
		if (idxEq < 0) {
			return;
		}
		String name = text.substring(0, idxEq).trim();
		if (name.isEmpty()) {
			return;
		}
		if (containsWhiteSpace(name)) {
			return;
		}
		String value = text.substring(idxEq + 1).trim();
		if (value.isEmpty()) {
			return;
		}
		if (!value.startsWith("\"")) {
			return;
		}
		if (!value.endsWith("\"")) {
			return;
		}
		value = value.substring(1, value.length() - 1).trim();
		if (value.isEmpty()) {
			return;
		}
		metaInfos.put(name, value);
	}
	
	private static boolean containsWhiteSpace(String s){
		if (s == null) {
			return false;
		}
        for(int i = 0; i < s.length(); i++) {
        	if (Character.isWhitespace(s.charAt(i))) {
        		return true;
        	}
        }
	    return false;
	}	
}
