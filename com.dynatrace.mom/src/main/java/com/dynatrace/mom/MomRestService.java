package com.dynatrace.mom;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dynatrace.mom.rest.FixedHrefGenerator;
import com.dynatrace.mom.rest.RESTChartList;
import com.dynatrace.mom.rest.RESTMeasureList;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.components.ServerRecord;

@Path("rest")
public class MomRestService {
	
	@Context
	private ServletContext context;
	
	private final ServerRepository getServerRepository() {
		return (ServerRepository) context.getAttribute(ServerRepository.class.getName());
	}
	
	@GET
	@Path("/servers/{servername}/charts")
	@Produces(MediaType.TEXT_XML)
	public Response getCharts(
		@PathParam("servername") String name,
		@Context HttpServletRequest request
	) {
		final FixedHrefGenerator hrefGenerator = new FixedHrefGenerator(request.getRequestURL().toString());
		final ServerRecord serverRecord = getServerRepository().get(name);
		if (serverRecord == null) {
			return Response.status(404).build();
		}
		Collection<String> chartNames = serverRecord.getChartNames();
		RESTChartList chartRecords = new RESTChartList(hrefGenerator);
		for (String chartName : chartNames) {
			chartRecords.add(chartName);
		}
		return Response.ok(chartRecords).build();
	}
	
	@GET
	@Path("/servers/{servername}/charts/{chartname}")
	@Produces(MediaType.TEXT_XML)
	public Response getChartMeasures(
		@PathParam("servername") String name,
		@PathParam("chartname") String chartname,
		@Context HttpServletRequest request
	) {
		final FixedHrefGenerator hrefGenerator = new FixedHrefGenerator(request.getRequestURL().toString());
		final ServerRecord serverRecord = getServerRepository().get(name);
		if (serverRecord == null) {
			return Response.status(404).build();
		}
		return Response.ok(
			new RESTMeasureList(
				serverRecord.getMeasures(chartname),
				hrefGenerator
			)
		).build();
	}
	
}