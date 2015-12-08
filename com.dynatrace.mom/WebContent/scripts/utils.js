var pageContext = {
	request: {}
};

$(document).ready(function() {
	pageContext.request.contextPath = attr('data-context-path');
	if (!pageContext.request.contextPath) {
		return;
	}
	pageContext.request.requestURI = attr('data-request-uri');
	if (!pageContext.request.requestURI) {
		return;
	}
	pageContext.request.server = {
			name: attr('data-server')
		};
});

$(document).ready(function() {
	var reloadElements = $(document).find("[id|='reload']");
	if (!reloadElements) {
		return;
	}
	if (reloadElements == null) {
		return;
	}
	if (!(reloadElements.length)) {
		return;
	}
	if (reloadElements.length == 0) {
		return;
	}
	window.setTimeout(refreshPage, 5000);
});

function refreshPage() {
	document.getElementById("autorefresh_message").style.display = "block";
	document.getElementById("spinner").style.display = "block";
	$.ajax({
		type: 'GET',
		dataType: 'html',
		url: pageContext.request.requestURI,
		success: function(result,status,xhr) {
			var location = xhr.getResponseHeader('Location');
			if (location) {
				window.location.href = location;
				return;
			}
			$(result).find("[id|='reload']").each(function() {
				document.getElementById(this.id).innerHTML = this.innerHTML;
			});
		},
		complete: function(xhr) {
			var location = xhr.getResponseHeader('Location');
			if (location) {
				return;
			}
			document.getElementById("autorefresh_message").style.display = "none";
			document.getElementById("spinner").style.display = "none";
			window.setTimeout(refreshPage, 5000);
		},
		error: function(xhr, textStatus, errorThrown) {
//			var s = "" + errorThrown;
//			log(s.replace(/</g, '&lt;').replace(/>/g, '&gt;'));
		}
	});	
}


function attr(name) {
	return document.body.getAttribute(name);
}

function guid() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	}
	return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}

function isNotEmpty(array) {
	return (array !== null) && (array.length > 0);
}

function isEmpty(array) {
	return (array === null) && (array.length == 0);
}

var DEBUG = false;
var logcnt = 0;
function log(msg) {
	if (!DEBUG) return;
	var dtlog = document.getElementById("dtlog");
	if (!dtlog) {
		dtlog = document.createElement("div");
		dtlog.id = "dtlog";
		dtlog.style.position = "absolute";
		dtlog.style.left = "0";
		dtlog.style.bottom = "0";
		dtlog.style.width = "90%";
		dtlog.style.height = "20ex";
		// dtlog.style.zIndex = "9999";
		document.body.insertBefore(dtlog, document.body.firstChild);
	}
	logcnt++;
	dtlog.style.display = "block";
	var msgdiv = document.createElement("div");
	msgdiv.innerHTML = logcnt + ": " + msg;
	dtlog.appendChild(msgdiv);
	msgdiv.scrollIntoView();
}
