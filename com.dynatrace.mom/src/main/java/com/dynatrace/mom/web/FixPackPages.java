package com.dynatrace.mom.web;

import java.util.Collection;
import java.util.Objects;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackManager;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.mom.web.tabs.LinkLabel;
import com.dynatrace.mom.web.tabs.Widget;
import com.dynatrace.mom.web.tabs.Widgets;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;
import com.sun.jersey.api.view.Viewable;

@Path("fixpacks")
public class FixPackPages extends PagesBase {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(FixPackPages.class.getName());
	
	private static final LinkLabel LINK_LABEL_FIXPACKS = new LinkLabel("FixPacks", "fixpacks", "fixpacks");
	private static final LinkLabel LINK_LABEL_UPLOAD = LINK_LABEL_FIXPACKS.build("add").label("Add FixPack").cssClass("fixpacks");
	private static final LinkLabel LINK_LABEL_FIXPACK_CONTENTS = LINK_LABEL_FIXPACKS.build("fixpacks").label("Contents").cssClass("fixpacks");
	private static final LinkLabel LINK_LABEL_FIXPACK_INSTALL = LINK_LABEL_FIXPACKS.build("install").label("Install").cssClass("fixpacks");
	private static final LinkLabel LINK_LABEL_FIXPACK_UNINSTALL = LINK_LABEL_FIXPACKS.build("uninstall").label("Uninstall").cssClass("fixpacks");
	private static final Widget fixPacksTab = new Widget(LINK_LABEL_FIXPACKS, "fixpacks.png", null);
	private static final Widget uploadTab = new Widget(LINK_LABEL_UPLOAD, "fixpacks.png", null);
	@SuppressWarnings("unused")
	private static final Widget fixPackContentsTab = new Widget(LINK_LABEL_FIXPACK_CONTENTS, "fixpacks.png", null);
	@SuppressWarnings("unused")
	private static final Widget fixPackInstallTab = new Widget(LINK_LABEL_FIXPACK_INSTALL, "fixpacks.png", null);
	@SuppressWarnings("unused")
	private static final Widget fixPackUninstallTab = new Widget(LINK_LABEL_FIXPACK_UNINSTALL, "fixpacks.png", null);
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getFixpacks() throws Exception {
		String servername = null;
		Widgets tabs = Widgets.get(fixPacksTab, uploadTab).select(fixPacksTab);
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Fixpacks", "fixpacks");
		setAttribute(breadCrumbs);
		request.setAttribute("serverFilter", servername);
		final ServerRecord serverRecord = null;
		ServerModel serverModel = new ServerModel(null);
		serverModel.setBreadCrumbs(breadCrumbs);
		serverModel.setTabs(tabs);
		serverModel.setServerRecord(serverRecord);
//		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
//		serverModel.setInstalledFixPacks(fpMgr.getInstalledFixPacks(serverRecord.getVersion()));
//		serverModel.setNonInstalledFixPacks(fpMgr.getNonInstalledFixPacks(serverRecord.getVersion()));
		setAttribute(serverModel);
		return new Viewable("/jsp/fixpack/fixpacks.jsp", serverModel);
	}
	
	@GET
	@Path("{version}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getFixpack(
			final @PathParam("version") String version
	)
			throws Exception
	{
		LinkLabel installLabel = LINK_LABEL_FIXPACKS.build(version).build("install").label("Install").cssClass("fixpacks");
		Widget installTab = new Widget(installLabel, "fixpacks.png", null);
		LinkLabel unInstallLabel = LINK_LABEL_FIXPACKS.build(version).build("uninstall").label("Uninstall").cssClass("fixpacks");
		Widget unInstallTab = new Widget(unInstallLabel, "fixpacks.png", null);
		LinkLabel contentsLabel = LINK_LABEL_FIXPACKS.build(version).cssClass("fixpacks");
		Widget contentsTab = new Widget(contentsLabel, "fixpacks.png", null);
		final Widgets tabs = Widgets.get(contentsTab, installTab, unInstallTab).select(contentsTab);
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Fixpacks", "fixpacks").add(version);
		setAttribute(breadCrumbs);
		
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Version fixPackVersion = Version.parse(version);
		FixPack fixPack = fpMgr.getFixPack(fixPackVersion);
		FixPackModel fixPackModel = new FixPackModel(fixPack, null);
		fixPackModel.setBreadCrumbs(breadCrumbs);
		fixPackModel.setTabs(tabs);
		setAttribute(fixPackModel);
		
		return new Viewable("/jsp/fixpack/fixpack.jsp", null);
	}
	
	@GET
	@Path("{version}/install")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getFixpackInstallPage(
			final @PathParam("version") String version
	)
			throws Exception
	{
		LinkLabel installLabel = LINK_LABEL_FIXPACKS.build(version).build("install").label("Install").cssClass("fixpacks");
		Widget installTab = new Widget(installLabel, "fixpacks.png", null);
		LinkLabel unInstallLabel = LINK_LABEL_FIXPACKS.build(version).build("uninstall").label("Uninstall").cssClass("fixpacks");
		Widget unInstallTab = new Widget(unInstallLabel, "fixpacks.png", null);
		LinkLabel contentsLabel = LINK_LABEL_FIXPACKS.build(version).cssClass("fixpacks");
		Widget contentsTab = new Widget(contentsLabel, "fixpacks.png", null);
		final Widgets tabs = Widgets.get(contentsTab, installTab, unInstallTab).select(installTab);
		
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Fixpacks", "fixpacks").add(version).add("Install", "install");
		setAttribute(breadCrumbs);
		
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Version fixPackVersion = Version.parse(version);
		FixPack fixPack = fpMgr.getFixPack(fixPackVersion);
		FixPackModel fixPackModel = new FixPackModel(fixPack, null);
		fixPackModel.setBreadCrumbs(breadCrumbs);
		fixPackModel.setTabs(tabs);
		
		ServerRepository serverRepository = getServerRepository();
		Collection<ServerRecord> serverRecords = serverRepository.getServerRecords();
		if (!Iterables.isNullOrEmpty(serverRecords)) {
			for (ServerRecord serverRecord : serverRecords) {
				Version serverVersion = serverRecord.getVersion();
				if (!serverVersion.includes(fixPackVersion)) {
					fixPackModel.addServer(serverRecord);
				}
			}
		}
		
		setAttribute(fixPackModel);
		
		return new Viewable("/jsp/fixpack/install.jsp", null);
	}
	
