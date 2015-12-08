package com.dynatrace.reporting;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class DoubleAdapter extends XmlAdapter<Double, Double> {
	
	@Override
	public final Double unmarshal(final Double v) throws Exception {
		return v;
	}
	
	@Override
	public final Double marshal(final Double v) throws Exception {
		return Double.isNaN(v) ? null : v;
	}
}