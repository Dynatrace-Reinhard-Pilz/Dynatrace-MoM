package com.dynatrace.mom.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.mom.web.tabs.Widgets;
import com.sun.jersey.api.view.Viewable;

@Path("settings")
public class SettingsPages extends PagesBase {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getSettings() {
		SettingsModel settingsModel = new SettingsModel(null);
		Widgets tabs = Widgets.get(SettingsModel.TAB_SETTINGS, SettingsModel.TAB_USERS).select(SettingsModel.TAB_SETTINGS);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Settings", "settings");
		settingsModel.setBreadCrumbs(breadCrumbs);
		settingsModel.setTabs(tabs);
		setAttribute(settingsModel);
		return new Viewable("/jsp/settings/settings.jsp", settingsModel);
	}
	
}
