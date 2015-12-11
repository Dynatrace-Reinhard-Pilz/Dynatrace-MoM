package com.dynatrace.onboarding.serverconfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.UnexpectedResponseCodeException;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.Unauthorized;
import com.dynatrace.onboarding.config.Debug;
import com.dynatrace.onboarding.dashboards.Dashboard;
import com.dynatrace.onboarding.profiles.Profile;
import com.dynatrace.sysinfo.SysInfoRequest;
import com.dynatrace.sysinfo.SysInfoResult;
import com.dynatrace.utils.Closeables;

public class ServerProperties {
	
	private static final Logger LOGGER =
			Logger.getLogger(ServerProperties.class.getName());
	
	public boolean isPermissionTaskInstalled = false;
	private final File SERVER_FOLDER = prepareServerFolder();
	
	private static File createTempFolder() {
		if (Debug.DEBUG) {
			File tempFolder = new File(ServerProperties.class.getSimpleName() + "-" + UUID.randomUUID().toString());
			if (tempFolder.exists()) {
				Closeables.purge(tempFolder);
			}
			LOGGER.log(Level.FINEST, "Working Folder: " + tempFolder.getAbsolutePath());
			tempFolder.mkdirs();
			return tempFolder;
		}
		File tempFolder = null;
		try {
			tempFolder = Files.createTempDirectory(ServerProperties.class.getSimpleName()).toFile();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to create temp folder", e);
		}
		LOGGER.log(Level.FINEST, "Working Folder: " + tempFolder.getAbsolutePath());
		tempFolder.deleteOnExit();
		return tempFolder;
	}
	
	private static File prepareServerFolder() {
		return createTempFolder();
	}
	
	private File getProfilesFolder() {
		File profilesFolder = new File(SERVER_FOLDER, "profiles");
		if (!profilesFolder.exists()) {
			profilesFolder.mkdirs();
			profilesFolder.deleteOnExit();
		}
		return profilesFolder;
	}

	private File getDashboardFolder() {
		File dashboardsFolder = new File(SERVER_FOLDER, "dashboards");
		if (!dashboardsFolder.exists()) {
			dashboardsFolder.mkdirs();
			dashboardsFolder.deleteOnExit();
		}
		return dashboardsFolder;
	}
	
	public static ServerProperties load(ServerConfig serverConfig) {
		return load(serverConfig, true);
	}
	
	public static ServerProperties load(ServerConfig serverConfig, boolean silently) {
		ServerProperties serverProperties = new ServerProperties();
		try {
			serverProperties.fetch(serverConfig, silently);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to fetch configuration files from dynaTrace Server", e);
			return null;
		}
		return serverProperties;
	}
	
	private void fetch(ServerConfig serverConfig, boolean silently) throws IOException {
		if (!silently) {
			LOGGER.log(Level.INFO, "Fetching System Profile, Dashboards and Server Configuration from dynaTrace Server");
		}
		SysInfoRequest sysInfoRequest = new SysInfoRequest();
		HttpResponse<SysInfoResult> httpResponse =
				sysInfoRequest.execute(serverConfig);
		Throwable exception = httpResponse.getException();
		if (exception != null) {
			if (exception instanceof UnexpectedResponseCodeException) {
				UnexpectedResponseCodeException urce = (UnexpectedResponseCodeException) exception;
				String missingPermission = Unauthorized.getMissingPermission(urce.getServerResponse());
				if (missingPermission != null) {
					LOGGER.log(Level.SEVERE, "Missing Permission: " + missingPermission);
				}
			}
			throw new IOException();
		}
		SysInfoResult sysInfoResult = httpResponse.getData();
		File sysInfoZipFile = sysInfoResult.getFile();
		if (sysInfoZipFile == null) {
			throw new IOException();
		}
		if (!sysInfoZipFile.exists()) {
			throw new IOException();
		}
		if (!sysInfoZipFile.isFile()) {
			throw new IOException();
		}
		try (
			FileInputStream fin = new FileInputStream(sysInfoZipFile);
			ZipInputStream zin = new ZipInputStream(fin);
		) {
			ZipEntry zipEntry = zin.getNextEntry();
			while (zipEntry != null) {
				handleZipEntry(zipEntry, zin);
				zipEntry = zin.getNextEntry();
			}
		} finally {
			sysInfoZipFile.delete();
		}
	}
	
	private void handleZipEntry(ZipEntry zipEntry, InputStream in) throws IOException {
		if (zipEntry == null) {
			return;
		}
		String name = zipEntry.getName();
		if (name == null) {
			return;
		}
		if (!ZipEntries.isServerZipEntry(zipEntry)) {
			return;
		}
		if (isServerConfigXml(zipEntry)) {
			handleServerXml(in);
		} else if (isProfile(zipEntry)) {
			handleProfile(ZipEntries.getFileName(zipEntry), in);
		} else if (isDashboard(zipEntry)) {
			handleDashboard(ZipEntries.getFileName(zipEntry), in);
		} else if (isUserPermissionConfigXml(zipEntry)) {
			handleUserPermissionConfig(ZipEntries.getFileName(zipEntry), in);
		}
	}
	
	public Profile profiles(String name) {
		if (name == null) {
			return null;
		}
		Collection<Profile> profiles = profiles();
		if (profiles == null) {
			return null;
		}
		for (Profile profile : profiles) {
			if (profile == null) {
				continue;
			}
			if (name.equals(profile.getName())) {
				return profile;
			}
		}
		return null;
	}
	
