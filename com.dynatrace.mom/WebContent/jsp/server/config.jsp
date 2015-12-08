<%@page import="com.dynatrace.http.Protocol,
com.dynatrace.http.config.Credentials,
com.dynatrace.http.config.ConnectionConfig,
com.dynatrace.mom.web.ServerModel,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.http.config.ServerConfig"
%><!DOCTYPE html>
<html style="height: 100%"><%

	String serverFilter = null;
	ServerRecord serverRecord = null;
	ServerModel serverModel = (ServerModel) request.getAttribute(ServerModel.class.getName());
	if (serverModel != null) {
		serverRecord = serverModel.getServerRecord();
	}
	if (serverRecord == null) {
		serverRecord = (ServerRecord) request.getAttribute(ServerRecord.class.getName());
	}
	if (serverRecord != null) {
		serverFilter = serverRecord.getName();
	}
	if (serverFilter == null) {
		serverFilter = (String) request.getAttribute("serverFilter");
	}

%>
	<head>
		<title><%= serverFilter %> - dynaTrace MoM</title>
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
	</head>
	<body style="height: 100%"><%--
--%><jsp:include page="/jsp/header.jspf" />	
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

					<div id="tab_status" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" aria-expanded="true" aria-hidden="false">
						<div class="tab_content tab_status">
							<div class="status_details_blocks"><%
									ServerConfig serverConfig = serverRecord.getConfig();
									ConnectionConfig connectionConfig = serverConfig.getConnectionConfig();
									Protocol protocol = connectionConfig.getProtocol();
									String host = connectionConfig.getHost();
									int port = connectionConfig.getPort();
									Credentials credentials =  serverConfig.getCredentials();
									String user = credentials.getUser();
									String pass = credentials.getPass();
							%>
							<form name="config" method="post" action="${pageContext.request.contextPath}/servers/<%= serverRecord.getName() %>/config">
							<table border="1">
								<tr>
									<td>Protocol</td>
									<td><select name="protocol" id="protocol">
										<option<% if (protocol == Protocol.HTTP) { %> selected="selected"<% } %>><%= Protocol.HTTP %></option>
										<option<% if (protocol == Protocol.HTTPS) { %> selected="selected"<% } %>><%= Protocol.HTTPS %></option>
									</select></td>
								</tr>
								<tr>
									<td>Host</td>
									<td><input type="text" size="15" value="<%= host %>" name="host" id="host" /></td>
								</tr>
								<tr>
									<td>Port</td>
									<td><input type="text" size="5" value="<%= port %>" name="port" id="port" /></td>
								</tr>
								<tr>
									<td>User</td>
									<td><input type="text" size="15" value="<%= user %>" name="user" id="user" /></td>
								</tr>
								<tr>
									<td>Pass</td>
									<td><input type="text" size="15" value="<%= pass %>" name="pass" id="pass" /></td>
								</tr>
							</table>
							<input type="submit" value="update" id="submit" name="submit" />
							</form>
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