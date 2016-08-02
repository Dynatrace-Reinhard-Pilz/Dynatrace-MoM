package com.dynatrace.mom;

import com.dynatrace.http.config.PWHConfig;
import com.dynatrace.utils.Labelled;

public final class PWHCheckerFactory {

	private PWHCheckerFactory() {
		// prevent instantiation
	}
	
	public static AbstractPWHChecker createPWHChecker(Labelled label, PWHConfig config) {
		if (config == null) {
			return new MockPWHChecker(null);
		}
		switch (config.getDatabaseType()) {
		case MSSQL:
			return new MSSQLerverPWHChecker(config);
		case Postgres:
			return new PostgresPWHChecker(config);
		case None:
			return new MockPWHChecker(config);
		}
		return new MockPWHChecker(config);
	}
}
