package com.dynatrace.collectors.web;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.dynatrace.collectors.CollectorCollection;
import com.dynatrace.collectors.CollectorInfo;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.collectors.CollectorRestart;
import com.dynatrace.collectors.RestartStatus;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.web.base.CorePagesBase;
import com.sun.jersey.api.view.Viewable;

@Path("servers/{servername}/collectors")
public class CollectorPages extends CorePagesBase {
	
	private CollectorCollection getCollectorCollection(String serverName) {
		ExecutionContext ctx = getContext(serverName);
		return ctx.getAttribute(CollectorCollection.class);
	}
	
	@GET
	@Produces(TEXT_HTML)
	public Viewable getCollectors(@PathParam("servername") String servername)
		throws Exception
	{
		ExecutionContext ctx = getContext(servername);
		request.setAttribute("serverFilter", servername);
		DefaultCollectorsModel model = new DefaultCollectorsModel(ctx, servername, false);
//		final ServerRecord serverRecord = getServerRepository().get(servername);
//		setAttribute(serverRecord);
//		DefaultCollectorsModel model = new DefaultCollectorsModel(serverRecord);
		setAttribute(model);
		return new Viewable("/jsp/mom-page.jsp", null);
	}
	
	@POST
	@Path("{collectorname}/restart")
	@Produces(TEXT_HTML)
	public Response restartCollector(
		@PathParam("servername") String servername,
		@PathParam("collectorname") String collectorname
	) {
		String requestURI = request.getRequestURI();
		String referer = request.getHeader("Referer");
		URL url = toURL(referer);
		if (url != null)
		log("requestURI: " + requestURI);
		CollectorCollection collectors = getCollectorCollection(servername);
		for (CollectorRecord collector : collectors) {
			CollectorInfo collectorInfo = collector.getCollectorInfo();
			String collectorName = collectorInfo.getName();
			String collectorHost = collectorInfo.getHost();
			String currentCollectorName = collectorName + "@" + collectorHost;
			if (currentCollectorName.equals(collectorname)) {
				collector.setRestartStatus(RestartStatus.SCHEDULED);
				ExecutionContext ctx = getContext(servername);
				ServerConfig config = ctx.getAttribute(ServerConfig.class);
				ctx.execute(new CollectorRestart(
					ctx,
					config,
					collectorName,
					collectorHost
				));
				return Response.seeOther(toURI(url)).build();
			}
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	
}