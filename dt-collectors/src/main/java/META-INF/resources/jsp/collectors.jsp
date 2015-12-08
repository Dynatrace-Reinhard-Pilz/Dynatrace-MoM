<%@page session="false"
import="java.util.Collection,
com.dynatrace.collectors.web.CollectorsModel,
com.dynatrace.collectors.CollectorRecord,
com.dynatrace.utils.Iterables,
com.dynatrace.utils.Version,
com.dynatrace.collectors.RestartStatus"
%><%
	CollectorsModel model = (CollectorsModel) request.getAttribute(CollectorsModel.class.getName());
	String serverName = model.getServerName();
	Iterable<CollectorRecord> collectors = model.getCollectors(); %>
	<table id="reload-collector-list" class="overview-list collectors">
		<tbody><%
		if (!Iterables.isNullOrEmpty(collectors)) { 
	for (CollectorRecord collector : collectors) {
		RestartStatus restartStatus = collector.getRestartStatus();
		String sVersion = "";
		if (Version.isValid(collector.getVersion())) {
			sVersion = collector.getVersion().toString();
		} %>
		<tr>
			<td class="icon-column" style="width: 1px; white-space: nowrap; font-family: Courier New"><a href="${pageContext.request.contextPath}/servers/<%= collector.getServerName() %>/collectors/<%= collector.getName() %>@<%= collector.getHost() %>"><%= collector.getName() %>@<%= collector.getHost() %></a></td>
			<td><%= sVersion %></td><%
			if (model.isServerColumnRequired()) { %>
			<td><a href="${pageContext.request.contextPath}/servers/<%= collector.getServerName() %>"><%= collector.getServerName() %></a></td><%
			} %>			
			<td style="text-align: right"><%
			if (collector.isConnected()) {
			%><form style="display: inline" method="post" action="${pageContext.request.contextPath}/servers/<%= serverName %>/collectors/<%= collector.getName() %>@<%= collector.getHost() %>/restart"><%
			if (restartStatus == RestartStatus.REQUIRED) {
				%><button class="btn btn-blue btn-compact failover_button">Restart Required</button><%
			} else if (restartStatus == RestartStatus.NONE) {						
				%><button class="btn btn-blue btn-compact failover_button">Restart</button><%
			} else {
				%><button class="btn btn-blue btn-compact failover_button busy" disabled="disabled">Restarting</button><%
			}
			%></form><%
			} else {
			%>offline<%
			}%></td>
		</tr><%
	} 
	} %>
		</tbody>
	</table>