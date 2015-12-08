package com.dynatrace.reporting;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Iterables;

public class HealthRefresh extends ServerOperation<DashboardReport> {
	
	private static final Logger LOGGER =
			Logger.getLogger(HealthRefresh.class.getName());
	
	public HealthRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	@Override
	protected boolean prepare() {
		if (!super.prepare()) {
			return false;
		}
		HealthDashboardAware hda = getContext().getAttribute(HealthDashboardAware.class);
		if (hda == null) {
			return false;
		}
		if (hda.getHealthDashboardAvailability() != Availability.Available) {
			return false;
		}
		return true;
	}

	@Override
	protected void handleResult(DashboardReport dashboardReport) {
		Collection<Dashlet> dashlets = dashboardReport.getDashlets();
		if (Iterables.isNullOrEmpty(dashlets)) {
			return;
		}
		MeasureAware ma = getAttribute(MeasureAware.class);
		for (Dashlet dashlet : dashlets) {
			if (dashlet == null) {
				continue;
			}
			if (dashlet instanceof ChartDashlet) {
				ChartDashlet chartDashlet = (ChartDashlet) dashlet;
				String dashletName = chartDashlet.getName();
				if (dashletName != null) {
					Collection<Measure> measures = chartDashlet.getMeasures();
					if (measures != null) {
						ma.setMeasures(measures, dashletName);
					}
				}
			}
		}
	}
	
	@Override
	protected void handleException(Throwable exception) {
		if (exception == null) {
			return;
		}
		if (exception instanceof FileNotFoundException) {
			HealthDashboardAware hda = getAttribute(HealthDashboardAware.class);
			if (hda != null) {
				hda.setHealthDashboardAvailability(Availability.Unavailable);
				return;
			}
		}
		super.handleException(exception);
	}

	@Override
	public Request<DashboardReport> createRequest() {
		return new DynatraceServerHealthDashboardRequest();
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
}
