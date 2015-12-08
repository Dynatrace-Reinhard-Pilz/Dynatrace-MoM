package com.dynatrace.mom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.dynatrace.fixpacks.FixPackManager;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Logging;
import com.dynatrace.utils.Storage;
import com.dynatrace.web.base.ServletExecutionContext;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@WebListener
public class MomWebAppInit implements ServletContextListener {
	
	private static final Logger LOGGER =
			Logger.getLogger(MomWebAppInit.class.getName());
	
	@SuppressWarnings("unused")
	private static final String MSG_PERSISTING = "Persisting Server Repository to {0}";
	
	@SuppressWarnings("unused")
	private static final String ATT_TEMP_DIR = ServletContext.TEMPDIR;
	private static final String ATT_CMDLINE = MomInit.class.getName();
	private static final String ATT_REPO = ServerRepository.class.getName();
	
	private static MomConfig resolveConfig() {
		MomConfig config = new MomConfig();
		return config;
	}
	
//	private void logClasses(final ExecutionContext ctx) {
//		AnnotationScannerListener listener = new AnnotationScannerListener(Module.class);
//		final PackageNamesScanner scanner=new PackageNamesScanner(new String[] { "" });
//		scanner.scan(listener);
//		Set<Class<?>> annotatedClasses = listener.getAnnotatedClasses();
//		if (CollUtil.isNotEmpty(annotatedClasses)) {
//			for (Class<?> annotatedClass : annotatedClasses) {
//				ctx.log(annotatedClass.getName());
//			}
//		}
//	}
	
	@Override
	public void contextInitialized(ServletContextEvent evt) {
		try {
			ServletContext servletContext = evt.getServletContext();
			MomConfig CONFIG = resolveConfig();
			servletContext.setAttribute(MomConfig.ATTRIBUTE, CONFIG);
			File ctxTempDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
			System.setProperty(Storage.PROPERTY_WORK_DIR, ctxTempDir.getAbsolutePath());
			Logging.init(null, (String []) null);
			ExecutionContext ctx = new ServletExecutionContext(servletContext);
			MomInit commandLine = new MomInit();
			FixPackManager fixPackManager = new FixPackManager(ctx);
			ctx.setAttribute(fixPackManager);
			
			try (InputStream in = Closeables.getResourceAsStream("/servers.xml")) {
				commandLine.init(in, ctx);
			} catch (IOException e) {
				ctx.log(Level.INFO, "Unable to recover Server Repository from persisted data, creating a new one",	e);
				commandLine.init(ctx);
			} catch (Throwable t) {
				ctx.log(Level.INFO, "Unable to recover Server Repository from persisted data", t);
				final Throwable cause = t.getCause();
				if (cause != null) {
					cause.printStackTrace(System.err);	
				} else {
					t.printStackTrace(System.err);
				}
			}
			ctx.setAttribute(ATT_CMDLINE, commandLine);
			ctx.setAttribute(ATT_REPO, commandLine.getServerRepository());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		ExecutionContext ctx = new ServletExecutionContext(evt.getServletContext());
		ctx.log(Level.INFO, "Shutting down Manager of Manager Web Application");
		MomInit cmdLine = ctx.getAttribute(ATT_CMDLINE);
		if (cmdLine != null) {
			ctx.removeAttribute(ATT_CMDLINE);
			cmdLine.shutdown();
		}
		ServerRepository repo = (ServerRepository) ctx.getAttribute(ATT_REPO);
		repo.close();
		ctx.removeAttribute(ATT_REPO);
		ctx.log(Level.INFO, "Manager of Manager Web Application has been shut down");
	}
	
}
