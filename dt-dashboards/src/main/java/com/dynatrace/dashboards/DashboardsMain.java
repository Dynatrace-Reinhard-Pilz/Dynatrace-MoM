package com.dynatrace.dashboards;

import java.io.IOException;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Protocol;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.Logging;

public class DashboardsMain {
	
	private String user = "admin";
	private String pass = "admin";
	private String host = "localhost";
	private int port = 8020;

	public static void main(String[] args) {
		Logging.init();
		DashboardsMain main = new DashboardsMain();
		try {
			main.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void execute() throws IOException {
		ConnectionConfig connectionConfig =
				new ConnectionConfig(Protocol.HTTP, host, port);
		Credentials credentials = new Credentials(user, pass);
		ServerConfig serverConfig = new ServerConfig(connectionConfig, credentials);
		DashboardsRequest request = new DashboardsRequest();
		HttpResponse<Dashboards> response =	request.execute(serverConfig);
		Dashboards dashboards = response.getData();
		for (Dashboard dashboard : dashboards.getDashboards()) {
			System.out.println(dashboard.getId());
		}
	}

}
