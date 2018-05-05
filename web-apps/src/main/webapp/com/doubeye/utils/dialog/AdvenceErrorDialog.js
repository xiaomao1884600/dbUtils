/**
 * 高级异常显示框
 * title : {String} 标题
 * message : {String} 错误信息
 * stack : {String} 异常的栈信息 
 */
com.doubeye.AdvenceErrorDialog = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.AdvenceErrorDialog',
	parent : '@@onlyDialog@@',
	title : '错误',
	init : function() {
		com.doubeye.AdvenceErrorDialog.superclass.init.call(this);
		this.rootElementTypeName = '<div>';
		return this;
	},
	render : function() {
		com.doubeye.AdvenceErrorDialog.superclass.render.call(this);
		this.__renderMessage().__renderStack().__renderBaidu().__renderSendEmail();
		this.openDialog({
			title : this.title,
			width : 500,
			height : 400,
			resizable : false,
			modal: true
		});
		return this;
	},
	__renderMessage : function() {
		var div = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<label>', {
			text : '错误信息:'
		}).appendTo(div);
		jQuery('<input>', {
			value : this.message,
			width : '100%'
		}).appendTo(div);
		return this;
	},
	__renderStack : function() {
		var div = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<label>', {
			text : '栈信息:'
		}).appendTo(div);
		div = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<textarea>', {
			value : this.stack,
			style : 'overflow:scroll;width:100%;height:200px;'
		}).appendTo(div);
		return this;
	},
	__renderBaidu : function() {
		var div = jQuery('<div>').appendTo(this.rootComponent);
		new com.doubeye.BaiduSearch({
			noLogo : true,
			value : this.message,
			inputSize : 45,
			parent : div
		}).init().render();
		return this;
	},
	__renderSendEmail : function() {
		var div = jQuery('<div>').appendTo(this.rootComponent);
		new com.doubeye.EmailSender({
			mailto : 'doubeye@sina.com',
			subject : this.message,
			body : this.stack,
			text : '发送错误报告',
			parent : div
		}).init().render();
		return this;
	},
	setMessage : function(message) {
		this.message = message;
		return this;
	}
});
