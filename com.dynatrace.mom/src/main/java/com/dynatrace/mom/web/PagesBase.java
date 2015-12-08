package com.dynatrace.mom.web;

import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.web.base.CorePagesBase;

public abstract class PagesBase extends CorePagesBase {
	
	protected ServerRepository getServerRepository() {
		return (ServerRepository) context.getAttribute(ServerRepository.class.getName());
	}
	
	protected void setAttribute(Object o, Class<?> c) {
		if (o == null) {
			return;
		}
		if (c == null) {
			return;
		}
		super.setAttribute(o, c);
		if (c.equals(Model.class)) {
			request.setAttribute("model", o);
		}
	}

}
