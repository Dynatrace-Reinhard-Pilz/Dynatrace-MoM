package com.dynatrace.fastpacks;

import static java.net.HttpURLConnection.HTTP_CREATED;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.AbstractServerOperation;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.Http;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.UploadResult;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.PermissionDeniedException;
import com.dynatrace.utils.ExecutionContext;

public class FastPackUpload extends AbstractServerOperation {
	
	private static final Logger LOGGER =
			Logger.getLogger(FastPackUpload.class.getName());
	
	private static final String FASTPACK_PATH = "/mom-health-fastpack.dtp";
	
	public FastPackUpload(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	private InputStream getResourceAsStream(String path) {
		return this.getClass().getClassLoader().getResourceAsStream(path);	
	}
	
	protected InputStream openStream() throws IOException {
		return getResourceAsStream(FASTPACK_PATH);
	}
	
	private HttpResponse<FastPackInstallStatus> pollStatus(URL url, Credentials credentials)
			throws IOException
	{
		 return Http.client().request(
			url,
			Method.GET,
			credentials,
			FastPackInstallStatus.class
		);
	}
	
	public FastPackInstallStatus handlePermissionDeniedException(PermissionDeniedException e) {
		return null;
	}
		
	
	private FastPackInstallStatus waitUntilFinished(URL url, Credentials credentials) throws IOException {
		FastPackInstallStatus status = new FastPackInstallStatus();
		while (!status.isFinished()) {
			HttpResponse<FastPackInstallStatus> response = pollStatus(url, credentials);
			Throwable exception = response.getException();
			if (exception instanceof PermissionDeniedException) {
				PermissionDeniedException pde = (PermissionDeniedException) exception;
				FastPackInstallStatus handledStatus = handlePermissionDeniedException(pde);
				if (handledStatus != null) {
					return handledStatus;
				}
				String permission = pde.getPermission();
				if (permission != null) {
					LOGGER.log(Level.SEVERE, "Missing Permission: " + permission);
					return null;
				}
			}
			FastPackInstallStatus curStatus = response.getData();
			if (curStatus != null && !status.equals(curStatus)) {
				status = curStatus;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		return status;
	}

	@Override
	public boolean execute() {
		log(
			Level.FINE,
			"Uploading Fast Pack to " + getConnectionConfig().getHost()
		);
		try (InputStream in = openStream()) {
	        try {
				URL url = createURL("/rest/management/installjobs?activateInstantly=true");
	        	UploadResult result = Http.client().upload(url, getCredentials(), "filename", in);
				switch (result.status) {
				case HTTP_CREATED:
		        	LOGGER.log(Level.FINE, "Successfully uploaded fast pack to " + getConnectionConfig().getHost());
		        	FastPackInstallStatus status = waitUntilFinished(new URL(result.headers.get("Location")), getCredentials());
					return status != null;
				default:
					LOGGER.log(Level.WARNING, "Unable to upload fast pack  to " + getConnectionConfig().getHost() + ": status code " + result.status);
					return false;
				}
			} catch (ConnectException e) {
				setStatus(ConnectionStatus.OFFLINE);
	        	return false;
			} catch (NoRouteToHostException e) {
				setStatus(ConnectionStatus.UNREACHABLE);
	        	return false;
			} catch (PermissionDeniedException e) {
				onPermissionDenied(e);
				setStatus(ConnectionStatus.ERRONEOUS);
	        	return false;
			} catch (IOException e) {
				setStatus(ConnectionStatus.ERRONEOUS);
	        	return false;
			}
		} catch (IOException e1) {
			log(Level.WARNING, "Unable to read local fastpack", e1);
        	return false;
		}
	}
	
	public void onPermissionDenied(PermissionDeniedException exception) {
		
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getServerConfig() == null) ? 0 : getServerConfig().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FastPackUpload other = (FastPackUpload) obj;
		if (getServerConfig() == null) {
			if (other.getServerConfig() != null)
				return false;
		} else if (!getServerConfig().equals(other.getServerConfig()))
			return false;
		return true;
	}	

}
