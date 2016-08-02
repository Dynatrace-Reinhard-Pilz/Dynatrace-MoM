package com.dynatrace.mom;

import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.servlet.ServletContext;

public class ServletContextLogHandler extends Handler {
	
	private final ServletContext ctx;
	
	public ServletContextLogHandler(ServletContext ctx) {
		Objects.requireNonNull(ctx);
		this.ctx = ctx;
	}

	@Override
	public void publish(LogRecord record) {
		if (record == null) {
			return;
		}
		Formatter formatter = getFormatter();
		if (formatter == null) {
			return;
		}
		String message = formatter.format(record);
		ctx.log(message);
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

}
