package com.dynatrace.http;

import java.io.File;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

public abstract class AbstractServerOperation
implements Runnable, ExecutionContext {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(AbstractServerOperation.class.getName());
			
	private final ServerConfig serverConfig;
	private final ExecutionContext ctx;
	
	public AbstractServerOperation(ExecutionContext ctx, ServerConfig scfg) {
		Objects.requireNonNull(scfg);
		Objects.requireNonNull(ctx);
		this.serverConfig = scfg;
		this.ctx = ctx;
	}
	
	public ExecutionContext getContext() {
		return ctx;
	}
	
	@Override
	public <T, A extends T> void setAttribute(Class<T> c, A attribute) {
		ctx.setAttribute(c, attribute);
	}
	
	public <E> E getAttribute(Class<E> c) {
		return ctx.getAttribute(c);
	}
	
	public URL createURL(String path) throws MalformedURLException {
		return getConnectionConfig().createURL(path);
	}
	
	public ConnectionConfig getConnectionConfig() {
		return serverConfig.getConnectionConfig();
	}
	
	public ServerConfig getServerConfig() {
		return serverConfig;
	}
	
	public Credentials getCredentials() {
		return serverConfig.getCredentials();
	}
	
	public final Request<Version> createVersionRequest() {
		return new VersionRequest();
	}
	
	protected void setStatus(ConnectionStatus status) {
		ConnectionAware ca = ctx.getAttribute(ConnectionAware.class);
		if (ca != null) {
			ca.setConnectionStatus(status);
		}
	}
	
	protected ConnectionStatus getStatus() {
		ConnectionAware ca = ctx.getAttribute(ConnectionAware.class);
		return ca.getConnectionStatus();
	}
	
	public abstract boolean execute();
	protected abstract Logger logger();
	
	public void onSuccess() {
		// to be overridden
	}
	
	public void onFailure() {
		// to be overridden
	}
	
	@Override
	public void run() {
		if (!prepare()) {
			return;
		}
		if (execute()) {
			onSuccess();
		} else {
			onFailure();
		}
	}
	
	protected boolean prepare() {
		ConnectionStatus connectionStatus =
				ctx.getAttribute(ConnectionAware.class).getConnectionStatus();
		switch (connectionStatus) {
		case ONLINE:
			return true;
		case ERRONEOUS:
		case OFFLINE:
		case RESTARTING:
		case RESTARTSCHEDULED:
		case UNREACHABLE:
			logger().log(Level.FINER, "Execution for " + getConnectionConfig().getHost() + " discarded because status is " + connectionStatus);
			return false;
		}
		return true;
	}
	
	protected Version requestVersion() {
		HttpResponse<Version> response = createVersionRequest().execute(
			serverConfig
		);
		Throwable exception = response.getException();
		if (exception != null) {
			if (exception instanceof NoRouteToHostException) {
				setStatus(ConnectionStatus.UNREACHABLE);
				return null;
			} else if (exception instanceof ConnectException) {
				setStatus(ConnectionStatus.OFFLINE);
				return null;
			} else if (exception instanceof SocketException) {
				setStatus(ConnectionStatus.OFFLINE);
				return null;
			} else {
				setStatus(ConnectionStatus.ERRONEOUS);
				return null;
			}
		}
		return response.getData();
	}
	
	protected Version updateVersion() {
		Version version = requestVersion();
		if (version != null) {
			Versionable versionable = getAttribute(Versionable.class);
			versionable.updateVersion(version);
		}
		return version;
	}

	@Override
	public void execute(Runnable command) {
		ctx.execute(command);
	}

	@Override
	public void setAttribute(Object attribute) {
		ctx.setAttribute(attribute);
	}

	@Override
	public <T> T getAttribute(String name) {
		return ctx.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object attribute) {
		ctx.setAttribute(attribute);
	}

	@Override
	public void removeAttribute(String name) {
		ctx.removeAttribute(name);
	}

	@Override
	public String getContextPath() {
		return ctx.getContextPath();
	}

	@Override
	public void log(Level level, String message) {
		logger().log(level, message);
	}

	@Override
	public void log(Level level, String message, Throwable throwable) {
		logger().log(level, message, throwable);
	}

	@Override
	public void log(Level level, String msg, Object... params) {
		logger().log(level, msg, params);
	}

	@Override
	public File getStorageFolder() {
		return ctx.getStorageFolder();
	}

	@Override
	public File getStorageSubFolder(String attribute, String folderName) {
		return ctx.getStorageSubFolder(attribute, folderName);
	}
	
	@Override
	public ExecutionContext getContext(String id) {
		return ctx;
	}
	
	@Override
	public void register(ExecutionContext ctx, String id) {
		ctx.register(ctx, id);
	}
	
	@Override
	public void unregister(String id) {
		ctx.unregister(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serverConfig == null) ? 0 : serverConfig.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractServerOperation other = (AbstractServerOperation) obj;
		if (serverConfig == null) {
			if (other.serverConfig != null)
				return false;
		} else if (!serverConfig.equals(other.serverConfig))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
