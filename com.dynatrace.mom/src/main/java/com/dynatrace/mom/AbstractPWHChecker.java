package com.dynatrace.mom;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.PWHConfig;

public abstract class AbstractPWHChecker implements AutoCloseable {
	
	private static final Logger LOGGER =
			Logger.getLogger(AbstractPWHChecker.class.getName());
	
	private static final String DEFAULT_QUERY =
			"SELECT COUNT(*) AS CNT FROM {0}";
	
	private final PWHConfig config;
	
	private final Class<?> DRIVER_CLASS;
	
	public AbstractPWHChecker(PWHConfig config) {
		this.config = config;
		DRIVER_CLASS = getDriver();
		if (DRIVER_CLASS == null) {
			LOGGER.log(Level.WARNING, "PWH Checker cannot get started - Driver was not found");
		}
	}
	
	protected static class CountRecord {
		
		public final String table;
		public int lastCount = 0;
		public long lastModTime = System.currentTimeMillis();
		
		public CountRecord(String table) {
			this.table = table;
		}
		
		public void update(int count) {
			if (count != lastCount) {
				// LOGGER.log(Level.INFO, "[" + table + "] last: " + lastCount + ", new: " + count);
				lastCount = count;
				lastModTime = System.currentTimeMillis();
			}
		}
		
		public static long getLastModTime(CountRecord[] records) {
			if (records == null) {
				return 0L;
			}
			long lastModTime = 0;
			for (CountRecord record : records) {
				if (record == null) {
					continue;
				}
				lastModTime = Math.max(lastModTime, record.lastModTime);
			}
			return lastModTime;
		}
	}
	
	
	protected abstract Class<?> getDriver();
	
	protected String getQuery() {
		return DEFAULT_QUERY;
	}

	protected abstract Connection getConnection(PWHConfig config) throws SQLException;
	protected String getTablePrefix() {
		return null;
	}
	
	public void update(CountRecord countRecord) {
		if (countRecord == null) {
			return;
		}
		try {
			Connection connection = null;
			ResultSet rs = null;
			Statement stmt = null;
			try {
				String table = countRecord.table;
				String prefix = getTablePrefix();
				if (prefix != null) {
					table = prefix + "." + table;
				}
				String sql = MessageFormat.format(getQuery(), table);
				LOGGER.log(Level.FINEST, sql);
				connection = getConnection(config);
				if (connection == null) {
					return;
				}
				stmt = connection.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					countRecord.update(rs.getInt(1));
				}
			} catch (SQLException e) {
				Throwable cause = e.getCause();
				if (cause != null) {
					LOGGER.log(Level.SEVERE, "problem querying for measurement entries", cause);
					throw cause;
				}
				LOGGER.log(Level.SEVERE, "problem querying for measurement entries", cause);
				throw e;
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (Throwable t) {
						LOGGER.log(Level.SEVERE, "Unable to close connection", t);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (Throwable t) {
						LOGGER.log(Level.SEVERE, "Unable to close statement", t);
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (Throwable t) {
						LOGGER.log(Level.SEVERE, "Unable to close result set", t);
					}
				}
			}
			
		} catch (Throwable cfne) {
			LOGGER.log(Level.SEVERE, "Unable to resolve DB Driver", cfne);
		}		
	}

	@Override
	public void close() {
		LOGGER.log(Level.INFO, getName() + " shutting down");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public final String getName() {
		return "PWHChecker[" + config.getDatabase() + "@" + config.getHost() + "]";
	}
	
}
