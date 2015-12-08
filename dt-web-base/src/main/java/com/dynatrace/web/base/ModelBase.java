package com.dynatrace.web.base;

import com.dynatrace.utils.ExecutionContext;

public interface ModelBase {

	String getServerName();
	String getTitle();
	String getDetailInclude();
	Iterable<Link> getBreadCrumbsEx();
	ExecutionContext getContext();
}
