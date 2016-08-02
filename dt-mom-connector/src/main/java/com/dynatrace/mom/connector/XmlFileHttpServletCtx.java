package com.dynatrace.mom.connector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

import com.dynatrace.diagnostics.sdk.security.Permission;
import com.dynatrace.diagnostics.sdk.sessions.SessionReference;
import com.dynatrace.diagnostics.sdk.sessions.SessionType;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.mom.connector.model.dashboards.DashboardReference;
import com.dynatrace.mom.connector.model.dashboards.DashboardReferences;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReferences;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReference;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReferences;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Extract;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.files.AbstractFileReference;
import com.dynatrace.utils.files.AbstractFileReferences;
import com.dynatrace.xml.XMLUtil;

public abstract class XmlFileHttpServletCtx<T extends AbstractFileReference> extends AbstractHttpServletCtx implements FileFilter {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(XmlFileHttpServletCtx.class.getName());
	
	// private static final String EXT_XML = ".xml";
	private static final String CONTENT_TYPE_XML = "application/xml";
	private static final String HEADER_CONTENT_TYPE = "Content-Type".intern();
	private static final String HEADER_CACHE_CONTROL = "Cache-Control".intern();
	private static final String HEADER_VALUE_CACHE_CONTROL = "no-cache, no-store, must-revalidate".intern();
	private static final String HEADER_PRAGMA = "Pragma".intern();
	private static final String HEADER_VALUE_PRAGMA = "no-cache".intern();
	private static final String HEADER_EXPIRES = "Expires".intern();
	
	private static final JAXBContext ctx = createJAXBContext();
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	public XmlFileHttpServletCtx(ServerAccess serverAccess) {
		super(serverAccess);
	}
	
	private static JAXBContext createJAXBContext() {
		try {
			return XMLUtil.createContext(
				DashboardReferences.class,
				SystemProfileReferences.class,
				ProfileTemplateReferences.class,
				SystemProfileReference.class,
				ProfileTemplateReference.class,
				DashboardReference.class,
				AbstractFileReference.class,
				AbstractFileReferences.class
			);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to create JAXB Context", e);
			return null;
		}		
	}
	
	protected abstract File getRootFolder();
	public abstract AbstractFileReferences<T> createContainer();
	public abstract T createContainerElement();
	public abstract String[] getExtensions();
	public abstract boolean checkPermission(UserPermissionInfo info, String id);
	
	private boolean serveFileContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestURI = request.getRequestURI();
		
		if (getMatchingExtension(requestURI) == null) {
			response.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_XML);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			HttpError httpError = new HttpError(HttpServletResponse.SC_BAD_REQUEST, removeContext(requestURI) + " is not a valid file type for this context");
			try (
				PrintWriter writer = response.getWriter();					
			) {
				writer.print(XMLUtil.toString(httpError));
			}
			return false;
		}
		URL resource = getResource(removeContext(requestURI));
		if (resource == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, removeContext(requestURI) + " does not exist");
			return true;
		}
		response.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_XML);
		response.setStatus(HttpServletResponse.SC_OK);
		try (
			InputStream in = resource.openStream();
			OutputStream out = response.getOutputStream();
		) {
			Closeables.copy(in, out);
		}
		return true;
	}
	
	private String getMatchingExtension(String requestURI) {
		for (String extension : getExtensions()) {
			if (requestURI.endsWith(extension)) {
				return extension;
			}
		}
		return null;
	}
	
	private void serveDirectoryContent(HttpServletRequest req, HttpServletResponse res) throws IOException {
		UserPermissionInfo upi = (UserPermissionInfo) req.getAttribute(UserPermissionInfo.class.getName());
		String requestURI = req.getRequestURI();
		if (Strings.equals(getContext(), requestURI)) {
			res.setStatus(HttpServletResponse.SC_OK);
			res.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_XML);
			AbstractFileReferences<T> container = createContainer();
			container.setHref(new URL(req.getScheme(), req.getServerName(), req.getServerPort(), requestURI).toString());
			File rootFolder = getRootFolder();
			File[] files = rootFolder.listFiles(this);
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					String fileUrl = getContext() + "/" + URLEncoder.encode(fileName, UTF8.name());
					T element = createContainerElement();
					String id = fileName;
					SessionReference sessionReference =
							SessionReference.createSessionReference(SessionType.live, id);
					if (!checkPermission(upi, sessionReference, Permission.WriteApplicationConfig)) {
						continue;
					}
					String matchingExtension = getMatchingExtension(fileName);
					if (id.endsWith(matchingExtension)) {
						id = id.substring(0, id.length() - matchingExtension.length());
					}
					element.setId(id);
					element.setHref(new URL(req.getScheme(), req.getServerName(), req.getServerPort(), fileUrl).toString());
					long fileLength = file.length();
					if (fileLength == 0) {
						continue;
					}
					element.setSize(fileLength);
					element.setLastModified(file.lastModified());
					String sVersion = null;
					try (FileInputStream in = new FileInputStream(file)) {
						sVersion = Extract.extract(in, "<dynatrace version=\"", "\"");
						if (sVersion == null) {
							continue;
						}
						element.setVersion(Version.parse(sVersion).toString());
					} catch (IOException e) {
						LOGGER.log(Level.WARNING, "Unable to extract version information from " + file.getName(), e);
						element = null;
					} catch (IllegalArgumentException e) {
						LOGGER.log(Level.WARNING, "Invalid version '" + sVersion + "' extracted from " + file.getName(), e);
						element = null;
					}
					if (element != null) {
						container.add(element);
					}
				}
			}
			try (
				OutputStream out = res.getOutputStream();
			) {
				XMLUtil.serialize(container, out, UTF8, ctx);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader(HEADER_CACHE_CONTROL, HEADER_VALUE_CACHE_CONTROL); // HTTP 1.1
		resp.setHeader(HEADER_PRAGMA, HEADER_VALUE_PRAGMA); // HTTP 1.0
		resp.setDateHeader(HEADER_EXPIRES, 0); // Proxies.
		String requestURI = req.getRequestURI();
		if (Strings.equals(getContext(), requestURI)) {
			serveDirectoryContent(req, resp);
			return;
		}
		serveFileContent(req, resp);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final URL getResource(String resource) {
		if (resource == null) {
			return null;
		}
		resource = Strings.removePrefix(resource, '/');
		if (Strings.isNullOrEmpty(resource)) {
			return null;
		}
		File rootFolder = getRootFolder();
		if (!Closeables.existsFolder(rootFolder)) {
			return null;
		}
		File file = new File(getRootFolder(), resource);
		if (!Closeables.existsFile(file)) {
			return null;
		}
		return toURL(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getMimeType(String resource) {
		if (resource == null) {
			return null;
		}
		if (getMatchingExtension(resource) != null) {
			return CONTENT_TYPE_XML;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			return false;
		}
		if (!pathname.exists()) {
			return false;
		}
		if (!pathname.isFile()) {
			return false;
		}
		if (getMatchingExtension(pathname.getName()) == null) {
			return false;
		}
		return true;
	}

}
