var DELETE	= "DELETE";
var GET		= "GET";
var POST	= "POST";
var PUT		= "PUT";

function XHR(method, url, data, headers) {
	var xhr = new XMLHttpRequest();
	xhr.open(method, url, false);
	if (headers != null) {
		for (var i = 0; i < headers.length; i++) {
			xhr.setRequestHeader(headers[i].key, headers[i].value);		
		}
	}
	xhr.send(data);
	if ((xhr.status != 200) && (xhr.status != 204) && (xhr.status != 201)) {
		throw "Unexpected XHR status " + xhr.status;
	}
	return xhr;
}

function XHR_GET(url) {
	return XHR(GET, url, null, null);
}

function GETLines(url) {
	var responseText = XHR(GET, url).responseText;
	var lines = responseText.split(/\n/);
	
	var results = [];
	var resultIdx = 0;
	
	var lineIdx = 0;
	for (lineIdx = 0; lineIdx < lines.length; lineIdx++) {
		var line = lines[lineIdx].trim();
		if (line.length == 0) {
			continue;
		}
		results[resultIdx] = line;
		resultIdx++;
	}
	return results;
}

