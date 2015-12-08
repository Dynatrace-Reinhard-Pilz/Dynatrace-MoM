<%@page
import="com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.mom.web.breadcrumbs.BreadCrumbs"
%><%
if (System.getProperty(System.class.getName()) != null) {
%>			<div class="navi_bar">
				<div class="navi_item selected"><a href="${pageContext.request.contextPath}/servers"><i class="icon-live"></i><span>Live</span></a></div><%--
				<div class="navi_item collectors">
					<a href="${pageContext.request.contextPath}/collectors"><i></i><span>Collectors</span></a>
				</div>
				<div class="navi_item profiles">
					<a href="${pageContext.request.contextPath}/profiles"><i></i><span>System Profiles</span></a>
				</div>
				<div class="navi_item dashboards">
					<a href="${pageContext.request.contextPath}/dashboards"><i></i><span>Dashboards</span></a>
				</div>
--%>
				<div class="navi_item fixpacks">
					<a href="${pageContext.request.contextPath}/fixpacks"><i></i><span>Fixpacks</span></a>
				</div>
<%--				
				<div class="navi_item settings">
					<a href="${pageContext.request.contextPath}/settings"><i></i><span>Settings</span></a>
				</div>
				<div class="navi_item tools">
					<a href="${pageContext.request.contextPath}/settings"><i></i><span>Utilities</span></a>
				</div>
--%>
			</div><%
}
			%>