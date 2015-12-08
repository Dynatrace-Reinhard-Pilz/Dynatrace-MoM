package com.dynatrace.fixpacks;

import static java.net.HttpURLConnection.HTTP_CREATED;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.AbstractServerOperation;
import com.dynatrace.http.Http;
import com.dynatrace.http.Method;
import com.dynatrace.http.UploadResult;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.ExecutionContext;

public class FixPackUpload extends AbstractServerOperation {
	
	private static final Logger LOGGER =
			Logger.getLogger(FixPackUpload.class.getName());
	
	private final FixPack fixPack;

	public FixPackUpload(ExecutionContext ctx, ServerConfig scfg, FixPack fp) {
		super(ctx, scfg);
		this.fixPack = fp;
	}

	@Override
	public boolean execute() {
		ConnectionConfig connectionConfig = getConnectionConfig();
		Credentials credentials = getCredentials();
		FixPackAware fpa = getAttribute(FixPackAware.class);
		FixPackInstallStatus installStatus = new FixPackInstallStatus();
		installStatus.setInstallStatus(InstallStatus.UPLOADING);
		installStatus.setFixPackVersion(fixPack.getVersion());
		fpa.setFixPackInstallStatus(installStatus);
		fpa.updateFixPackState(fixPack, FixPackStatus.Installing);
		synchronized (fixPack) {
			fixPack.notifyAll();
		}
		
        try (InputStream in = fixPack.openStream()) {
        	LOGGER.log(Level.INFO, "Uploading fix pack " + fixPack + " to " + connectionConfig.getHost());
        	URL url = connectionConfig.createURL("/rest/management/installjobs?activateInstantly=true");
        	UploadResult result = upload(url, credentials, in);
			switch (result.status) {
			case HTTP_CREATED:
	        	LOGGER.log(Level.INFO, "Successfully uploaded fix pack " + fixPack + " to " + connectionConfig.getHost());
	    		installStatus.setInstallStatus(InstallStatus.INSTALLING);
	        	installStatus = waitUntilFinished(new URL(result.headers.get("Location")), credentials);
	    		fpa.updateFixPackState(fixPack, FixPackStatus.RestartPending);
	    		installStatus.setFixPackVersion(fixPack.getVersion());
	    		installStatus.setInstallStatus(InstallStatus.RESTARTREQUIRED);
	    		fpa.setFixPackInstallStatus(installStatus);
				return true;
			default:
				LOGGER.log(Level.WARNING, "Unable to upload fix pack " + fixPack + " to " + connectionConfig.getHost() + ": status code " + result.status);
				return false;
			}
		} catch (IOException e) {
    		fpa.updateFixPackState(fixPack, FixPackStatus.None);
			LOGGER.log(Level.WARNING, "Unable to upload fix pack " + fixPack + " to " + connectionConfig.getHost(), e);
			return false;
		}
	}
	
	private UploadResult upload(
		URL url,
		Credentials credentials,
		InputStream in
	) throws IOException {
		LOGGER.log(Level.INFO, url.toString());
    	return Http.client().upload(url, credentials, "filename", in);
	}
	

	private final FixPackInstallStatus waitUntilFinished(
		URL url, Credentials credentials
	) throws IOException {
    	FixPackInstallStatus status = new FixPackInstallStatus();
    	while (!status.isFinished()) {
    		FixPackInstallStatus curStatus = pollStatus(url, credentials);
//    		LOGGER.info("isFinished: " + curStatus.isFinished());
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
	
	private FixPackInstallStatus pollStatus(URL url, Credentials credentials)
			throws IOException
	{
    	return Http.client().request(
    		url,
    		Method.GET,
    		credentials,
    		FixPackInstallStatus.class
    	).getData();
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
		FixPackUpload other = (FixPackUpload) obj;
		if (getServerConfig() == null) {
			if (other.getServerConfig() != null)
				return false;
		} else if (!getServerConfig().equals(other.getServerConfig()))
			return false;
		return true;
	}	
	

}
