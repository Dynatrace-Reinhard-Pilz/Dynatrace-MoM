package com.dynatrace.mom.rest;

import java.util.Objects;

import com.dynatrace.utils.Strings;

public final class PrefixedHrefGenerator implements HrefGenerator {
	
	private static final String SLASH = "/";
	
	private final String prefix;
	
	public PrefixedHrefGenerator(final String prefix) {
		Objects.requireNonNull(prefix);
		this.prefix = prefix;
	}

	@Override
	public final String getHref(final String value) {
		if (Strings.isNullOrEmpty(value)) {
			if (prefix.endsWith(SLASH)) {
				return prefix.substring(1);
			}
			return prefix;
		}
		if (prefix.endsWith(SLASH)) {
			if (value.startsWith(SLASH)) {
				return prefix + value.substring(1);
			} else {
				return prefix + value;
			}
		} else {
			if (value.startsWith(SLASH)) {
				return prefix + value;
			} else {
				return prefix + SLASH + value;
			}
		}
	}
}
