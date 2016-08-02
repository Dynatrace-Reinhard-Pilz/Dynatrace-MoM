package com.dynatrace.mom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.dynatrace.fixpacks.FixPackManager;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.XMLServerRepository;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Logging;
import com.dynatrace.utils.Storage;
import com.dynatrace.web.base.ServletExecutionContext;
import com.dynatrace.xml.XMLUtil;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@WebListener
public class MomWebAppInit implements ServletContextListener {
	
	@SuppressWarnings("unused")
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
	
	private InputStream resolveServersXml(ExecutionContext ctx) throws FileNotFoundException {
		File storageFolder = ctx.getStorageFolder();
		File storageServersXml = new File(storageFolder, "servers.xml");
		if (storageServersXml.exists()) {
			LOGGER.log(Level.INFO, "***** Loading configuration from " + storageServersXml.getAbsolutePath());
			return new FileInputStream(storageServersXml);
		}
		LOGGER.log(Level.INFO, "**** Loading configuration from WEB-INF/servers.xml");
		return Closeables.getResourceAsStream("/servers.xml");
	}
	
	@Override
	public void contextInitialized(ServletContextEvent evt) {
		try {
			ServletContext servletContext = evt.getServletContext();
			// Logging.init(null, new ServletContextLogHandler(servletContext), (String []) null);
			Logging.init(null, (String []) null);
			MomConfig CONFIG = resolveConfig();
			servletContext.setAttribute(MomConfig.ATTRIBUTE, CONFIG);
			File ctxTempDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
			if (CONFIG.getStorage() != null) {
				LOGGER.info("System.setProperty(" + Storage.PROPERTY_WORK_DIR + ", " + CONFIG.getStorage().getAbsolutePath() + " (CONFIG.getStorage().getAbsolutePath()));");
				System.setProperty(Storage.PROPERTY_WORK_DIR, CONFIG.getStorage().getAbsolutePath());
			} else {
				LOGGER.info("System.setProperty(" + Storage.PROPERTY_WORK_DIR + ", " + ctxTempDir.getAbsolutePath() + " (ctxTempDir.getAbsolutePath()));");
				System.setProperty(Storage.PROPERTY_WORK_DIR, ctxTempDir.getAbsolutePath());
			}
			ExecutionContext ctx = new ServletExecutionContext(servletContext);
			MomInit commandLine = new MomInit();
			FixPackManager fixPackManager = new FixPackManager(ctx);
			ctx.setAttribute(fixPackManager);
			
			try (InputStream in = resolveServersXml(ctx)) {
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
		XMLServerRepository xmlServerRepository = new XMLServerRepository();
		Collection<ServerRecord> serverRecords = repo.getServerRecords();
		for (ServerRecord serverRecord : serverRecords) {
			ctx.log(Level.INFO, serverRecord.toString());
		}
		xmlServerRepository.setServerRecords(serverRecords);
		xmlServerRepository.encrypt();
		File storageFolder = ctx.getStorageFolder();
		File storageServersXml = new File(storageFolder, "servers.xml");
		try (OutputStream out = new FileOutputStream(storageServersXml)) {
			XMLUtil.serialize(xmlServerRepository, out);
		} catch (IOException e) {
			ctx.log(Level.WARNING, "Unable to persist servers.xml");
		}
		xmlServerRepository.decrypt();

		
		repo.close();
		ctx.removeAttribute(ATT_REPO);
		Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                ctx.log(Level.INFO, String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
            	ctx.log(Level.SEVERE, String.format("Error deregistering driver %s", driver), e);
            }

        }		
		ctx.log(Level.INFO, "Manager of Manager Web Application has been shut down");
	}
	
}
