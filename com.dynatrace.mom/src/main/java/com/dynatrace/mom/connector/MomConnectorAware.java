package com.dynatrace.mom.connector;

import com.dynatrace.reporting.Availability;
import com.dynatrace.utils.Version;

public interface MomConnectorAware {

	void setMoMConnectorAvailability(Availability availability);
	Availability getMoMConnectorAvailability();
	Version getMoMConnectorVersion();
	void setMoMConnectorVersion(Version version);
	
}
