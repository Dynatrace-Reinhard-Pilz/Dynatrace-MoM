
function render() {
	var renderElements = findRenderElements(document.body);
	for (var renderElementIdx = 0; renderElementIdx < renderElements.length; renderElementIdx++) {
		var renderElem = renderElements[renderElementIdx];
		var backupDiv = document.createElement("div");
		var dataLayoutElem = renderElem;
		var dataSourceURL = renderElem.getAttribute("data-source");
		xhr = XHR_GET(dataSourceURL);
		var dataDoc = xhr.responseXML;
		var dataElementName = renderElem.getAttribute("data-element");
		var dataElements = dataDoc.getElementsByTagName(dataElementName);
		for (var dataElementIdx = 0; dataElementIdx < dataElements.length; dataElementIdx++) {
			var clonedDataLayoutElem = dataLayoutElem.cloneNode(true);
			clonedDataLayoutElem.removeAttribute("data-source");
			clonedDataLayoutElem.removeAttribute("data-element");
			var dataElement = dataElements[dataElementIdx];
			evalData(clonedDataLayoutElem, dataElement);
			insertAfter(clonedDataLayoutElem, renderElem);
		}
		dataLayoutElem.parentNode.removeChild(dataLayoutElem);
	}
}

function insertAfter(newElement,targetElement) {
	var parent = targetElement.parentNode;
	
	if (parent.lastchild == targetElement) {
		parent.appendChild(newElement);
	} else {
		parent.insertBefore(newElement, targetElement.nextSibling);
	}
}

function evalData(el, thisObj) {
	if (!el) return;
	var nodeValue = el.nodeValue;
	if (nodeValue) {
		nodeValue = nodeValue.trim();
		if (nodeValue.length > 0) {
			el.nodeValue = evalDataValues(el.nodeValue, thisObj);
		}
	}
	var attributes = el.attributes;
	if (attributes) {
		for (var i = 0; i < attributes.length; i++) {
			evalData(attributes[i], thisObj);
		}
	}
	var children = el.childNodes;
	if (attributes) {
		for (var i = 0; i < children.length; i++) {
			evalData(children[i], thisObj);
		}
	}
}

function evalDataValues(str, thisObj) {
	var re = /{([^}]+)}/g;
	var text;
	var origStr = str;
	while (text = re.exec(origStr)) {
		var path = text[1].split(".");
		var resolved = resolve(path, thisObj);
		if (resolved) {
			if (resolved.nodeType == 1) {
				resolved = resolved.innerHTML;
			}
			resolved = resolved.replace("/services/", "/web/");
			str = str.replace("{" + text[1] + "}", resolved);
		}
	}
	return str;
}

function MatchProperty(expr) {
	this.getValue = function(v) {
		if (!this.match) {
			return v;
		} else if (this.match.trim() == v.trim()) {
			if (!this.valueOnMatch) {
				return true;
			} else {
				return this.valueOnMatch;
			}
		} else {
			if (!this.valueOnNonMatch) {
				return false;
			} else {
				return this.valueOnNonMatch;
			}
		}
	};
	
	var eqIdx = expr.trim().indexOf("==");
	if (eqIdx == -1) {
		this.property = expr;
//		alert("this.property: " + this.property);
		this.match = undefined;
		this.valueOnMatch = undefined;
		this.valueOnNonMatch = undefined;
		return;
	}
	var propName = expr.substring(0, eqIdx).trim();
	var afterNamePart = expr.substring(eqIdx + 2);
	this.property = propName;
//	alert("this.property: " + this.property);
	var qmIdx = afterNamePart.indexOf("?");
	if (qmIdx > -1) {
		this.match = afterNamePart.substring(0, qmIdx);
		var matchValuePart = afterNamePart.substring(qmIdx + 1).trim();
		var colonIdx = matchValuePart.indexOf(":");
		if (colonIdx > -1) {
			this.valueOnMatch = matchValuePart.substring(0, colonIdx).trim();
			this.valueOnNonMatch = matchValuePart.substring(colonIdx + 1).trim();
		} else {
			this.valueOnMatch = matchValuePart;
			this.valueOnNonMatch = "";
		}
	}
	
}

function resolve(path, thisObj) {
	if (path.length == 0) {
		return null;
	}
	var property = new MatchProperty(path[0]);
	path.shift();
//	alert("Looking for property " + property + " in element " + thisObj.tagName);
	var foundMatch = null;
	
	var matchingChildElement = getElementsByTagName(thisObj, property.property);
	if (matchingChildElement) {
//		alert("foundMatch(" + property.property + ")[element]");
		foundMatch = matchingChildElement;
	} else if (thisObj.hasAttribute(property.property)) {
//		alert("foundMatch(" + property.property + ")[attribute]");
		foundMatch = thisObj.getAttribute(property.property);
	}
	if (!foundMatch) {
		return null;
	}
	if (path.length == 0) {
		return property.getValue(foundMatch);
	}
	return resolve(path, foundMatch);
}

function getElementsByTagName(el, name) {
//	alert("looking for " + name + " in " + el.outerHTML);
	var els = el.getElementsByTagName(name);
	for (var i = 0; i < els.length; i++) {
		if (els[i].parentNode == el) {
			return els[i];
		}
	}
	return undefined;
}

function removeChildren(el) {
	while (el.firstChild) el.removeChild(el.firstChild);
}

function findRenderElements(el) {
	if (!el) return [];
	var result = [];
	if (el.hasAttribute("data-source")) {
		add(result, el);
	}
	var children = el.children;
	for (var childIdx = 0; childIdx < children.length; childIdx++) {
		var childResults = findRenderElements(children[childIdx]);
		for (var childResultIdx = 0; childResultIdx < childResults.length; childResults++) {
			add(result, childResults[childResultIdx]);
		}
	}
	return result;
}

function add(arr, el) {
	arr[arr.length] = el;
}