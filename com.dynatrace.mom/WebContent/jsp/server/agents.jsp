<%@page
session="false"
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
java.lang.Iterable,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.utils.Iterables,
com.dynatrace.agents.AgentInfo,
com.dynatrace.collectors.CollectorInfo,
com.dynatrace.utils.Version,
com.dynatrace.agents.AgentUtils"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");
	ServerRecord serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName());

%>
	<head>
		<title>Agents - <%= serverFilter %> - dynaTrace MoM</title>
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
						<div class="instance_content">
						<div id="tab_incidents" aria-labelledby="ui-id-19" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" aria-expanded="true" aria-hidden="false">
							<div class="tab_content tab_incidents">
							<table id="reload-server-list" class="overview-list servers">
								<tbody><%
								pageContext.setAttribute(ServerRecord.class.getName(), serverRecord);
								%><%@ include file="./server-row.jspf" %>
								</tbody>
							</table><%
								Iterable<AgentInfo> agents =
								serverRecord.getAgents();
								if (!Iterables.isNullOrEmpty(agents)) { %>
								<table id="reload-agent-list" class="overview-list agents"><%--
									<thead>
										<tr>
											<th>Name</th>
											<th>Host</th>
											<th>Version</th>
											<th>System Profile</th>
											<th colspan="2">Collector</th>
										</tr>
									</thead>
--%>
									<tbody><c:forEach var="agent" items="<%= agents.iterator() %>"><%
									AgentInfo agent = (AgentInfo) pageContext.getAttribute("agent");
									if (agent == null) {
										continue;
									} %>
									<tr>
										<td class="icon-column"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/agents/<%= agent.getInstanceName() %>"><%= agent.getName() %></a></td>
										<td>${agent.host}</td>
										<td><% if (!AgentUtils.checkRestartRequired(agent, serverRecord)) { %><img width="16" height="16" style="vertical-align: middle" src="${pageContext.request.contextPath}/images/ok.png" /> <% } %><%= agent.getAgentProperties().getAgentVersion() %></td>
										<td><%= agent.getSystemProfile() %></td>
										<td><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/collectors/<%= agent.getCollectorName() %>"><%= agent.getCollectorName() %></a></td>
										<td></td>
									</tr>
								</c:forEach>
									</tbody>
								</table><%
								} %>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>