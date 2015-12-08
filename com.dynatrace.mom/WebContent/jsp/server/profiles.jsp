<%@page
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.profiles.SystemProfile,
com.dynatrace.utils.Iterables"
%><%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"
%><!DOCTYPE html>
<html lang="en"><%

	String serverFilter = (String) request.getAttribute("serverFilter");

%>
	<head>
		<title>System Profiles - <%= serverFilter %> - dynaTrace MoM</title>
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
								pageContext.setAttribute("serverRecord", serverRecord);
								Iterable<SystemProfile> profiles = serverRecord.getProfiles();
								if (!Iterables.isNullOrEmpty(profiles)) { %>
								<table style="border: none" id="reload-dashboard-list">
									<thead>
										<tr>
											<th></th>
											<th>name</th>
											<th>recording</th>
										</tr>
									</thead>
									<tbody><%
									%><c:forEach var="profile" items="${serverRecord.profiles}">
										<tr>
											<td><img width="32" height="32" src="${pageContext.request.contextPath}/images/system.gif" /></td>
											<td>${profile.id}</td>
											<td>${profile.recording}</td>
										</tr><%
									%></c:forEach>
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