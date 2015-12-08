var Login = function() {
	
	this.checkBrowser = function() {
		var browserDetect = new BrowserDetect();
		
		if (browserDetect.supportedBrowser()) {
			$('#browserNotSupportedMessage').hide();
		} else {
			$('#browserNotSupportedMessage').show();
		}
	};
};


var BrowserDetect = function() {
		
	this.dataBrowser = [
	  	{
	  		name: "Chrome"
	  	},
	  	{
	  		name: "Firefox"
	  	},
	  	{
	  		name: "Apple"
	  	},
	  	{
	  		name: "MSIE",
	  		versionSearch: "MSIE",
	  		versionSupported: 9
	  	},
	  	{
	  		name: "Trident",
	  		versionSearch: "rv",
	  		versionSupported: 11
	  	}
	];
		
	this.supportedBrowser = function() {
		var dataString = navigator.userAgent;
		for (var i = 0; i < this.dataBrowser.length; i++)	{
			if (dataString && dataString.indexOf(this.dataBrowser[i].name) != -1) {
				var versionSupported = this.dataBrowser[i].versionSupported;
				if (versionSupported) {
					var versionSearchString = this.dataBrowser[i].versionSearch;
					var version = this.searchVersion(dataString, versionSearchString) 
							|| this.searchVersion(navigator.appVersion, versionSearchString);
					if (version >= versionSupported) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	};
		
	this.searchVersion = function(dataString, versionSearchString) {
		var index = dataString.indexOf(versionSearchString);
		if (index == -1) return;
		
		return parseFloat(dataString.substring(index + versionSearchString.length + 1));
	};
};

var login = new Login();

$(function() {
	login.checkBrowser();
});