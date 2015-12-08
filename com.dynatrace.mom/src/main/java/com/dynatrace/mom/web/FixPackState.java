package com.dynatrace.mom.web;

import java.util.Collection;

import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackStatus;

public class FixPackState {

	private final FixPack fixPack;
	private final FixPackStatus status;
	
	public FixPackState(FixPack fixPack) {
		this.fixPack = fixPack;
		this.status = FixPackStatus.None;
	}
	
	public FixPackState(FixPack fixPack, FixPackStatus status) {
		this.fixPack = fixPack;
		this.status = status;
	}
	
	public static void updateStatus(Collection<FixPackState> states, FixPack fixPack, FixPackStatus status) {
		updateStatus(states, new FixPackState(fixPack, status));
	}
	
	public static void updateStatus(Collection<FixPackState> states, FixPackState fixPackState) {
		if (states == null) {
			return;
		}
		if (fixPackState == null) {
			return;
		}
		synchronized (states) {
			FixPackState storedState = null;
			for (FixPackState state : states) {
				if (state == null) {
					continue;
				}
				if (state.equals(fixPackState)) {
					storedState = state;
					break;
				}
			}
			if (storedState != null) {
				states.remove(storedState);
			}
			synchronized (fixPackState.status) {
				fixPackState.status.notifyAll();
			}
			states.add(fixPackState);
		}
	}
	
	public FixPack getFixPack() {
		return fixPack;
	}
	
	public FixPackStatus getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fixPack == null) ? 0 : fixPack.hashCode());
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
		FixPackState other = (FixPackState) obj;
		if (fixPack == null) {
			if (other.fixPack != null)
				return false;
		} else if (!fixPack.equals(other.fixPack))
			return false;
		return true;
	}
	
	
}
