package com.dynatrace.mom.connector.client;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.http.Protocol;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.utils.Batch;
import com.dynatrace.utils.Version;


public class ConnectorClientTest {
	
	private static final ServerConfig SERVER_CONFIG = new ServerConfig(
		new ConnectionConfig(
			Protocol.HTTPS, "localhost", 8021
		),
		new Credentials("admin", "admin")
	);
	
	private static final ConnectorClient CLIENT =
			new ConnectorClient(SERVER_CONFIG);

	@Test
	public void testGetVersion() throws Exception {
		Version version = CLIENT.getVersion();
		Assert.assertNotNull(version);
		Assert.assertNotEquals(Version.UNDEFINED, version);
	}
	
	@Test
	public void testGetProfiles() throws Exception {
		Batch<SystemProfileReference> profiles = CLIENT.getProfiles();
		for (SystemProfileReference profile : profiles) {
			long size = profile.getSize();
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				CLIENT.getProfile(profile, out);
				Assert.assertEquals(size, out.toByteArray().length);
			}
		}
	}
	
}
