package com.dynatrace.mom.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.mom.web.tabs.Widgets;
import com.sun.jersey.api.view.Viewable;

@Path("settings/users")
public class SettingsUserPages extends PagesBase {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getUsers() {
		SettingsModel settingsModel = new SettingsModel(null);
		Widgets tabs = Widgets.get(SettingsModel.TAB_SETTINGS, SettingsModel.TAB_USERS).select(SettingsModel.TAB_USERS);
		final BreadCrumbs breadCrumbs = new BreadCrumbs("Settings", "settings").add("User Management", "users");
		settingsModel.setBreadCrumbs(breadCrumbs);
		settingsModel.setTabs(tabs);
		setAttribute(settingsModel);
		return new Viewable("/jsp/settings/users.jsp", settingsModel);
	}
}
