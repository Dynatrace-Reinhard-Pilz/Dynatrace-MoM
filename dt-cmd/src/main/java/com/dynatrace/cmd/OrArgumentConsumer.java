package com.dynatrace.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class OrArgumentConsumer extends AbstractArgumentConsumer {

	private final Collection<ArgumentConsumer> alternatives =
			new ArrayList<ArgumentConsumer>();
	
	public OrArgumentConsumer(ArgumentConsumer... alternatives) {
		for (ArgumentConsumer alternative : alternatives) {
			if (alternative == null) {
				throw new IllegalArgumentException(
					"no alternative is allowed to be null"
				);
			}
			this.alternatives.add(alternative);
		}
	}
	
	public boolean matches(String arg) {
		Objects.requireNonNull(arg);
		for (ArgumentConsumer alternative : alternatives) {
			if (alternative == null) {
				continue;
			}
			if (alternative.matches(arg)) {
				return true;
			}
		}
		return false;
	}

}
