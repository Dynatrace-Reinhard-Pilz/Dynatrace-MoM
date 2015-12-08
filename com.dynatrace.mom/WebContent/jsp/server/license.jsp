<%@page import="com.dynatrace.utils.Iterables,
com.dynatrace.license.LicensedAgent,
com.dynatrace.license.LicenseInfo,
com.dynatrace.mom.web.ServerModel,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");
	ServerModel serverModel = (ServerModel) request.getAttribute(ServerModel.class.getName());
	LicenseInfo license = serverModel.getServerRecord().getLicenseInfo();
	String licenseNumber = license.getLicenseNumber();
	String edition = license.getLicenseEdition();
	Collection<LicensedAgent> licensedAgents = license.getLicensedAgents();
	LicensedAgent la = null;
%>
	<head>
		<title>License - <%= serverFilter %> - dynaTrace MoM</title>
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
								<table class="license">
									<colgroup>
										<col width="1" />
										<col width="1"  />
										<col />
									</colgroup>								
									<tbody>
										<tr>
											<td class="tablabel">License Number</td>
											<td><%= licenseNumber %></td>
											<td></td>
										</tr>
										<tr>
											<td class="tablabel">Edition</td>
											<td><%= edition %></td>
											<td></td>
										</tr><%
										if (!Iterables.isNullOrEmpty(licensedAgents)) {
											for (LicensedAgent licensedAgent : licensedAgents) {
												if (licensedAgent == null) {
													continue;
												}
												if (licensedAgent.getCount() <= 0) {
													continue;
												} %>
												<tr>
													<td class="tablabel"><%= licensedAgent.getName() %></td>
													<td><%= licensedAgent.getCount() %></td>
													<td></td>
												</tr><%
											}
										}
										%>
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