package com.dynatrace.mom.rest;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.dynatrace.utils.Closeables;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
	
    @Context
    HttpServletRequest request;
	
	@Override
	public Response toResponse(Throwable throwable) {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		return Response.status(INTERNAL_SERVER_ERROR).cacheControl(cacheControl).entity(getErrorHTML(throwable)).type(TEXT_HTML).build();
	}
	
	private String getErrorHTML(Throwable throwable) {
		URL errorHtmlUrl = GenericExceptionMapper.class.getResource("/error.html");
		try (InputStream in = errorHtmlUrl.openStream()){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out);
			String errorHtml = new String(out.toByteArray());
			errorHtml = errorHtml.replace("${pageContext.request.contextPath}", request.getServletContext().getContextPath());
			errorHtml = errorHtml.replace("${pageContext.request.errorMessage}", getStackTrace(throwable));
			//errorHtml = errorHtml.replace("${pageContext.request.remoteUser}", request.getRemoteUser());
			
			return errorHtml;
		} catch (final IOException e) {
			final ServletContext ctx = request.getServletContext();
			ctx.log("Unable to load error.html", e);
			return "<!DOCTYPE html><html lang=\"en\"><head><title>An internal error occured</title></head><body>An internal error occurred</body></html>";			
		}
	}
	
	private String getStackTrace(Throwable throwable) {
		try (
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
		) {
			throwable.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.getBuffer().toString();
		} catch (IOException ioe) {
			return "Unable to pull the StackTrace from Exception " + throwable.getMessage();
		}
	}
	
	
}