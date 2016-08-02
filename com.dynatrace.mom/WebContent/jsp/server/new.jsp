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
//	String serverFilter = (String) request.getAttribute("serverFilter");
	ServerRepository serverRepository = (ServerRepository) request.getAttribute(ServerRepository.class.getName());
	Collection<ServerRecord> serverRecords = serverRepository.getServerRecords();
%>
	<body data-request-uri="<%= request.getAttribute("javax.servlet.forward.request_uri") %>" data-context-path="${pageContext.request.contextPath}">
<jsp:include page="/jsp/header.jspf" />	
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
					<div class="instance_content">
					<div id="tab_license" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div class="tab_content tab_license">
							<form name="frmcreateserver" id="frmcreateserver" method="post" action="#" style="display: inline">
							<table>
								<tr>
									<td><input id="host" type="text" value="" placeholder="host[:port]" /></td>
								</tr>
								<tr>
									<td><input id="user" type="text" value="" placeholder="username" autocomplete="false" /></td>
								</tr>
								<tr>
									<td><input id="pass" type="password" value="" placeholder="password" autocomplete="new-password" /></td>
								</tr>
								<tr>
									<td>Would you like to monitor the Performance Warehouse directly?</td>
								</tr>
								<tr>
									<td><select id="pwhtype" name="pwhtype">
										<option value="none">Don't monitor the PWH</option>
										<option value="mssql">SQL Server</option>
										<option value="PostgreSQL">PostgreSQL</option>
									</select></td>
								</tr>
								<tr>
									<td><input id="pwhhost" type="text" value="" placeholder="host[:port]" /></td>
								</tr>
								<tr>
									<td><input id="pwhuser" type="text" value="" placeholder="username" autocomplete="false" /></td>
								</tr>
								<tr>
									<td><input id="pwhpass" type="password" value="" placeholder="password" autocomplete="new-password" /></td>
								</tr>
							</table>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>