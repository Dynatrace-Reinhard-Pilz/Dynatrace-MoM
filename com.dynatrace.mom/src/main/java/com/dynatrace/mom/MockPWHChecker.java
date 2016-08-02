package com.dynatrace.mom;

import java.sql.Connection;
import java.sql.SQLException;

import com.dynatrace.http.config.PWHConfig;

public class MockPWHChecker extends AbstractPWHChecker {
	
	public MockPWHChecker(PWHConfig config) {
		super(config);
	}

	@Override
	protected Class<?> getDriver() {
		return MockPWHChecker.class;
	}

	@Override
	protected Connection getConnection(PWHConfig config) throws SQLException {
		return null;
	}
	
	@Override
	public void update(CountRecord countRecord) {
		if (countRecord == null) {
			return;
		}
		countRecord.lastModTime = System.currentTimeMillis();
	}

}
