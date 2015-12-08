function restartServer() {
	log(pageContext.request.contextPath + '/rest/servers/' + pageContext.request.server.name + '/restart');
	$.ajax({
		type: 'POST',
		url: pageContext.request.contextPath + '/rest/servers/' + pageContext.request.server.name + '/restart',
		success: function(data) {
			refreshPage()
		},
		statusCode: {
			404: function() {
				alert('page not found');
			},
			400: function() {
				alert('bad request');
			}
		}
	});	
}