package com.dynatrace.profilediff;

import java.io.File;
import java.io.PrintStream;

public class Log {
	
	private static final String LOG_FILE_NAME = Log.class.getName() + ".log";
	private static final PrintStream OUT = init();
	
	private static PrintStream init() {
		File logFile = new File(LOG_FILE_NAME);
		if (logFile.exists()) {
			logFile.delete();
		}
		try {
			return new PrintStream(logFile);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			throw new InternalError(t.getMessage());
		}
	}

	public static void info(String s) {
		if (s == null) {
			return;
		}
		System.out.println(s);
		OUT.println(s);
	}
}
