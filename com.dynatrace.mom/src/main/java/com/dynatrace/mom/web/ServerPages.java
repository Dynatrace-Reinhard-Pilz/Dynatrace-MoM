package com.dynatrace.mom.web;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.dynatrace.collectors.CollectorCollection;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.collectors.Filter;
import com.dynatrace.collectors.web.DefaultCollectorsModel;
import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackInstallStatus;
import com.dynatrace.fixpacks.FixPackManager;
import com.dynatrace.fixpacks.InstallStatus;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.Protocol;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.components.FixPackAction;
import com.dynatrace.mom.runtime.components.FixPackActions;
import com.dynatrace.mom.runtime.components.ServerContext;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.mom.web.breadcrumbs.ActivePage;
import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.mom.web.tabs.Widget;
import com.dynatrace.mom.web.tabs.Widgets;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Version;
import com.dynatrace.web.base.Link;
import com.sun.jersey.api.view.Viewable;

@Path("servers")
public class ServerPages extends PagesBase {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ServerPages.class.getName());
	
	@GET
	@Produces(TEXT_HTML)
	public Viewable getServers() throws Exception {
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers();
		setAttribute(breadCrumbs);
		setAttribute(getServerRepository());
		ServerRepository repo = getServerRepository();
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Collection<ServerRecord> serverRecords = repo.getServerRecords();
		if (!Iterables.isNullOrEmpty(serverRecords)) {
			for (ServerRecord serverRecord : serverRecords) {
				if (serverRecord == null) {
					continue;
				}
				Collection<FixPack> fixpacks = fpMgr.getNonInstalledFixPacks(serverRecord.getVersion());
				serverRecord.setVersionOutdated(!Iterables.isNullOrEmpty(fixpacks));
			}
		}

		return new Viewable("/jsp/servers.jsp", null);
	}

	@GET
	@Path("<new>")
	@Produces(TEXT_HTML)
	public Viewable createServer() throws Exception {
		setAttribute(Widgets.createServer().select(Widget.serverNew()));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add("<new>");
		setAttribute(breadCrumbs);
		Viewable viewable = new Viewable("/jsp/server/new.jsp", null);
		return viewable;
	}
	
	@GET
	@Path("{servername}/config")
	@Produces(TEXT_HTML)
	public Viewable serverConfig(
			@PathParam("servername") String servername
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverConfig(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		setAttribute(servername);
		setAttribute(breadCrumbs);
		request.setAttribute("activepage", ActivePage.config);
		Viewable viewable = new Viewable("/jsp/server/config.jsp", null);
		return viewable;
	}
	
	@POST
	@Path("{servername}/config")
	@Produces(TEXT_HTML)
	public Response updateServerConfig(
			@PathParam("servername") String servername,
			@FormParam("protocol") Protocol protocol,
			@FormParam("host") String host,
			@FormParam("port") String port,
			@FormParam("user") String user,
			@FormParam("pass") String pass
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverConfig(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		setAttribute(servername);
		setAttribute(breadCrumbs);
		if (protocol != null) {
			serverRecord.getConfig().getConnectionConfig().setProtocol(
				protocol
			);
		}
		if (host != null) {
			serverRecord.getConfig().getConnectionConfig().setHost(host);
		}
		if (port != null) {
			int iPort = Integer.parseInt(port);
			serverRecord.getConfig().getConnectionConfig().setPort(iPort);
		}
		if (user != null) {
			serverRecord.getConfig().getCredentials().setUser(user);
		}
		if (pass != null) {
			serverRecord.getConfig().getCredentials().setPass(pass);
		}
		URI uri = new URI("/servers/" + servername + "/config");
		return Response.seeOther(uri).build();
	}
	
	
	@GET
	@Path("{servername}")
	@Produces(TEXT_HTML)
	public Viewable getServer(
		@PathParam("servername") String servername
	)
		throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverStatus(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		setAttribute(servername);
		setAttribute(breadCrumbs);
		Viewable viewable = new Viewable("/jsp/server/status.jsp", null);
		return viewable;
	}
	
	@GET
	@Path("{servername}/charts")
	@Produces(TEXT_HTML)
	public Viewable getServerCharts(
		@PathParam("servername") String servername
	)
		throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverCharts(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		setAttribute(servername);
		setAttribute(breadCrumbs);
		Viewable viewable = new Viewable("/jsp/server/charts.jsp", null);
		return viewable;
	}	
	
	@GET
	@Path("{servername}/incidents")
	@Produces(TEXT_HTML)
	public Viewable getIncidents(
			@PathParam("servername") String servername
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverIncidents(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Incidents", "incidents");
		final ServerRecord serverRecord = getServerRepository().get(servername);
//		LOGGER.log(Level.INFO, "serverRecord: " + serverRecord);
		setAttribute(serverRecord);
		request.setAttribute("serverFilter", servername);
		setAttribute(breadCrumbs);
		return new Viewable("/jsp/server/incidents.jsp", null);
	}
	
//	@GET
//	@Path("{servername}/collectors")
//	@Produces(TEXT_HTML)
//	public Viewable getCollectors(
//			@PathParam("servername") String servername
//	)
//			throws Exception
//	{
//		AbstractResourceMethod method = uriInfo.getMatchedMethod();
//		if (method != null) {
//			Annotation[] annotations = method.getDeclaredAnnotations();
//			if (annotations != null) {
//				for (Annotation annotation : annotations) {
//					if (annotation != null) {
////						log(annotation.toString());
//					}
//				}
//			}
//			
//		}
////		uriInfo.getBaseUriBuilder()
////		uriInfo.getRequestUriBuilder()
////		UriBuilder builder = uriInfo.getRequestUriBuilder();
////		log(builder.build(servername).toString());
//		setAttribute(Widgets.server(servername).select(Widget.serverCollectors(servername)));
//		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Collectors", "collectors");
//		setAttribute(breadCrumbs);
//		request.setAttribute("serverFilter", servername);
//		final ServerRecord serverRecord = getServerRepository().get(servername);
//		setAttribute(serverRecord);
//		Iterable<CollectorRecord> collectors = serverRecord.getCollectors();
//		if (Iterables.isNullOrEmpty(collectors)) {
//			collectors = null;
//		}
//		setAttribute(serverRecord, null);
//		DefaultCollectorsModel model = new DefaultCollectorsModel(serverRecord);
//		setAttribute(model, CollectorsModel.class);
//		return new Viewable("/jsp/server/collectors.jsp", null);
//	}
	
	@GET
	@Path("{servername}/collectors/{collectorname}")
	@Produces(TEXT_HTML)
	public Viewable getCollector(
			@PathParam("servername") String servername,
			@PathParam("collectorname") String collectorname
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverCollectors(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Collectors", "collectors").add(collectorname);
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		return new Viewable("/jsp/collector/collector.jsp", null);
	}
	
	@GET
	@Path("{servername}/agents")
	@Produces(TEXT_HTML)
	public Viewable getAgents(
			@PathParam("servername") String servername
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverAgents(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Agents", "agents");
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		return new Viewable("/jsp/server/agents.jsp", null);
	}
	
	@GET
	@Path("{servername}/agents/{agentname}")
	@Produces(TEXT_HTML)
	public Viewable getAgent(
			@PathParam("servername") String servername,
			@PathParam("agentname") String agentname
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverAgents(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Agents", "agents").add(agentname);
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		return new Viewable("/jsp/agent/agent.jsp", null);
	}
	
	@GET
	@Path("{servername}/profiles")
	@Produces(TEXT_HTML)
	public Viewable getProfiles(
			@PathParam("servername") String servername
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverProfiles(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("System Profiles", "profiles");
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		return new Viewable("/jsp/server/profiles.jsp", null);
	}
	
	@GET
	@Path("{servername}/dashboards")
	@Produces(TEXT_HTML)
	public Viewable getDashboards(
			@PathParam("servername") String servername
	)
			throws Exception
	{
		setAttribute(Widgets.server(servername).select(Widget.serverDashboards(servername)));
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Dashboards", "dashboards");
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		return new Viewable("/jsp/server/dashboards.jsp", null);
	}
	
	@GET
	@Path("{servername}/fixpacks")
	@Produces(TEXT_HTML)
	public Viewable getFixpacks(@PathParam("servername") String servername)
			throws Exception
	{
		Widgets tabs = Widgets.server(servername).select(Widget.serverFixPacks(servername));
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Fixpacks", "fixpacks");
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		ServerModel serverModel = new ServerModel(getContext(servername));
		serverModel.setBreadCrumbs(breadCrumbs);
		serverModel.setTabs(tabs);
		serverModel.setServerRecord(serverRecord);
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Collection<FixPack> nonInstalledFixPacks = fpMgr.getNonInstalledFixPacks(serverRecord.getVersion());
		serverRecord.setVersionOutdated(!Iterables.isNullOrEmpty(nonInstalledFixPacks));
		Collection<FixPack> fixPacks = fpMgr.getFixPacks();
		FixPackActions fixPackActions = new FixPackActions();
		Version serverVersion = serverRecord.getVersion();
		for (FixPack fixPack : fixPacks) {
			if (serverVersion.getMajor() != fixPack.getVersion().getMajor()) {
				continue;
			}
			if (serverVersion.getMinor() != fixPack.getVersion().getMinor()) {
				continue;
			}
			fixPackActions.add(new FixPackAction(fixPack, serverRecord));
		}
		serverModel.setFixPackActions(fixPackActions);
		setAttribute(serverModel);
		return new Viewable("/jsp/server/fixpacks.jsp", serverModel);
	}

	@GET
	@Path("{servername}/fixpacks/{version}")
	@Produces(TEXT_HTML)
	public Viewable getFixpack(
			@PathParam("servername") String servername,
			@PathParam("version") String version
	)
			throws Exception
	{
//		URI baseUri = uriInfo.getBaseUri();
//		LOGGER.log(Level.INFO, "baseUri: " + baseUri);
		Version fixPackVersion = Version.parse(version);
		Widgets tabs = Widgets.server(servername).select(Widget.serverFixPacks(servername));
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = BreadCrumbs.servers().add(servername).add("Fixpacks", "fixpacks").add(version, version);
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		setAttribute(serverRecord);
		ServerModel serverModel = new ServerModel(getContext(servername));
		serverModel.setBreadCrumbs(breadCrumbs);
		serverModel.setTabs(tabs);
		serverModel.setServerRecord(serverRecord);
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Collection<FixPack> fixPacks = fpMgr.getFixPacks();
		FixPackActions fixPackActions = new FixPackActions();
		Version serverVersion = serverRecord.getVersion();
		for (FixPack fixPack : fixPacks) {
			if (!fixPack.equals(fixPackVersion)) {
				continue;
			}
			if (serverVersion.getMajor() != fixPack.getVersion().getMajor()) {
				continue;
			}
			if (serverVersion.getMinor() != fixPack.getVersion().getMinor()) {
				continue;
			}
			fixPackActions.add(new FixPackAction(fixPack, serverRecord));
		}
		serverModel.setFixPackActions(fixPackActions);
		serverModel.setFixPack(fpMgr.getFixPack(fixPackVersion));
		setAttribute(serverModel);
		return new Viewable("/jsp/server/fixpacks.jsp", serverModel);
	}
	
	@GET
	@Path("{servername}/fixpacks/{version}/collectors")
	@Produces(TEXT_HTML)
	public Viewable getFixpackCollectors(
			final @PathParam("servername") String servername,
			final @PathParam("version") String version
	)
			throws Exception
	{
		final Version fixPackVersion = Version.parse(version);
		ExecutionContext ctx = getContext(servername);
		request.setAttribute("serverFilter", servername);
		DefaultCollectorsModel model = new DefaultCollectorsModel(ctx, servername, false) {
			@Override
			protected CollectorCollection retrieveCollectors() {
				CollectorCollection superColl = super.retrieveCollectors();
				return superColl.filter(new Filter<CollectorRecord>() {

					@Override
					public boolean accept(CollectorRecord collector) {
						return !collector.equals(fixPackVersion);
					}
					
				});
			}
			
			@Override
			public Iterable<Link> getBreadCrumbsEx() {
				ArrayList<Link> links = new ArrayList<Link>();
				links.add(new Link("Live", "servers"));
				links.add(new Link("Servers", "servers"));
				links.add(new Link(servername, "servers/" + servername));
				links.add(new Link("FixPacks", "servers/" + servername + "/fixpacks"));
				links.add(new Link(version, "servers/" + servername + "/fixpacks/" + version));
				links.add(new Link("Collectors", "servers/" + servername + "/fixpacks/" + version + "/collectors"));
				return links;
			}
		};
		int collectorCount = model.getCollectorCount();
		if (collectorCount == 0) {
			URI baseUri = uriInfo.getBaseUri();
			URL url = new URL(baseUri.getScheme(), baseUri.getHost(), baseUri.getPort(), baseUri.getPath() + "servers/" + servername + "/fixpacks/" + version);
			response.setHeader("Location", url.toString());
		}
		
		setAttribute(model);
		return new Viewable("/jsp/mom-page.jsp", null);

	}
	
	@POST
	@Path("{servername}/install/fixpacks/{version}")
	public Response installFixPack(
			@PathParam("servername") String servername,
			@PathParam("version") String version
	)
			throws Exception
	{
		ServerContext server = getServerRepository().getServer(servername);
		final ServerRecord serverRecord = getServerRepository().get(servername);
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Version fixPackVersion = Version.parse(version);
		final FixPack fixPack = fpMgr.getFixPack(fixPackVersion);
		FixPackInstallStatus status = new FixPackInstallStatus();
		status.setFixPackVersion(fixPack.getVersion());
		status.setInstallStatus(InstallStatus.SCHEDULED);
		serverRecord.setFixPackInstallStatus(status);
		server.installFixpack(fixPack);
		
//		FixPackStatus.Installing.condition(serverRecord, fixPack).await(100);
		
		URI uri = new URI("/servers/" + servername + "/fixpacks");
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/collectors")
	@Produces(TEXT_HTML)
	public Viewable getCollectors() throws Exception {
		request.setAttribute("key", "value");
		return new Viewable("/jsp/collectors.jsp", null);
	}
	
	@GET
	@Path("/reini")
	@Produces(TEXT_HTML)
	public Viewable reini() throws Exception {
		return new Viewable("/test.jsp", null);
	}
	
	@POST
	@Path("{servername}/restart")
	@Produces(TEXT_HTML)
	public Response restartServer(@PathParam("servername") String servername) {
		String referer = request.getHeader("Referer");
		URL url = toURL(referer);
		ServerContext server = getServerRepository().getServer(servername);
		if (server == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		ServerRecord serverRecord = server.getServerRecord();
		serverRecord.setConnectionStatus(ConnectionStatus.RESTARTSCHEDULED);
		server.restart();
		return Response.seeOther(toURI(url)).build();
	}
	
}