package com.dynatrace.mom.runtime.components;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.dynatrace.incidents.Incident;
import com.dynatrace.incidents.IncidentSeverity;
import com.dynatrace.mom.PWHAware;

public final class PWHInactiveIncident extends Incident implements PWHAware {
	
	private final AtomicLong incidentStartTime = new AtomicLong(0);

	public PWHInactiveIncident() {
		setId(UUID.randomUUID().toString());
		setMessage(
			"Inactivity for Measurement Tables within Performance Warehouse"
		);
	}
	
	@Override
	public IncidentSeverity getSeverity() {
		return IncidentSeverity.severe;
	}
	
	@Override
	public long getStartTime() {
		return incidentStartTime.get();
	}
	
	@Override
	public long getEndTime() {
		long startTime = getStartTime();
		if (20000 < System.currentTimeMillis() - startTime) {
			return Long.MAX_VALUE;
		}
		return 0;
	}
	
	@Override
	public boolean isConfirmed() {
		return false;
	}

	@Override
	public void setLastModificationTime(long time) {
		this.incidentStartTime.set(time);
	}
}
