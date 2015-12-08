package com.dynatrace.mom.web;

import com.dynatrace.mom.web.tabs.LinkLabel;
import com.dynatrace.mom.web.tabs.Widget;

public class SettingsModel extends Model {
	
	private final String serverName;
	
	public SettingsModel(String serverName) {
		super(null);
		this.serverName = serverName;
	}
	
	@Override
	public String getServerName() {
		return serverName;
	}

	public static final LinkLabel LINK_LABEL_SETTINGS = new LinkLabel("Settings", "settings", "settings");
	public static final LinkLabel LINK_LABEL_USERS = LINK_LABEL_SETTINGS.build("users").label("User Management").cssClass("users");
	public static final Widget TAB_SETTINGS = new Widget(LINK_LABEL_SETTINGS, "settings.png", null);
	public static final Widget TAB_USERS = new Widget(LINK_LABEL_USERS, "users.png", null);

}
