<%@page
session="false"
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.utils.Strings,
java.util.HashMap,
java.util.ArrayList,
com.dynatrace.incidents.IncidentRule,
com.dynatrace.incidents.IncidentReference,
com.dynatrace.incidents.Incident,
com.dynatrace.incidents.IncidentState,
java.text.SimpleDateFormat,
java.util.Date,
com.dynatrace.utils.Iterables"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");
	ServerRecord serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName());
%>
	<head>
		<title>Incidents - <%= serverFilter %> - dynaTrace MoM</title>
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
						<div id="tab_incidents" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
								<table id="reload-server-list" class="overview-list servers">
									<tbody><%
									pageContext.setAttribute(ServerRecord.class.getName(), serverRecord);
									%><%@ include file="./server-row.jspf" %>
									</tbody>
								</table>
								<div class="tab_content tab_incidents" id="reload-server-incidents">
								<table class="incident-table" style="font-size: 90%; font-family: raleway">
									<colgroup>
										<col style="width: 30em"  />
										<col />
										<col style="width: 80px" />
									</colgroup>								
									<tbody><%
									if (serverRecord != null) {
									Iterable<IncidentRule> incidentRules = serverRecord.getIncidentRules();
									
									if (!Iterables.isNullOrEmpty(incidentRules)) {
										for (IncidentRule incidentRule : incidentRules) {
											if (Iterables.isNullOrEmpty(incidentRule)) {
												continue;
											}
											if (incidentRule.getOpenIncidentCount() == 0) {
												continue;
											} %>
											<tr>
												<td colspan="3" class="incident-title <%= incidentRule.getSeverity() %>" colspan="5"><%= incidentRule.getSizedName() %></td>
											</tr><%
											for (IncidentReference incidentReference : incidentRule) {
												if (incidentReference == null) {
													continue;
												}
												Incident incident = incidentReference.getIncident();
												if (incident == null) {
													continue; 
												}
												if (!incident.isOpen()) {
													continue;
												}	%>
										<tr>
											<td class="timestamps"><%= incident.getFormattedTime() %></td>
											<td><%= incident.getMessage() %></td>
											<td class="form"><%
												if (incident.getState() != IncidentState.Confirmed) {
													%><form method="post" action="#"><%--
														--%><button class="btn btn-blue btn-compact failover_button" onclick="this.form.submit();">Confirm</button><%--
													--%></form><%
												}
											%></td>
										</tr><%
											}
										}
									}
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