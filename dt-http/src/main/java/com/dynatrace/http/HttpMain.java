package com.dynatrace.http;

import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.Version;

public class HttpMain {

	public static void main(String[] args) {
		HttpMain httpMain = new HttpMain();
		try {
			httpMain.execute(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute(String[] args) throws Exception {
		for (String arg : args) {
			System.out.println(arg);
		}
		VersionRequest request = new VersionRequest();
		ServerConfig serverConfig = new ServerConfig();
		ConnectionConfig connectionConfig = new ConnectionConfig();
		connectionConfig.setHost("localhost");
		connectionConfig.setPort(8020);
		connectionConfig.setProtocol(Protocol.HTTP);
		serverConfig.setConnectionConfig(connectionConfig);
		Credentials credentials = new Credentials();
		credentials.setUser("admin");
		credentials.setPass("admin");
		serverConfig.setCredentials(credentials);
		HttpResponse<Version> response = request.execute(serverConfig);
		Throwable exception = response.getException();
		if (exception != null) {
			if (exception instanceof UnexpectedResponseCodeException) {
				UnexpectedResponseCodeException urce = (UnexpectedResponseCodeException) exception;
				System.out.println(urce.getServerResponse());
			}
		}
		Version version = response.getData();
		System.out.println(version);
//		if (true) {
//			return;
//		}
//		if ((args == null) || (args.length == 0)) {
//			throw new Exception();
//		}
//		String command = args[0];
	}

}
