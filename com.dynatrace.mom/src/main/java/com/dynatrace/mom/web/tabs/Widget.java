package com.dynatrace.mom.web.tabs;

public final class Widget implements Cloneable {
	
	public static final Widget ADD_SERVER =
			new Widget(LinkLabel.ADD_SERVER, "/images/license.gif", null);
	
	public static final Widget SETTINGS =
			new Widget(LinkLabel.SETTINGS, "/images/settings.png", null);
	
	public static final Widget USERS =
			new Widget(LinkLabel.USERS, "/images/users.png", null);

	public static final Widget MODULES =
			new Widget(LinkLabel.MODULES, "/images/modules.png", null);
	
	private LinkLabel label = null;
	private String icon = null;
	private String onClick = null;
	private boolean isSelected = false;
	
	public static Widget serverStatus(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).label("Status").cssClass("status"), null, null);
	}
	
	public static Widget serverNew() {
		return new Widget(LinkLabel.SERVERS.build("<new>").label("Config").cssClass("settings"), null, null);
	}
	
	public static Widget serverConfig(String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("config").label("Config").cssClass("settings"), null, null);
	}
	
	public static Widget serverIncidents(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("incidents").label("Incidents").cssClass("incidents"), null, null);
	}

	public static Widget serverCharts(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("charts").label("Charts").cssClass("healthcharts"), null, null);
	}

	public static Widget serverCollectors(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("collectors").label("Collectors").cssClass("collectors"), null, null);
	}

	public static Widget serverAgents(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("agents").label("Agents").cssClass("agents"), null, null);
	}

	public static Widget serverProfiles(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("profiles").label("System Profiles").cssClass("profiles"), null, null);
	}

	public static Widget serverDashboards(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("dashboards").label("Dashboards").cssClass("dashboards"), null, null);
	}

	public static Widget serverFixPacks(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("fixpacks").label("Fixpacks").cssClass("fixpacks"), null, null);
	}

	public static Widget serverLicense(final String serverName) {
		return new Widget(LinkLabel.SERVERS.build(serverName).build("license").label("License").cssClass("license"), null, "license");
	}
	
	public Widget() {
		
	}
	
	public Widget(final LinkLabel label, final String icon, final String onClick) {
		this.label = label;
		this.icon = icon;
		this.onClick = onClick;
	}
	
	public final void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public final boolean isSelected() {
		return isSelected;
	}
	
	public final String getLabel() {
		return label.getLabel();
	}
	
	public final String getLink() {
		return label.getLink();
	}
	
	public final String getCssClass() {
		return label.getCssClass();
	}
	
	public final void setLabel(final LinkLabel label) {
		this.label = label;
	}
	
	public final String getIcon() {
		return icon;
	}
	
	public final void setIcon(final String icon) {
		this.icon = icon;
	}
	
	public final String getOnClick() {
		return onClick;
	}
	
	public final void setOnClick(final String onClick) {
		this.onClick = onClick;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Widget other = (Widget) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Widget clone() {
		try {
			final Widget clone = (Widget) super.clone();
			if (label != null) {
				clone.setLabel(label.clone());
			}
			return clone;
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}

	
}
