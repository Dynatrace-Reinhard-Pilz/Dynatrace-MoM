package com.dynatrace.collectors.web;

import java.util.ArrayList;

import com.dynatrace.collectors.CollectorCollection;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.web.base.Link;

public class DefaultCollectorsModel implements CollectorsModel {
	
	private final ExecutionContext ctx;
	private final String serverName;
	private final boolean isServerColumnRequired;
	
	public DefaultCollectorsModel(ExecutionContext ctx, String serverName, boolean isServerColumnRequired) {
		this.ctx = ctx;
		this.serverName = serverName;
		this.isServerColumnRequired = isServerColumnRequired;
	}

	@Override
	public String getTitle() {
		return "Collectors - dynaTrace MoM";
	}

	@Override
	public String getDetailInclude() {
		return "/jsp/collectors.jsp";
	}
	
	protected CollectorCollection retrieveCollectors() {
		return ctx.getAttribute(CollectorCollection.class);
	}

	@Override
	public Iterable<CollectorRecord> getCollectors() {
		return retrieveCollectors();
	}

	@Override
	public int getCollectorCount() {
		return retrieveCollectors().size();
	}
	
	@Override
	public Iterable<Link> getBreadCrumbsEx() {
		ArrayList<Link> links = new ArrayList<Link>();
		links.add(new Link("Live", "servers"));
		links.add(new Link("Servers", "servers"));
		links.add(new Link(serverName, "servers/" + serverName));
		links.add(new Link("Collectors", "servers/" + serverName + "/collectors"));
		return links;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public boolean isServerColumnRequired() {
		return isServerColumnRequired;
	}

	@Override
	public ExecutionContext getContext() {
		return ctx;
	}

}
