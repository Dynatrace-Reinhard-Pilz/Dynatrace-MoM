package com.dynatrace.mom.web;

import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.mom.runtime.components.FixPackActions;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.ExecutionContext;

public class ServerModel extends Model {

	private ServerRecord serverRecord = null;
	private FixPackActions fixPackActions = new FixPackActions();
	private FixPack fixPack;
	
	public ServerModel(ExecutionContext ctx) {
		super(ctx);
	}
	
	public ServerRecord getServerRecord() {
		return serverRecord;
	}
	
	public final void setServerRecord(final ServerRecord serverRecord) {
		this.serverRecord = serverRecord;
	}
	
	@Override
	public String getServerName() {
		return serverRecord.name();
	}
	
	public void setFixPackActions(FixPackActions fixPackActions) {
		this.fixPackActions = fixPackActions;
	}
	
	public FixPackActions getFixPackActions() {
		return fixPackActions;
	}
	
	public FixPack getFixPack() {
		return fixPack;
	}
	
	public void setFixPack(FixPack fixPack) {
		this.fixPack = fixPack;
	}
	
}
