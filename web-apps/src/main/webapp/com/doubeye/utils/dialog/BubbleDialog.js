/**
 * 弹出的消息框，过指定时间后自动消失
 * @ 
 * @param {Object} config
 * message : {String|Array<String>} 要显示的内容
 * last : {Number} 显示时间，单位毫秒，默认为2000毫秒
 * model : {Boolean} 是否为模式窗口，默认为true
 * 
 * @version 1.0.1
 * 	history
 *   1.0.1 : 
 *    + message能够支持Array<String>
 */
com.doubeye.BubbleDialog = function(config){
	this.time = config.last ? config.last : 2000;
	this.bg = config.model === false ? false : true;
	//this.content = config.message;
	if (config.message) {
		this.setMessage(config.message);
	}
};

com.doubeye.BubbleDialog.prototype = {
	init : function(){
		return this;
	},
	render : function() {
		jQuery.Prompt(this);
		return this;
	},
	setMessage : function(message) {
		if (com.doubeye.Ext.isArray(message)) {
			var messages = '';
			for (var i = 0; i < message.length; i ++) {
				messages += message[i] + '<br>';
			}
			this.content = messages;
		} else {
			this.content = message;
		}
		return this;
	}
};