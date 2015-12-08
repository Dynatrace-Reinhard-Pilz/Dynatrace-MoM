package com.dynatrace.onboarding.config;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class Debug {
	
	private static final Logger LOGGER =
			Logger.getLogger(Debug.class.getName());
	
	public static final boolean DEBUG = isDebug();
	
	private static boolean isDebug() {
		URL url = Debug.class.getClassLoader().getResource("META-INF/MANIFEST.MF");
		LOGGER.log(Level.FINEST, url.toString());
		boolean isDebug = url.getProtocol().equals("file");
		if (isDebug) {
			LOGGER.log(Level.INFO, "Debug Mode on");
		}
		return isDebug;
	}

	public static void logMethodEntry(Logger logger, Level level) {
		Exception exception = new Exception();
		StackTraceElement[] stackTrace = exception.getStackTrace();
		StackTraceElement stackTraceElement = stackTrace[1];
		String methodName = stackTraceElement.getMethodName();
		String className = stackTraceElement.getClassName();
		logger.log(level, className + "." + methodName);
	}
	
}