	public Collection<Profile> profiles() {
		Collection<Profile> profiles = new ArrayList<>();
		File profilesFolder = getProfilesFolder();
		File[] files = profilesFolder.listFiles();
		if (files == null) {
			return profiles;
		}
		for (File file : files) {
			if (file == null) {
				continue;
			}
			if (file.getName().endsWith(".profile.xml")) {
				Profile profile = null;
				try {
					profile = new Profile(file);
					profiles.add(profile);
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, file.getName() + " is not a valid System Profile and will not get considered");
					LOGGER.log(Level.FINE, file.getName() + " is not a valid System Profile and will not get considered", e);
				}
			}
		}
		return profiles;
	}
	
	public Collection<Dashboard> dashboards() {
		Collection<Dashboard> dashboards = new ArrayList<>();
		File dashboardsFolder = getDashboardFolder();
		File[] files = dashboardsFolder.listFiles();
		if (files == null) {
			return dashboards;
		}
		for (File file : files) {
			if (file == null) {
				continue;
			}
			if (file.getName().endsWith(".dashboard.xml")) {
				Dashboard dashboard = null;
				try {
					dashboard = new Dashboard(file, UUID.randomUUID().toString());
					dashboards.add(dashboard);
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, file.getName() + " is not a valid Dashboard and will not get considered");
					LOGGER.log(Level.FINE, file.getName() + " is not a valid Dashboard and will not get considered", e);
				}
			}
		}
		return dashboards;
	}
	
	private void handleProfile(String fileName, InputStream in) throws IOException {
		File profilesFolder = getProfilesFolder();
		File profileFile = new File(profilesFolder, fileName);
		if (profileFile.exists()) {
			Closeables.purge(profileFile);
		}
		try (OutputStream out = new FileOutputStream(profileFile)) {
			Closeables.copy(in, out);
		}
	}

	private void handleUserPermissionConfig(String fileName, InputStream in) throws IOException {
//		File dashboardsFolder = getDashboardFolder();
//		File dashboardFile = new File(dashboardsFolder, fileName);
//		if (dashboardFile.exists()) {
//			Closeables.purge(dashboardFile);
//		}
//		try (OutputStream out = new FileOutputStream(dashboardFile)) {
//			Closeables.copy(in, System.out);
//		}
	}

	private void handleDashboard(String fileName, InputStream in) throws IOException {
		File dashboardsFolder = getDashboardFolder();
		File dashboardFile = new File(dashboardsFolder, fileName);
		if (dashboardFile.exists()) {
			Closeables.purge(dashboardFile);
		}
		try (OutputStream out = new FileOutputStream(dashboardFile)) {
			Closeables.copy(in, out);
		}
	}
	
	private void handleServerXml(InputStream in) throws IOException {
		File serverConfigXml = File.createTempFile("server.config.xml", ".tmp");
		serverConfigXml.deleteOnExit();
		try (OutputStream out = new FileOutputStream(serverConfigXml)) {
			Closeables.copy(in, out);
		}
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse collector.config.xml", e);
			return;
		}
		DefaultHandler handler = new DefaultHandler() {
			
			@Override
			public void startElement(String uri, String localName,	String qName, Attributes attributes) throws SAXException {
				if ("plugintypeconfig".equals(qName)) {
					String name = attributes.getValue("name");
					if ("Permission User Group Task".equals(name)) {
						isPermissionTaskInstalled = true;
					}
				}
			}
			
		};
		try (InputStream xmlIn = new FileInputStream(serverConfigXml)) {
			saxParser.parse(new BufferedInputStream(xmlIn) {
				@Override
				public void close() throws IOException {
				}
			}, handler);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse server.config.xml", e);
		}		
	}
	
	
	
	private static boolean isServerConfigXml(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		String fileName = ZipEntries.getFileName(zipEntry);
		if (fileName == null) {
			return false;
		}
		return isServerConfigXml(fileName);
	}
	
	private static boolean isServerConfigXml(String fileName) {
		if (fileName == null) {
			return false;
		}
		return fileName.endsWith("server.config.xml");
	}
	
	private static boolean isProfile(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		String fileName = ZipEntries.getFileName(zipEntry);
		if (fileName == null) {
			return false;
		}
		return isProfile(fileName);
	}
	
	private static boolean isUserPermissionConfigXml(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		String fileName = ZipEntries.getFileName(zipEntry);
		if (fileName == null) {
			return false;
		}
		return isUserPermissionConfigXml(fileName);
	}
	
	private static boolean isDashboard(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		String fileName = ZipEntries.getFileName(zipEntry);
		if (fileName == null) {
			return false;
		}
		return isDashboard(fileName);
	}
	
	private static boolean isProfile(String fileName) {
		if (fileName == null) {
			return false;
		}
		if (fileName.endsWith(".profile.xml")) {
			if (fileName.equals("dynaTrace Self-Monitoring.profile.xml")) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private static boolean isDashboard(String fileName) {
		if (fileName == null) {
			return false;
		}
		if (fileName.endsWith(".dashboard.xml")) {
			return true;
		}
		return false;
	}
	
	private static boolean isUserPermissionConfigXml(String fileName) {
		if (fileName == null) {
			return false;
		}
		if (fileName.endsWith("user.permissions.xml")) {
			return true;
		}
		return false;
	}
	
	
	
}
