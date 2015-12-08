package com.dynatrace.mom.web;

import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.utils.ExecutionContext;

public class CollectorModel extends ServerModel {

	private CollectorRecord collector = null;
	
	public CollectorModel(ExecutionContext ctx) {
		super(ctx);
	}
	
	public void setCollector(CollectorRecord collector) {
		this.collector = collector;
	}
	
	public CollectorRecord getCollector() {
		return collector;
	}
}
