package com.dynatrace.mom.web.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class Widgets implements Iterable<Widget> {
	
	private final Collection<Widget> widgets = new ArrayList<Widget>();
	
	public static Widgets server(final String serverName) {
		return Widgets.get(
				Widget.serverStatus(serverName),
				Widget.serverCharts(serverName),
				Widget.serverIncidents(serverName),
				Widget.serverCollectors(serverName),
				Widget.serverAgents(serverName),
				Widget.serverFixPacks(serverName)
		);
	}
	
	public static Widgets createServer() {
		return Widgets.get(Widget.serverNew());
	}
	
	public static Widgets settings() {
		return Widgets.get(Widget.SETTINGS, Widget.USERS, Widget.MODULES);
	}
	
	private Widgets(final Widget ...widgets) {
		if (widgets == null) {
			return;
		}
		for (Widget widget : widgets) {
			if (widget != null) {
				this.widgets.add(widget);
			}
		}
	}
	
	public Widgets select(final Widget widget) {
		Objects.requireNonNull(widget);
		synchronized (widgets) {
			for (Widget w : widgets) {
				w.setSelected(w.equals(widget));
			}
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<Widget> iterator() {
		synchronized (widgets) {
			return new ArrayList<Widget>(widgets).iterator();
		}
	}
	
	public final int size() {
		synchronized (widgets) {
			return widgets.size();
		}
	}
	
	public static Widgets get(final Widget ...widgets) {
		return new Widgets(widgets);
	}

}
