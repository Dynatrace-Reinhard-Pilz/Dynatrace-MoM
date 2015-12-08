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
							<div>This Fix Pack can get installed on these servers</div><%
							Collection<ServerRecord> serverRecords = fpm.getServers();
							if (!Iterables.isNullOrEmpty(serverRecords)) { %>
								<table id="reload-server-list">
									<colgroup>
										<col width="50" />
										<col />
										<col />
										<col />
										<col />
										<col />
									</colgroup>								
									<thead>
										<tr>
											<th style="border-bottom: 1px solid black"></th>
											<th style="border-bottom: 1px solid black">name</th>
											<th style="border-bottom: 1px solid black">action</th>
											<th style="border-bottom: 1px solid black">version</th>
											<th style="border-bottom: 1px solid black">edition</th>
										</tr>
									</thead>
									<tbody><%
								for (ServerRecord serverRecord : serverRecords) { %>
									<tr>
										<td style="border-bottom: 1px solid black" width="50"><img width="32" height="32" src="${pageContext.request.contextPath}/images/server.png" /></td>
										<td style="border-bottom: 1px solid black"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>"><%= serverRecord.getName() %></a></td>
										<td style="border-bottom: 1px solid black"><a href="${pageContext.request.contextPath}/fixpacks/<%= fpm.getFixPack().getVersion() %>/install/<%= serverRecord.getName() %>">INSTALL</a></td>
										<td style="border-bottom: 1px solid black"><%= serverRecord.getVersion().toString() %></td>
										<td style="border-bottom: 1px solid black"><%= serverRecord.getLicenseInfo().getLicenseEdition() %></td>
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