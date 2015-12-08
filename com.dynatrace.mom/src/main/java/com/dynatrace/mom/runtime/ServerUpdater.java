package com.dynatrace.mom.runtime;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.collectors.RestartStatus;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.mom.runtime.components.ServerContext;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.reporting.Availability;

public final class ServerUpdater extends TimerTask {
	
	private static final Logger LOGGER = Logger.getLogger(ServerUpdater.class.getName());
	
	private static final long PERIOD_MS = 40 * 1000;
	private static final long FREQUENT_PERIOD_MS = 10 * 1000;
	
	private final String name = UUID.randomUUID().toString();

	private final Timer timer =
			new Timer(ServerUpdater.class.getSimpleName());
	private final Timer frequentTimer =
			new Timer("Frequent-" + ServerUpdater.class.getSimpleName());
	private final ServerRepository serverRepository;
	
	public ServerUpdater(final ServerRepository repository) {
		this.serverRepository = repository;
		this.run();
		timer.schedule(this, 0, PERIOD_MS);
		this.run();
		frequentTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				final Collection<ServerContext> serverAccessors = serverRepository.getServerContexts();
				for (ServerContext serverAccessor : serverAccessors) {
					if (serverAccessor == null) {
						continue;
					}
					try {
						serverAccessor.refreshVersion();
						if (serverAccessor.getServerRecord().getConnectionStatus() == ConnectionStatus.ONLINE) {
							ServerRecord serverRecord = serverAccessor.getServerRecord();
							for (CollectorRecord collector : serverRecord.getCollectors()) {
								if (collector.getRestartStatus() == RestartStatus.INPROGRESS) {
									serverAccessor.refreshConfigs();
									break;
								}
							}
							for (CollectorRecord collector : serverRecord.getCollectors()) {
								synchronized (collector) {
									if (collector.getRestartStatus() == RestartStatus.REQUIRED) {
//										collector.setRestartStatus(RestartStatus.SCHEDULED);
//										serverAccessor.restartCollector(collector.getName(), collector.getHost());
									}
								}
							}
						}
					} catch (final Throwable t) {
						LOGGER.log(Level.WARNING, "Unable to update Server " + serverAccessor.getName(), t);
					}
				}
			}
			
		}, 0, FREQUENT_PERIOD_MS);
	}
	
	@Override
	public final void run() {
		final Collection<ServerContext> serverContexts = serverRepository.getServerContexts();
		for (ServerContext serverContext : serverContexts) {
			if (serverContext == null) {
				continue;
			}
			try {
				serverContext.refreshVersion();
				if (serverContext.getServerRecord().isOnline()) {
					serverContext.refreshDashboardStatus();
					Availability availability = serverContext.getHealthDashboardAvailability();
					if (availability == Availability.Unavailable) {
						serverContext.setHealthDashboardAvailability(
							Availability.NotYetAvailable
						);
						serverContext.uploadHealthDashboard();
					}
					serverContext.refreshCollectors();
					serverContext.refreshLicense();
					serverContext.refreshProfiles();
					serverContext.refreshDynatraceServerHealthMeasures();
					serverContext.refreshAgents();
					serverContext.refreshConfigs();
					serverContext.refreshIncidentReferences();
					serverContext.refreshRuleBasedIncidents();
					
					ServerRecord serverRecord = serverContext.getServerRecord();
					Iterable<CollectorRecord> collectors = serverRecord.getCollectors();
					for (CollectorRecord collector : collectors) {
						synchronized (collector) {
							if (collector.getRestartStatus() == RestartStatus.REQUIRED) {
//								collector.setRestartStatus(RestartStatus.SCHEDULED);
//								serverAccessor.restartCollector(collector.getName(), collector.getHost());
							}
						}
					}
				}
			} catch (Throwable t) {
				LOGGER.log(Level.WARNING, "Unable to update Server " + serverContext.getName(), t);
			}
		}
	}
	
	public void shutdown() {
		LOGGER.log(Level.INFO, "Shutting down " + ServerUpdater.class.getSimpleName());
		LOGGER.log(Level.INFO, "Cancelling Request Execution Timer " + name);
		timer.cancel();
		frequentTimer.cancel();
	}

}