	@GET
	@Path("{version}/install/{servername}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getInstallOnServer(
			final @PathParam("version") String version,
			final @PathParam("servername") String servername
	)
			throws Exception
	{
		LinkLabel installLabel = LINK_LABEL_FIXPACKS.build(version).build("install").label("Install").cssClass("fixpacks");
		Widget installTab = new Widget(installLabel, "fixpacks.png", null);
		LinkLabel unInstallLabel = LINK_LABEL_FIXPACKS.build(version).build("uninstall").label("Uninstall").cssClass("fixpacks");
		Widget unInstallTab = new Widget(unInstallLabel, "fixpacks.png", null);
		LinkLabel contentsLabel = LINK_LABEL_FIXPACKS.build(version).cssClass("fixpacks");
		Widget contentsTab = new Widget(contentsLabel, "fixpacks.png", null);
		final Widgets tabs = Widgets.get(contentsTab, installTab, unInstallTab).select(installTab);
		
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Fixpacks", "fixpacks").add(version).add("Install", "install").add(servername);
		setAttribute(breadCrumbs);
		
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Version fixPackVersion = Version.parse(version);
		FixPack fixPack = fpMgr.getFixPack(fixPackVersion);
		FixPackModel fixPackModel = new FixPackModel(fixPack, null);
		fixPackModel.setBreadCrumbs(breadCrumbs);
		fixPackModel.setTabs(tabs);
		
		ServerRepository serverRepository = getServerRepository();
		ServerRecord serverRecord = serverRepository.get(servername);
		fixPackModel.addServer(serverRecord);
		
		setAttribute(fixPackModel);
		
		return new Viewable("/jsp/fixpack/installonserver.jsp", null);
	}	

	@GET
	@Path("{version}/uninstall")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getFixpackUnInstallPage(
			final @PathParam("version") String version
	)
			throws Exception
	{
		LinkLabel installLabel = LINK_LABEL_FIXPACKS.build(version).build("install").label("Install").cssClass("fixpacks");
		Widget installTab = new Widget(installLabel, "fixpacks.png", null);
		LinkLabel unInstallLabel = LINK_LABEL_FIXPACKS.build(version).build("uninstall").label("Uninstall").cssClass("fixpacks");
		Widget unInstallTab = new Widget(unInstallLabel, "fixpacks.png", null);
		LinkLabel contentsLabel = LINK_LABEL_FIXPACKS.build(version).cssClass("fixpacks");
		Widget contentsTab = new Widget(contentsLabel, "fixpacks.png", null);
		final Widgets tabs = Widgets.get(contentsTab, installTab, unInstallTab).select(unInstallTab);

		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Fixpacks", "fixpacks").add(version).add("Install", "install");
		setAttribute(breadCrumbs);
		
		FixPackManager fpMgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
		Version fixPackVersion = Version.parse(version);
		FixPack fixPack = fpMgr.getFixPack(fixPackVersion);
		FixPackModel fixPackModel = new FixPackModel(fixPack, null);
		fixPackModel.setBreadCrumbs(breadCrumbs);
		fixPackModel.setTabs(tabs);
		
		ServerRepository serverRepository = getServerRepository();
		Collection<ServerRecord> serverRecords = serverRepository.getServerRecords();
		if (!Iterables.isNullOrEmpty(serverRecords)) {
			for (ServerRecord serverRecord : serverRecords) {
				Version serverVersion = serverRecord.getVersion();
				if (serverVersion.includes(fixPackVersion)) {
					fixPackModel.addServer(serverRecord);
				}
			}
		}
		
		setAttribute(fixPackModel);
		
		return new Viewable("/jsp/fixpack/uninstall.jsp", null);
	}	
	
	@GET
	@Path("{version}/servers/{servername}")
	@Produces(MediaType.TEXT_HTML)
	public Response getFixpack(
			final @PathParam("version") String version,
			final @PathParam("servername") String servername
	)
			throws Exception
	{
		return ok(version, servername);
	}
	
	@GET
	@Path("upload")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getFixpack() throws Exception {
		final Widgets tabs = Widgets.get(fixPacksTab, uploadTab).select(uploadTab);
		setAttribute(tabs);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("fixpacks").add("Upload", "upload");
		setAttribute(breadCrumbs);
		
//		ServerRecord serverRecord = getServerRepository().get("52.5.210.190:8021");
//		FixPackManager fpmgr = (FixPackManager) context.getAttribute(FixPackManager.class.getName());
//		FixPackUploadEvent event = new FixPackUploadEvent(serverRecord, fpmgr.getFixPack(Version.parse("6.1.0.8191")));
//		EventBus eventBus = (EventBus) context.getAttribute(EventBus.class.getName());
//		eventBus.offer(event);
		return new Viewable("/jsp/fixpack/upload.jsp", null);
	}
	
	private static Response ok(final String... parts) {
		Objects.requireNonNull(parts);
		final StringBuilder sb = new StringBuilder("fixpacks");
		for (String part : parts) {
			sb.append(Strings.SLASH).append(part);
		}
		return Response.ok(sb.toString()).build();
	}
}