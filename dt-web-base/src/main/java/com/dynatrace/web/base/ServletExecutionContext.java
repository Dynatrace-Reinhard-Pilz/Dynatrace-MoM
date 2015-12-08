package com.dynatrace.web.base;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.dynatrace.utils.AbstractExecutionContext;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class ServletExecutionContext extends AbstractExecutionContext {
	
	private static final Logger LOGGER =
			Logger.getLogger(ServletExecutionContext.class.getName());
	
	private final ServletContext ctx;
	
	public ServletExecutionContext(ServletContext ctx) {
		Objects.requireNonNull(ctx);
		this.ctx = ctx;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		Objects.requireNonNull(name);
		return (T) ctx.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object attribute) {
		Objects.requireNonNull(name);
		ctx.setAttribute(name, attribute);
	}
	
	@Override
	public void removeAttribute(String name) {
		Objects.requireNonNull(name);
		ctx.removeAttribute(name);
	}

	@Override
	public String getContextPath() {
		String contextPath = ctx.getContextPath();
		contextPath = contextPath.replace("\\", "");
		contextPath = contextPath.replace("/", "_");
		if (contextPath.startsWith("_")) {
			contextPath = contextPath.substring(1);
		}
		return contextPath;
	}
	
	@Override
	public void log(Level level, String message) {
		ctx.log(message);
	}
	
	@Override
	public void log(Level level, String message, Throwable throwable) {
		ctx.log(message, throwable);
	}
	
	@Override
	public void log(Level level, String msg, Object... params) {
		log(level, MessageFormat.format(msg, params));
	}

	@Override
	public void execute(Runnable command) {
		command.run();
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
