package com.dynatrace.tasks;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.sdk.dashboard.DashboardConfig;
import com.dynatrace.diagnostics.sdk.sessions.SessionReference;
import com.dynatrace.diagnostics.sdk.sessions.SessionType;
import com.dynatrace.diagnostics.server.Server;
import com.dynatrace.diagnostics.util.ExceptionHelper;

public class DashboardHelper {
	
	private static final Logger LOGGER =
			Logger.getLogger(DashboardHelper.class.getName());

	private static Server getServer() {
		Field serverField = null;
		Field[] fields = Server.class.getDeclaredFields();
		for (Field field : fields) {
			if (field.getType().equals(Server.class)) {
				serverField = field;
				break;
			}
		}
		serverField.setAccessible(true);
		try {
			return (Server) serverField.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Unable to resolve Server Object", e);
			return null;
		}
	}
	
	private static DashboardConfig getDashboardConfig(Server server, String dashboardName) {
		try {
			Object dcm = server.getDashboardConfigManager(); // DashboardConfigManagerInterface
			Method methodGetDashboardConfiguration = dcm.getClass().getDeclaredMethod("getDashboardConfiguration", String.class);
			methodGetDashboardConfiguration.setAccessible(true);
			return (DashboardConfig) methodGetDashboardConfiguration.invoke(dcm, dashboardName);  // 
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Unable to get DashboardConfig Object", e);
			LOGGER.log(Level.SEVERE, ExceptionHelper.stackTraceToString(e));
			return null;
		}
	}
	
	private static boolean storeDashboardConfiguration(Server server, DashboardConfig dbc) {
		try {
			Object dcm = server.getDashboardConfigManager(); // DashboardConfigManagerInterface
			Method methodStoreDashboardConfiguration = dcm.getClass().getDeclaredMethod("storeDashboardConfiguration", DashboardConfig.class, String.class);
			methodStoreDashboardConfiguration.setAccessible(true);
			methodStoreDashboardConfiguration.invoke(dcm, dbc, dbc.getName());
			return true;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Unable to get DashboardConfig Object", e);
			LOGGER.log(Level.SEVERE, ExceptionHelper.stackTraceToString(e));
			return false;
		}
	}
	
	public static void assign(String dashboardName, String systemProfile) {
		Server server = getServer();
		DashboardConfig dbc = getDashboardConfig(server, dashboardName);
		if (dbc == null) {
			LOGGER.log(Level.WARNING, "A Dashboard named '" + dashboardName + "' does not exist.");
			return;
		}
		dbc.setSessionReference(SessionReference.createSessionReference(SessionType.live, systemProfile));
		storeDashboardConfiguration(server, dbc);
	}
}
