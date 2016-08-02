package com.dynatrace.integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.dynatrace.utils.Closeables;

public class ServerLauncher {
	
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 8021;
	private static final String SERVER_PROTOCOL = "https";
	
	private static final File DT_HOME = new File("C:\\Program Files\\dynaTrace\\dynaTrace 6.3");
	private static final File DT_SERVER_EXE = new File(DT_HOME, "dtserver.exe");

	public static void main(String[] args) {
		Process process = null;
		ProcessBuilder pb = new ProcessBuilder(DT_SERVER_EXE.getAbsolutePath());
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		while (!isReachable()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.exit(1);
			}
		}
		System.out.println("dynaTrace Server is up and running");
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			System.exit(1);
		}
	}
	
	public static boolean isReachable() {
		URL url = null;
		try {
			url = new URL(SERVER_PROTOCOL, SERVER_HOST, SERVER_PORT, "/rest/management/version");
		} catch (MalformedURLException e) {
			throw new InternalError(e.getMessage());
		}
		URLConnection con = null;
		try {
			con = url.openConnection();
			try (InputStream in = con.getInputStream()) {
				Closeables.copy(in, System.out);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
