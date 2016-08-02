package com.dynatrace.mom;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.PWHConfig;

public final class MSSQLerverPWHChecker extends AbstractPWHChecker {
	
	private static final Logger LOGGER =
			Logger.getLogger(MSSQLerverPWHChecker.class.getName());
	
	private static final String DEFAULT_PORT = "1443";
	
	private static Class<?> DRIVER_CLASS = resolveDriver();
	
	public MSSQLerverPWHChecker(PWHConfig config) {
		super(config);
	}	
	
	private static Class<?> resolveDriver() {
		try {
			Class<?> cls = Class.forName("net.sourceforge.jtds.jdbc.Driver");
			try {
				DriverManager.registerDriver((Driver) cls.newInstance());
			} catch (Throwable e) {
				throw new InternalError(e.getMessage());
			}
			return cls;
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Unable to resolve JDBC Driver");
			return null;
		}
	}
	
	@Override
	protected Class<?> getDriver() {
		return DRIVER_CLASS;
	}
	
	@Override
	protected Connection getConnection(PWHConfig config) throws SQLException {
		String host = config.getHost();
		String port = DEFAULT_PORT;
		int colonIdx = host.lastIndexOf(':');
		if (colonIdx >= 0) {
			port = host.substring(colonIdx);
			LOGGER.log(
				Level.INFO,
				"discovered port " + port + " as part of host attribute " + host
			);
			host = host.substring(0, colonIdx);
		}
		return DriverManager.getConnection(
			"jdbc:jtds:sqlserver://" + host + ":" + port + ";DatabaseName=" + config.getDatabase(),
			config.getCredentials().getUser(),
			config.getCredentials().getPass()
		);
	}
	
}
