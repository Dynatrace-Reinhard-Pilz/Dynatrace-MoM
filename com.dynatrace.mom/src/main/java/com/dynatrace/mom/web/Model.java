package com.dynatrace.mom.web;

import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.mom.web.tabs.Widgets;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.web.base.Link;
import com.dynatrace.web.base.ModelBase;

public abstract class Model implements ModelBase {

	private BreadCrumbs breadCrumbs = null;
	private Widgets tabs = null;
	private final Model parent;
	private final ExecutionContext ctx;
	
	public Model(final Model parent, ExecutionContext ctx) {
		this.parent = parent;
		this.ctx = ctx;
	}
	
	public ExecutionContext getContext() {
		return ctx;
	}
	
	public Model(ExecutionContext ctx) {
		this(null, ctx);
	}
	
	public final BreadCrumbs getBreadCrumbs() {
		if (breadCrumbs == null) {
			if (parent != null) {
				return parent.getBreadCrumbs();
			}
		}
		return breadCrumbs;
	}
	
	public final void setBreadCrumbs(final BreadCrumbs breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}
	
	public final Widgets getTabs() {
		if (tabs == null) {
			if (parent != null) {
				return parent.getTabs();
			}
		}
		return tabs;
	}
	
	public final void setTabs(final Widgets tabs) {
		this.tabs = tabs;
	}

	@Override
	public String getTitle() {
		return "UNDEFINED TITLE";
	}

	@Override
	public String getDetailInclude() {
		return null;
	}

	@Override
	public Iterable<Link> getBreadCrumbsEx() {
		return null;
	}
}
