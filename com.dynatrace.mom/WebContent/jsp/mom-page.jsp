<%@page
session="false"
import="java.util.Collection,
com.dynatrace.collectors.web.CollectorsModel,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.utils.Iterables,
com.dynatrace.web.base.ModelBase,
java.util.Enumeration,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.mom.runtime.ServerRepository"
%><!DOCTYPE html>
<html lang="en"><%
	ModelBase modelBase = (ModelBase) request.getAttribute(ModelBase.class.getName());
	ServerRepository repo = modelBase.getContext().getAttribute(ServerRepository.class);
	String title = "UNDEFINED";
	if (modelBase != null) {
		title = modelBase.getTitle();
	} else {
		Enumeration<String> attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) { %>
			<div><%= attributeNames.nextElement() %></div><%
		}
	}
	String serverFilter = (String) request.getAttribute("serverFilter");
	
	ServerRecord serverRecord = repo.get(modelBase.getServerName());
	
	%>
	<head>
		<title><%= title %></title>
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/debug.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/icomoon.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/header.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/widgets.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/event_log.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/stack_details.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/components/popup.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/lib/jquery-ui-1.9.0.custom.min.css" />
		<script src="${pageContext.request.contextPath}/scripts/jquery.min.js"></script>
		<script src="${pageContext.request.contextPath}/scripts/utils.js"></script>
		<script src="${pageContext.request.contextPath}/scripts/commands.js"></script>
		<script src="${pageContext.request.contextPath}/scripts/highcharts.js"></script>
	</head>
	<body data-request-uri="<%= request.getAttribute("javax.servlet.forward.request_uri") %>" data-context-path="${pageContext.request.contextPath}" data-server="<%= serverFilter %>">
<jsp:include page="/jsp/header.jspf" />
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
						<div id="tab_incidents" aria-labelledby="ui-id-19" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" aria-expanded="true" aria-hidden="false">
							<div class="tab_content tab_incidents">
								<hr /><table id="reload-server-list" class="overview-list servers">
									<tbody><%
									pageContext.setAttribute(ServerRecord.class.getName(), serverRecord);
									%><%@ include file="./server/server-row.jspf" %>
									</tbody>
								</table><hr /><%
								String include = modelBase.getDetailInclude();
								if (include != null) { %>
									<jsp:include page="<%= include %>" /><%
								} %>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>