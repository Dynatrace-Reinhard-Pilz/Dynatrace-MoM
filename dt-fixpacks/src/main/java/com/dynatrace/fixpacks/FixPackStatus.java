package com.dynatrace.fixpacks;

import java.util.Objects;

import com.dynatrace.utils.Condition;

public enum FixPackStatus {

	None,
	RestartPending,
	Installing,
	Restarting;
	
	public Condition condition(FixPackAware support, FixPack fixPack) {
		return new StatusCondition(support, this, fixPack);
	}
	
	private static class StatusCondition extends Condition {
		
		private final FixPackAware support;
		private final FixPackStatus status;
		private final FixPack fixPack;
		
		public StatusCondition(
				FixPackAware support,
			FixPackStatus status,
			FixPack fixPack
		) {
			Objects.requireNonNull(status);
			Objects.requireNonNull(fixPack);
			Objects.requireNonNull(support);
			this.status = status;
			this.fixPack = fixPack;
			this.support = support;
		}
		
		public Object getLock() {
			return fixPack;
		}

		@Override
		public boolean isMet() {
			return status == support.getFixpackStatus(fixPack);
		}
		
	}
}
