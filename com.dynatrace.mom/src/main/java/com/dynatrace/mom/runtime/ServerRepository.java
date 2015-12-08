package com.dynatrace.mom.runtime;

import java.util.Collection;

import com.dynatrace.agents.AgentInfos;
import com.dynatrace.mom.runtime.components.ServerContext;
import com.dynatrace.mom.runtime.components.ServerRecord;


public interface ServerRepository extends BaseServerRepository {
	
	public static final String STORAGE_FILE_NAME = ServerRepository.class.getName() + ".xml";

	void add(final ServerRecord server);
	ServerRecord get(final String serverName);
	ServerContext getServer(String serverName);
	void remove(final String serverName);
	void remove(final ServerRecord server);
	AgentInfos getAgents();
	Collection<ServerContext> getServerContexts();
	int size();
	void close();
	boolean rename(ServerRecord serverRecord, String name);
	
}
