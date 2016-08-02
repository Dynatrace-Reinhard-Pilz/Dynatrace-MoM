package com.dynatrace.mom;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.PWHConfig;

public final class PostgresPWHChecker extends AbstractPWHChecker {
	
	private static final Logger LOGGER =
			Logger.getLogger(PostgresPWHChecker.class.getName());
	
	private static Class<?> DRIVER_CLASS = resolveDriver();
	private static final String DEFAULT_PORT = "5432";
	
	public PostgresPWHChecker(PWHConfig config) {
		super(config);
	}	
	
	private static Class<?> resolveDriver() {
		try {
			Class<?> cls = Class.forName("org.postgresql.Driver");
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
			"jdbc:postgresql://" + host + ":" + port + "/" + config.getDatabase() + ":user=" + config.getCredentials().getUser() + ";" + "password=" + config.getCredentials().getPass() + ";",
			config.getCredentials().getUser(),
			config.getCredentials().getPass()
		);
	}
	
}
