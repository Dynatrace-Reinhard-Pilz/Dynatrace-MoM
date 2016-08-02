<%@page
session="false"
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.mom.runtime.ServerRepository,
com.dynatrace.http.ConnectionStatus,
com.dynatrace.reporting.Availability"
%><!DOCTYPE html>
<html lang="en">
	<head>
		<title>Servers - dynaTrace MoM</title>

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
	</head><%
	String serverFilter = (String) request.getAttribute("serverFilter");
	ServerRepository serverRepository = (ServerRepository) request.getAttribute(ServerRepository.class.getName());
	Collection<ServerRecord> serverRecords = serverRepository.getServerRecords();
%>
	<body data-request-uri="<%= request.getAttribute("javax.servlet.forward.request_uri") %>" data-context-path="${pageContext.request.contextPath}" data-server="<%= serverFilter %>">
<jsp:include page="/jsp/header.jspf" />	
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
					<div class="instance_content">
<!--					
						<div id="instance_tabs" class="instance_tabs ui-tabs ui-widget ui-widget-content ui-corner-all">
							<jsp:include page="/jsp/tabs.jsp" />
						</div>
-->
					<div id="tab_license" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div class="tab_content tab_license">
								<table id="reload-server-list" class="overview-list servers" style="font-family: Arial"><%--
									<thead>
										<tr>
											<th>name</th>
											<th>version</th>
											<th>edition</th>
											<th>incidents</th>
											<th>collectors</th>
											<th>agents</th>
											<th>host</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
--%>
									<tbody><%
								for (ServerRecord serverRecord : serverRecords) {
									if (serverRecord == null) {
										continue;
									}
									pageContext.setAttribute(ServerRecord.class.getName(), serverRecord);
									%><%@ include file="./server/server-row.jspf" %><%
								} %>
									</tbody>
								</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>