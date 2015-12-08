 <%@page
 session="false"
 import="com.dynatrace.utils.Iterables,
 java.util.Iterator,
 com.dynatrace.web.base.Link,
 com.dynatrace.web.base.ModelBase,
 com.dynatrace.mom.web.Model,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.http.ConnectionStatus,
com.dynatrace.mom.web.breadcrumbs.BreadCrumbs"
%><%
	ServerRecord serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName());
ConnectionStatus serverStatus = ConnectionStatus.OFFLINE;
	if (serverRecord != null) {
		serverStatus = serverRecord.getConnectionStatus();
	}
	BreadCrumbs breadCrumbs = null;
	Model model = (Model) request.getAttribute(Model.class.getName());
	if (model != null) {
		breadCrumbs = model.getBreadCrumbs();
	}
	if (breadCrumbs == null) {
		breadCrumbs = (BreadCrumbs) request.getAttribute(BreadCrumbs.class.getName());
	}
	ModelBase modelBase = (ModelBase) request.getAttribute(ModelBase.class.getName());
	
	if ((breadCrumbs != null) && ((modelBase == null) || (modelBase.getBreadCrumbsEx() == null))) { %>
		<div id="bread-crumbs"><%
		for (BreadCrumbs crumb : breadCrumbs) { %>
			<div class="wrapper">
				<div class="crumb<%= crumb.hasNext() ? "": " on" %>"><%
					String l = crumb.getLink();
					if (l == null) {
						l = "";
					}
					%><a href="${pageContext.request.contextPath}/<%= l  %>"><span class="text"><%= crumb.getLabel() %></span></a><%
				%></div>
			</div><%
			if (crumb.hasNext()) {
				%><div class="wrapper"><div class="arrow-right"></div></div><%
			}
		}
		if (serverRecord != null) {
			%><jsp:include page="/jsp/serveractions.jspf" /><%
		}
		%></div><%		
	}
	if (modelBase != null) {
		Iterable<Link> links = modelBase.getBreadCrumbsEx();
		if (!Iterables.isNullOrEmpty(links)) { %>
		<div id="bread-crumbs"><%
			for (Iterator<Link> it = links.iterator(); it.hasNext();) {
				Link link = it.next();
				String url = link.getUrl();
				String cssClass = "crumb on";
				if (it.hasNext()) {
					cssClass = "crumb";
				} %>
				<div class="wrapper">
					<div class="<%= cssClass %>">
						<a href="${pageContext.request.contextPath}/<%= url %>"><span class="text"><%= link.getText() %></span></a>
					</div>
				</div><%
				if (it.hasNext()) { %>
					<div class="wrapper"><div class="arrow-right"></div></div><%
				}				
			}
			if (serverRecord != null) {
				%><jsp:include page="/jsp/serveractions.jspf" /><%
			}
		%></div><%
		}
	}	
%>