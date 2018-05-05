/**
 * 普通的对话框，同时为对话框类族提供接口
 * @ 
 * @param {Object} config
 * message : 要显示的内容
 */
com.doubeye.NormalDialog = function(config){
	com.doubeye.Ext.apply(this, config);
};

com.doubeye.NormalDialog.prototype = {
	init : function(){
		return this;
	},
	render : function() {
		alert(this.message);
		return this;
	},
	setMessage : function(message) {
		this.message = message;
		return this;
	}
};
