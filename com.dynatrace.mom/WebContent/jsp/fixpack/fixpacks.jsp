<%@page import="com.dynatrace.mom.web.ServerModel,
com.dynatrace.fixpacks.FixPackManager,
com.dynatrace.fixpacks.FixPack,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.utils.Iterables"
%><!DOCTYPE html>
<html lang="en">
	<head>
		<title>Fixpacks - dynaTrace MoM</title>
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
	<body data-request-uri="<%= request.getAttribute("javax.servlet.forward.request_uri") %>" data-context-path="${pageContext.request.contextPath}" data-server="null">
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
						<div id="tab_incidents" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
							<div class="tab_content tab_incidents"><%
								FixPackManager fpmgr = (FixPackManager) request.getServletContext().getAttribute(FixPackManager.class.getName());
								Collection<FixPack> fixPacks = fpmgr.getFixPacks();
								if (!Iterables.isNullOrEmpty(fixPacks)) { %>
								<table id="reload-fixpack-list">
									<colgroup>
										<col width="50" />
										<col />
									</colgroup>								
									<thead>
										<tr>
											<th style="border-bottom: 1px solid black"></th>
											<th style="border-bottom: 1px solid black" align="left" style="text-align: left">version</th>
										</tr>
									</thead>
									<tbody><%								
									for (FixPack fixPack : fixPacks) { %>
										<tr>
											<td style="border-bottom: 1px solid black; text-align: center; width: 50px"><img width="32" height="32" src="${pageContext.request.contextPath}/images/fixpacks.png" /></td>
											<td style="border-bottom: 1px solid black; text-align: left"><a href="${pageContext.request.contextPath}/fixpacks/<%= fixPack.getVersion() %>"><%= fixPack.getVersion() %></a></td>
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