package com.dynatrace.license;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class LicenseRefresh extends ServerOperation<LicenseInfo> {
	
	private static final Logger LOGGER =
			Logger.getLogger(LicenseRefresh.class.getName());
	
	public LicenseRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	protected void handleResult(LicenseInfo licenseInfo) {
		getAttribute(LicenseAware.class).setLicenseInfo(licenseInfo);
	}

	@Override
	public Request<LicenseInfo> createRequest() {
		return new LicenseRequest();
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
