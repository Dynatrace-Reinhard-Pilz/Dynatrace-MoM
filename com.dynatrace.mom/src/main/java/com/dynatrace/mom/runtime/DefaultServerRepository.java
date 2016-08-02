package com.dynatrace.mom.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.agents.AgentInfo;
import com.dynatrace.agents.AgentInfos;
import com.dynatrace.mom.runtime.components.ServerContext;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Iterables;

@XmlRootElement(name = DefaultServerRepository.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class DefaultServerRepository implements ServerRepository {
	
	private static final Logger LOGGER = Logger.getLogger(
		DefaultServerRepository.class.getName()
	);
	
	static final String TAG = "servers";
	
	private final Map<String, ServerContext> serverContexts =
			new HashMap<String, ServerContext>();
	
	private ExecutionContext ctx;
	
	public DefaultServerRepository(ExecutionContext context) {
		this.ctx = context;
	}

	public DefaultServerRepository() {
	}
	
	public DefaultServerRepository(BaseServerRepository repository, ExecutionContext ctx) {
		this.ctx = ctx;
		decrypt();
		setServerRecords(repository.getServerRecords());
	}
	
	@Override
	public void add(final ServerRecord serverRecord) {
		Objects.requireNonNull(serverRecord);
		String serverName = serverRecord.getName();
		synchronized (serverContexts) {
			ServerContext serverContext = serverContexts.get(serverName);
			if (serverContext != null) {
				LOGGER.log(Level.WARNING, "A Server with name " + serverName + " is already registered.");
				return;
			}
			LOGGER.log(Level.INFO, "dynaTrace Server '" + serverName + "' discovered.");
			serverContexts.put(serverName, new ServerContext(serverRecord, ctx));
		}
	}

	@XmlElementRef(name = ServerRecord.TAG, type = ServerRecord.class)
	@Override
	public final Collection<ServerRecord> getServerRecords() {
		synchronized (serverContexts) {
			final ArrayList<ServerRecord> serverRecords = new ArrayList<ServerRecord>(serverContexts.size());
			for (ServerContext serverAccessor : serverContexts.values()) {
				if (serverAccessor == null) {
					continue;
				}
				serverRecords.add(serverAccessor.getServerRecord());
			}
			return serverRecords;
		}
	}
	
	public final void setServerRecords(final Collection<ServerRecord> serverRecords) {
		synchronized (serverContexts) {
			for (ServerContext serverAccessor : serverContexts.values()) {
				if (serverAccessor == null) {
					continue;
				}
				serverAccessor.close();
			}
			serverContexts.clear();
			if (!Iterables.isNullOrEmpty(serverRecords)) {
				for (ServerRecord serverRecord : serverRecords) {
					if (serverRecord == null) {
						continue;
					}
					add(serverRecord);
				}
			}
		}
	}
	
	@Override
	public final Collection<ServerContext> getServerContexts() {
		synchronized (serverContexts) {
			return new ArrayList<ServerContext>(serverContexts.values());
		}
	}

	@Override
	public final ServerRecord get(final String serverName) {
		Objects.requireNonNull(serverName);
		synchronized (serverContexts) {
			final ServerContext serverAccessor = serverContexts.get(serverName);
			if (serverAccessor == null) {
				return null;
			}
			return serverAccessor.getServerRecord();
		}
	}

	@Override
	public ServerContext getServer(String serverName) {
		Objects.requireNonNull(serverName);
		synchronized (serverContexts) {
			return serverContexts.get(serverName);
		}
	}

	@Override
	public final void remove(final String serverName) {
		Objects.requireNonNull(serverName);
		synchronized (serverContexts) {
			serverContexts.remove(serverName);
		}
	}

	@Override
	public final void remove(final ServerRecord server) {
		Objects.requireNonNull(server);
		final String serverName = server.getName();
		synchronized (serverContexts) {
			serverContexts.remove(serverName);
		}
	}

	@Override
	public final AgentInfos getAgents() {
		final AgentInfos agentsResult = new AgentInfos();
		final Collection<AgentInfo> agentInfos = new ArrayList<AgentInfo>();
		synchronized (serverContexts) {
			for (ServerContext serverAccessor : serverContexts.values()) {
				if (serverAccessor == null) {
					continue;
				}
				ServerRecord serverRecord = serverAccessor.getServerRecord();
				if (serverRecord == null) {
					continue;
				}
				for (AgentInfo agentInfo : serverRecord.getAgents()) {
					if (agentInfo == null) {
						continue;
					}
					agentInfos.add(agentInfo);
				}
				agentsResult.setAgents(agentInfos);
			}
		}
		return agentsResult;
	}

	@Override
	public int size() {
		return serverContexts.size();
	}
	
	@Override
	public void close() {
		LOGGER.info("Shutting down Server Repository");
		if (Iterables.isNullOrEmpty(serverContexts)) {
			return;
		}
		ArrayList<ServerContext> contexts = new ArrayList<>();
		synchronized (serverContexts) {
			contexts.addAll(serverContexts.values());
		}
		CountDownLatch latch = new CountDownLatch(contexts.size());
		
		for (ServerContext serverContext : contexts) {
			ShutdownThread thread = new ShutdownThread(serverContext, latch);
			thread.start();
		}
		try {
			latch.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}
	
	private static class ShutdownThread extends Thread {
		private final ServerContext context;
		private final CountDownLatch latch;
		
		public ShutdownThread(ServerContext context, CountDownLatch latch) {
			super(ShutdownThread.class.getSimpleName() + " - " + context.getName());
			this.context = context;
			this.latch = latch;
			setDaemon(true);
		}
		
		@Override
		public void run() {
			context.close();
			latch.countDown();
		}
	}

	@Override
	public boolean rename(ServerRecord serverRecord, String name) {
		synchronized (serverContexts) {
			if (name == null) {
				return false;
			}
			if (serverRecord == null) {
				return false;
			}
			String oldName = serverRecord.getName();
			if (oldName == null) {
				return false;
			}
			if (oldName.equals(name)) {
				return false;
			}
			if (serverContexts.containsKey(name)) {
				return false;
			}
			ServerContext serverContext = serverContexts.get(oldName);
			if (serverContext == null) {
				return false;
			}
			serverContexts.remove(oldName);
			serverContext.rename(name);
			serverContexts.put(name, serverContext);
			LOGGER.log(Level.INFO, "dynaTrace Server '" + oldName + "' has been renamed to '" + name + "'");
			return true;
		}
	}

	@Override
	public void encrypt() {
		Collection<ServerRecord> serverRecords = getServerRecords();
		if (serverRecords == null) {
			return;
		}
		for (ServerRecord serverRecord : serverRecords) {
			if (serverRecord == null) {
				continue;
			}
			serverRecord.encrypt();
		}
	}

	@Override
	public void decrypt() {
		Collection<ServerRecord> serverRecords = getServerRecords();
		if (serverRecords == null) {
			return;
		}
		for (ServerRecord serverRecord : serverRecords) {
			if (serverRecord == null) {
				continue;
			}
			serverRecord.decrypt();
		}
	}

}
