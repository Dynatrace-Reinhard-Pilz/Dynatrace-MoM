<%@page
session="false"
import="com.dynatrace.fixpacks.FixPackStatus,
com.dynatrace.fixpacks.FixPackInstallStatus,
com.dynatrace.mom.web.ServerModel,
com.dynatrace.fixpacks.FixPackManager,
com.dynatrace.fixpacks.FixPack,
com.dynatrace.fixpacks.FixPackInfo,
com.dynatrace.fixpacks.Fix,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.http.ConnectionStatus,
com.dynatrace.utils.Iterables,
com.dynatrace.utils.Version,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.collectors.RestartStatus,
com.dynatrace.mom.runtime.components.FixPackAction,
com.dynatrace.mom.runtime.components.FixPackActions,
com.dynatrace.fixpacks.InstallStatus"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");

%>
	<head>
		<title>Fixpacks - <%= serverFilter %> - dynaTrace MoM</title>
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
							<div class="tab_content tab_incidents"><%
								ServerModel serverModel = (ServerModel) request.getAttribute(ServerModel.class.getName());
							%>
							<table id="reload-server-list" class="overview-list servers">
								<tbody><%
								pageContext.setAttribute(ServerRecord.class.getName(), serverModel.getServerRecord());
								%><%@ include file="./server-row.jspf" %>
								</tbody>
							</table><%
							
							FixPackActions fixPackActions = serverModel.getFixPackActions();
							
							ServerRecord serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName()); 
							
							
							%>
								<div id="reload-fixpack-list">
								<table class="overview-list fixpacks">
									<tbody><%
								boolean isRestarting = serverRecord.isRestarting();
								for (FixPackAction fixPackAction : fixPackActions) {
									if (!fixPackActions.isVisible(fixPackAction)) {
										continue;
									}
									InstallStatus installStatus = fixPackAction.getInstallStatus();
									if (installStatus == InstallStatus.NOTINSTALLED) { %>
										<tr>
											<td class="icon-column" style="width: 1px"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/fixpacks/<%= fixPackAction %>"><%= fixPackAction %></a></td>
											<td><%
											%><form style="display: inline" method="post" action="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/install/fixpacks/<%= fixPackAction %>"><%
												String label = "Install";
												if (isRestarting) {
													label = "Restarting";
												}
												%><button class="btn btn-blue btn-compact failover_button<%
														if (isRestarting) { %> busy<% }
														if (!fixPackActions.isEnabled(fixPackAction)) { %> disabled" disabled="disabled<% } %>"><%= label %></button><%
											%></form><%
										%></td>
										</tr><%
									} else if (installStatus == InstallStatus.COLLECTORRESTARTREQUIRED) { %>
										<tr>
											<td class="icon-column" style="width: 1px"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/fixpacks/<%= fixPackAction %>"><%= fixPackAction %></a></td>
											<td><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/fixpacks/<%= fixPackAction %>/collectors">Collector Restart required</a></td>
										</tr><%
									} else { %>
										<tr>
											<td class="icon-column" style="width: 1px"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/fixpacks/<%= fixPackAction %>"><%= fixPackAction %></a></td>
											<td>
												<form style="display: inline" method="post" action="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/restart"><%
												String label = fixPackAction.getInstallStatus().toString();
												if (isRestarting) {
													label = "Restarting";
												} else if (installStatus == InstallStatus.INSTALLING) {
													label = "Installing";
												} else if (installStatus == InstallStatus.SCHEDULED) {
													label = "Installation Scheduled";
												} else if (installStatus == InstallStatus.RESTARTREQUIRED) {
													label = "Restart Required";
												}
												String css = "btn btn-blue btn-compact failover_button";
												if (isRestarting || (installStatus == InstallStatus.INSTALLING) || (installStatus == InstallStatus.UPLOADING) || (installStatus == InstallStatus.SCHEDULED)) {
													css = css + " busy";
												}
												String disabled = "";
												if (!fixPackActions.isEnabled(fixPackAction)) {
													css = css + " disabled";
													disabled = "disabled=\"disabled\"";
												} %>
												<button class="<%= css %>"<%= disabled %>><%= label %></button>
												</form></td>
										</tr><%
										}
									} %>
									</tbody>
								</table><%
									FixPack fp = serverModel.getFixPack();
									if (fp != null) {
									FixPackInfo fpi = fp.getFixPackInfo();
									Collection<Fix> fixes = fpi.getFixes();
									if (!Iterables.isNullOrEmpty(fixes)) { %>
									<table style="font-family: Courier New; font-size: 110%">
										<tbody><%
										for (Fix fix : fixes) { %>
											<tr>
												<td style="white-space: nowrap; vertical-align: top"><%= fix.getId() %></td>
												<td><%= fix.getDescription() %></td>
											</tr><%
										} %>
										</tbody>
									</table><%
									}
									} %>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>