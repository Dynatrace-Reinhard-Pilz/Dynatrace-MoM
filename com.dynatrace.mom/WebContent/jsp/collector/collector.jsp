<%@page
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
java.lang.Iterable,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.utils.Iterables"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");

%>
	<head>
		<title>Collectors - <%= serverFilter %> - dynaTrace MoM</title>
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
							<div id="instance_tabs" class="instance_tabs ui-tabs ui-widget ui-widget-content ui-corner-all">
<jsp:include page="/jsp/tabs.jsp" />	
						</div>
						<div id="tab_incidents" aria-labelledby="ui-id-19" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" aria-expanded="true" aria-hidden="false">
							<div class="tab_content tab_incidents"><%
								ServerRecord serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName());
								Iterable<CollectorRecord> collectors = serverRecord.getCollectors();
								if (!Iterables.isNullOrEmpty(collectors)) { %>
								<table id="reload-collector-list">
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
											<th style="border-bottom: 1px solid black">host</th>
											<th style="border-bottom: 1px solid black">version</th>
											<th style="border-bottom: 1px solid black">server</th>
											<th style="border-bottom: 1px solid black">status</th>
										</tr>
									</thead>
									<tbody><%
								for (CollectorRecord collector : collectors) { %>
									<tr>
										<td style="border-bottom: 1px solid black" width="50"><img width="32" height="32" src="${pageContext.request.contextPath}/images/collector.png" /></td>
										<td style="border-bottom: 1px solid black"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/collectors/<%= collector.getName() %>"><%= collector.getName() %></a></td>
										<td style="border-bottom: 1px solid black"><%= collector.getHost() %></td>
										<td style="border-bottom: 1px solid black"><%= collector.getVersion() %></td>
										<td style="border-bottom: 1px solid black"><a href="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>"><%= serverRecord.getName() %></a></td>
										<td style="border-bottom: 1px solid black"><%= collector.getRestartStatus() %></td>
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