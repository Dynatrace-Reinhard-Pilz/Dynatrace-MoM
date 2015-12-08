<%@page import="com.dynatrace.mom.web.ServerModel,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection"
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
		<script src="${pageContext.request.contextPath}/scripts/highcharts.js"></script>
		<script src="${pageContext.request.contextPath}/scripts/charting.js"></script>
<!--		
		<script src="${pageContext.request.contextPath}/js/modules/exporting.js"></script>
-->
	</head>
	<body data-request-uri="<%= request.getAttribute("javax.servlet.forward.request_uri") %>" data-context-path="${pageContext.request.contextPath}" data-server="<%= serverFilter %>" style="height: 100%"><%--
--%><jsp:include page="/jsp/header.jspf" />	
		<div class="main_content">
<jsp:include page="/jsp/navibar.jsp" />	
			<div class="work_area">
<jsp:include page="/jsp/breadcrumbs.jsp" />	
				<div class="stack_details_container">
					<div id="stack-details">
						<div class="instance_content">
						<div id="tab_status" aria-labelledby="ui-id-19" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" aria-expanded="true" aria-hidden="false">
							<table id="reload-server-list" class="overview-list servers">
								<tbody><%
								pageContext.setAttribute(ServerRecord.class.getName(), serverRecord);
								%><%@ include file="./server-row.jspf" %>
								</tbody>
							</table>
							<div id="highcharts"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>