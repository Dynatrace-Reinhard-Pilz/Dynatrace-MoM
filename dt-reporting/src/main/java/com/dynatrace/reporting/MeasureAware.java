package com.dynatrace.reporting;

import java.util.Collection;

public interface MeasureAware {

	void setMeasures(Collection<Measure> measures, String dashletName);
	
}
