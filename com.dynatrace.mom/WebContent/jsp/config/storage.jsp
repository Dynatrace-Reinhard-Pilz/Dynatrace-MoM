<%@page
session="false"
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.mom.runtime.ServerRepository,
com.dynatrace.http.ConnectionStatus,
com.dynatrace.reporting.Availability,
java.io.File,
com.dynatrace.mom.MomConfig"
%><%
MomConfig momConfig = null;
momConfig = (MomConfig) pageContext.findAttribute(MomConfig.ATTRIBUTE);
	File storage = momConfig.getStorage();
%><!DOCTYPE html>
<html lang="en">
	<head>
		<title>Storage - dynaTrace MoM</title><%
		if (storage != null) { %>
			<meta http-equiv="refresh" content="0; url=${pageContext.request.contextPath}" /><%
		} %>
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
%>
	<body>
<jsp:include page="/jsp/header.jspf" />	
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
					<div class="instance_content">
					<div id="tab_license" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div class="tab_content tab_license"><%
							if (storage == null) { %>
							<div>You need to specify a folder to store settings and temporary data</div>
							<div>The Application Server hosting MoM requires r/w access to this folder</div>
							<form name="set-storage" id="set-storage" method="post">
								<input name="storage" id="storage" type="text" value="" placeholder="&lt;absolute folder&gt;" /><input type="submit" value="submit" />
							</form><%
							} else { %>
							<form name="set-storage" id="set-storage" method="post">
								<input name="storage" id="storage" type="text" value="<%= storage.getAbsolutePath() %>" placeholder="&lt;absolute folder&gt;" /><input type="submit" value="submit" />
							</form><%
							}
							%>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>