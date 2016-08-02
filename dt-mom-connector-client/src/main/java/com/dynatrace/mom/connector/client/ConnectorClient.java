package com.dynatrace.mom.connector.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Logger;

import com.dynatrace.http.Http;
import com.dynatrace.http.HttpClient;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseVerifier;
import com.dynatrace.http.VerificationException;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.model.dashboards.DashboardReference;
import com.dynatrace.mom.connector.model.dashboards.DashboardReferences;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReferences;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReference;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReferences;
import com.dynatrace.utils.SizedIterable;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.files.FileReference;

public class ConnectorClient implements ResponseVerifier {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(ConnectorClient.class.getName());
	
	private static final HttpClient HTTP = Http.client();

	private final ServerConfig serverConfig;
	
	public ConnectorClient(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		HTTP.setResponseVerifier(this);
	}
	

	public Version getVersion() throws IOException {
		ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
		final URL url = connectionConfig.createURL("/mom/version");
		final Credentials credentials = serverConfig.getCredentials();
		HttpResponse<Version> response = HTTP.request(
			url, Method.GET, credentials, Version.class
		);
		Throwable exception = response.getException();
		if (exception != null) {
			if (exception instanceof VerificationException) {
				return Version.UNDEFINED;
			}
		}
		return response.getData();
	}
	
	public SizedIterable<ProfileTemplateReference> getProfileTemplates() throws IOException {
		ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
		final URL url = connectionConfig.createURL("/mom/profiles");
		final Credentials credentials = serverConfig.getCredentials();
		HttpResponse<ProfileTemplateReferences> response = HTTP.request(
			url, Method.GET, credentials, ProfileTemplateReferences.class
		);
		if (response.getException() != null) {
			throw new IOException(response.getException());
		}
		return response.getData();
	}
	
	public void getProfileTemplate(ProfileTemplateReference template, OutputStream out)
		throws IOException
	{
		getXmlFile(template, out);
	}
	
	public SizedIterable<SystemProfileReference> getProfiles() throws IOException {
		ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
		final URL url = connectionConfig.createURL("/mom/profiles");
		final Credentials credentials = serverConfig.getCredentials();
		HttpResponse<SystemProfileReferences> response = HTTP.request(
			url, Method.GET, credentials, SystemProfileReferences.class
		);
		if (response.getException() != null) {
			throw new IOException(response.getException());
		}
		return response.getData();
	}
	
	public void getProfile(SystemProfileReference profile, OutputStream out)
		throws IOException
	{
		getXmlFile(profile, out);
	}
	
	public void getDashboard(DashboardReference dashboard, OutputStream out)
		throws IOException
	{
		getXmlFile(dashboard, out);
	}
	
	public SizedIterable<DashboardReference> getDashboards() throws IOException {
		ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
		final URL url = connectionConfig.createURL("/mom/dashboards");
		final Credentials credentials = serverConfig.getCredentials();
		HttpResponse<DashboardReferences> response = HTTP.request(
			url, Method.GET, credentials, DashboardReferences.class
		);
		return response.getData();
	}
	
	public void getServerConfig(OutputStream out)
		throws IOException
	{
		ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
		final URL url = connectionConfig.createURL("/mom/config/server.config.xml");
		final Credentials credentials = serverConfig.getCredentials();
		HTTP.request(url, Method.GET, credentials, out);
	}
	
	private void getXmlFile(FileReference xmlFile, OutputStream out)
		throws IOException
	{
		final URL url = new URL(xmlFile.getHref());
		final Credentials credentials = serverConfig.getCredentials();
		HTTP.request(url, Method.GET, credentials, out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyResponseHeader(String name, String value) throws VerificationException {
		if (name == null) {
			return;
		}
		if ("Content-Type".equals(name)) {
			if (!"application/xml".equals(value)) {
				throw new VerificationException("Content-Type is not application/xml");
			}
		}
	}
	
}
