<%@page import="com.dynatrace.mom.web.Model,
com.dynatrace.mom.runtime.components.ServerRecord,
com.dynatrace.reporting.IncidentOverview,
java.util.Collection,
com.dynatrace.mom.web.tabs.Widgets,
com.dynatrace.mom.web.tabs.Widget"
%>					<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all"><%
	Widgets widgets = null;
	Model model = (Model) request.getAttribute(Model.class.getName());
	if (model != null) {
		widgets = model.getTabs();
	}
	if (widgets == null) {
		widgets = (Widgets) request.getAttribute(Widgets.class.getName());
	}
	if (widgets != null) {
		for (Widget widget : widgets) { %>
			<li class="ui-state-default ui-corner-top<%= widget.isSelected() ? " ui-tabs-active ui-state-active" : "" %>">
				<a href="${pageContext.request.contextPath}/<%= widget.getLink() %>" class="ui-tabs-anchor<%= widget.getCssClass() != null ? " " + widget.getCssClass() : "" %>"><strong><%= widget.getLabel() %></strong></a>
			</li><%
		}
	} %>
					</ul>