com.doubeye.AjaxManager = function(config) {
	com.doubeye.Ext.apply(this, config);
	this.maxConcurrency = config.maxConcurrency ? config.maxConcurrency : 5;
	this.unSubmitted = [];
	this.requesting = [];
	this.finished = [];
};

com.doubeye.AjaxManager.prototype = {
	addAjax : function(ajax) {
		this.unSubmitted[this.unSubmitted.length] = ajax;
	},
	finish : function(ajax) {
		for (var i = 0; i < this.requesting.length; i ++) {
			if (this.requesting[i] == ajax) {
				this.requesting.splice(i, 1);
				console.log("finde");
				break;
			}
		}
		this.sendNextRequest();		
	},
	sendNextRequest : function() {
		if (this.unSubmitted.length > 0) {
			var ajax = this.unSubmitted.shift();
			this.requesting.push(ajax);
			ajax.sendRequest();
		}
	},
	start : function() {
		for (var i = 1; i <= this.maxConcurrency && i < this.unSubmitted.length; i ++) {
			this.sendNextRequest();
		}
	}
};