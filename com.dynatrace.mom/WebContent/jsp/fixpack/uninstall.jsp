<%@page
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.utils.Iterables,
com.dynatrace.fixpacks.FixPackInfo,
com.dynatrace.fixpacks.FixPack,
com.dynatrace.mom.web.FixPackModel,
com.dynatrace.fixpacks.Fix"
%><!DOCTYPE html>
<html lang="en"><%
	String serverFilter = (String) request.getAttribute("serverFilter");
	FixPackModel fpm = (FixPackModel) request.getAttribute(FixPackModel.class.getName());
%>
	<head>
		<title>Fixpack - <%= fpm.getFixPack().getVersion() %> - dynaTrace MoM</title>
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
<jsp:include page="/jsp/header.jsp" />	
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
						<div class="instance_content">
							<div id="instance_tabs" class="instance_tabs ui-tabs ui-widget ui-widget-content ui-corner-all">
<jsp:include page="/jsp/tabs.jsp" />	
						</div>
						<div id="tab_incidents" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
							<div class="tab_content tab_incidents">
							<div>This Fix Pack is currently installed (or covered by another fix pack) on the following servers</div><%
							Collection<ServerRecord> servers = fpm.getServers();
							if (!Iterables.isNullOrEmpty(servers)) { %>
								<table style="font-family: Courier New; font-size: 110%">
									<tbody><%
									for (ServerRecord serverRecord : servers) { %>
										<tr>
											<td><%= serverRecord.getName() %></td>
											<td><%= serverRecord.getVersion() %></td>
										</tr><%
									} %>
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