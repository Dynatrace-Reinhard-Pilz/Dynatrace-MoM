package com.dynatrace.mom.connector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.Http;
import com.dynatrace.http.HttpClient;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.UnexpectedResponseCodeException;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.TempFiles;

public class RefreshSelfMonitoringRequest implements Request<SystemProfile> {
	
	private static final Logger LOGGER =
			Logger.getLogger(RefreshSelfMonitoringRequest.class.getName());
	
	private static final File TEMP = TempFiles.getTempFolder(
		RefreshSelfMonitoringRequest.class.getSimpleName()
	);
	
	private static final HttpClient HTTPCLIENT = Http.client();
	
	private final String BASE_PATH =
		"/profiles/dynaTrace%20Self-Monitoring.profile.xml";

	@Override
	public HttpResponse<SystemProfile> execute(ServerConfig serverConfig) {
		LOGGER.log(Level.FINE, MessageFormat.format(
				"Executing {0} {1}...",
				getClass().getSimpleName(),
				serverConfig
		));
		URL url = null;
		try {
			url = serverConfig.createURL(BASE_PATH);
		} catch (MalformedURLException e) {
			return new HttpResponse<SystemProfile>(Integer.MIN_VALUE);
		}
		
		File tmpSysInfoFile = null;
		FileOutputStream fos;
		try {
			tmpSysInfoFile = new File(TEMP, UUID.randomUUID().toString());
			tmpSysInfoFile.mkdirs();
			tmpSysInfoFile.deleteOnExit();
			tmpSysInfoFile = new File(tmpSysInfoFile, "dynaTrace Self-Monitoring.profile.xml");
			tmpSysInfoFile.deleteOnExit();
			fos = new FileOutputStream(tmpSysInfoFile);
		} catch (IOException e) {
			return new HttpResponse<SystemProfile>(Integer.MIN_VALUE, e);
		}

		int code = Integer.MIN_VALUE;
		
		try {
			code = HTTPCLIENT.request(
				url,
				Method.GET,
				serverConfig.getCredentials(),
				fos
			);
		} catch (IOException e) {
			return new HttpResponse<SystemProfile>(code, e);
		}
		if (!ResponseCode.OK.matches(code)) {
			try {
				fos.close();
			} catch (IOException e) {
				// ignore
			}
			String serverResponse = null;
			try (
				InputStream in = new FileInputStream(tmpSysInfoFile);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			) {
				Closeables.copy(in, out);
				serverResponse = new String(out.toByteArray());
			} catch (IOException ioe) {
				// ignore
			}
			return new HttpResponse<SystemProfile>(
				code,
				new UnexpectedResponseCodeException(
					ResponseCode.OK,
					code,
					serverResponse,
					BASE_PATH
				)
			);
		}
		try {
			fos.close();
		} catch (IOException e) {
			return new HttpResponse<SystemProfile>(code, e);
		}
		SystemProfile systemProfile = new SystemProfile(tmpSysInfoFile);
		return new HttpResponse<SystemProfile>(
			code,
			systemProfile
		);
	}

}
