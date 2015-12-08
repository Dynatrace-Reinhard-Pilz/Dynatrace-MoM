package com.dynatrace.mom.web;

import java.util.ArrayList;
import java.util.Collection;

import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.ExecutionContext;

public class FixPackModel extends Model {

	private final FixPack fixPack;
	private Collection<ServerRecord> servers = null;
	
	public FixPackModel(final FixPack fixPack, final Model parent, ExecutionContext ctx) {
		super(parent, ctx);
		this.fixPack = fixPack;
	}
	
	public FixPackModel(final FixPack fixPack, ExecutionContext ctx) {
		super(ctx);
		this.fixPack = fixPack;
	}
	
	public final FixPack getFixPack() {
		return fixPack;
	}
	
	public final void addServer(final ServerRecord serverRecord) {
		if (servers == null) {
			servers = new ArrayList<ServerRecord>();
		}
		servers.add(serverRecord);
	}
	
	public final Collection<ServerRecord> getServers() {
		return servers;
	}

	@Override
	public String getServerName() {
		return null;
	}
	
}
