package com.dynatrace.sysinfo;

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
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.TempFiles;

public class SysInfoRequest implements Request<SysInfoResult> {
	
	private static final Logger LOGGER =
			Logger.getLogger(SysInfoRequest.class.getName());
	
	static final String ERR_MSG_INVALID_SERVER_CONFIG =
			"Server Config is not valid".intern();
	
	private static final HttpClient HTTPCLIENT = Http.client();
	
	private final String BASE_PATH = "/rest/management/sysinfo/package?exclude=all&include=";
	
	private final String[] DEFAULT_FILE_TYPES = new String[] {
		"configfiles",
		"dashboards",
		"profiles",
		"agentrecords",
		"licensefile",
		"componentproperties"	
	};
	
	private static final File TEMP =
			TempFiles.getTempFolder(SysInfoRequest.class.getSimpleName());
	
	@Override
	public HttpResponse<SysInfoResult> execute(ServerConfig serverConfig) {
		LOGGER.log(Level.FINE, MessageFormat.format(
				"Executing {0} {1}...",
				getClass().getSimpleName(),
				serverConfig
		));
		URL url = null;
		try {
			url = serverConfig.createURL(getPath());
		} catch (MalformedURLException e) {
			return new HttpResponse<SysInfoResult>(Integer.MIN_VALUE);
		}
		
		File tmpSysInfoFile = null;
		FileOutputStream fos;
		try {
			tmpSysInfoFile = new File(TEMP, UUID.randomUUID().toString());
			tmpSysInfoFile.deleteOnExit();
			fos = new FileOutputStream(tmpSysInfoFile);
		} catch (IOException e) {
			return new HttpResponse<SysInfoResult>(Integer.MIN_VALUE, e);
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
			return new HttpResponse<SysInfoResult>(code, e);
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
			return new HttpResponse<SysInfoResult>(
				code,
				new UnexpectedResponseCodeException(ResponseCode.OK, code, serverResponse)
			);
		}
		try {
			fos.close();
		} catch (IOException e) {
			return new HttpResponse<SysInfoResult>(code, e);
		}
		return new HttpResponse<SysInfoResult>(
			code,
			new SysInfoResult(tmpSysInfoFile)
		);
	}
	
	protected String[] getSupportedFileTypes() {
		return DEFAULT_FILE_TYPES;
	}
	
	private String getPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(BASE_PATH);
		String[] fileTypes = getSupportedFileTypes();
		String sep = "";
		for (String fileType : fileTypes) {
			sb.append(sep).append(fileType);
			sep = ",";
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysInfoRequest other = (SysInfoRequest) obj;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		return true;
	}	
}