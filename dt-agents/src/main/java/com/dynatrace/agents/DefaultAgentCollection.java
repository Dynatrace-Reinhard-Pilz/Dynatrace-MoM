package com.dynatrace.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class DefaultAgentCollection implements AgentCollection {
	
	private final Map<String, AgentInfo> agents =
			new HashMap<String, AgentInfo>();
	
	public void onAgentRemoved(AgentInfo agentInfo) {
		// subclasses may override
	}
	
	public void onAgentAdded(AgentInfo agentInfo) {
		// subclasses may override
	}
	
	public void onAgentUpdated(AgentInfo agentInfo) {
		// subclasses may override
	}
	
	private void removeMissing(Iterable<AgentInfo> agentInfos) {
		Iterator<String> it = null;
		for (it = this.agents.keySet().iterator(); it.hasNext(); ) {
			String agentId = it.next();
			boolean found = false;
			for (AgentInfo agentInfo : agentInfos) {
				if (agentId.equals(agentInfo.getAgentId())) {
					found = true;
				}
			}
			if (!found) {
				AgentInfo agentInfo = get(agentId);
				it.remove();
				onAgentRemoved(agentInfo);
			}
		}
	}
	
	public void addAll(Iterable<AgentInfo> agentInfos) {
		if (agentInfos == null) {
			return;
		}
		synchronized (this.agents) {
			removeMissing(agentInfos);
			for (AgentInfo agentInfo : agentInfos) {
				add(agentInfo);
			}
		}
	}
	
	public void add(AgentInfo agentInfo) {
		if (agentInfo == null) {
			return;
		}
		synchronized (this.agents) {
			if (!this.agents.containsKey(agentInfo.getAgentId())) {
				onAgentAdded(agentInfo);
			} else {
				onAgentUpdated(agentInfo);
			}
			this.agents.put(agentInfo.getAgentId(), agentInfo);
		}
	}
	
	public Collection<AgentInfo> values() {
		synchronized (this) {
			return new ArrayList<AgentInfo>(
				agents.values()
			);
		}
	}
	
	@Override
	public AgentInfo get(String agentId) {
		Objects.requireNonNull(agentId);
		synchronized (this) {
			return agents.get(agentId);
		}
	}
	
	@Override
	public void remove(String agentId) {
		Objects.requireNonNull(agentId);
		synchronized (this) {
			agents.remove(agentId);
		}
	}

	@Override
	public Iterator<AgentInfo> iterator() {
		return values().iterator();
	}
	
	@Override
	public int size() {
		synchronized (agents) {
			return agents.size();
		}
	}
	
}
