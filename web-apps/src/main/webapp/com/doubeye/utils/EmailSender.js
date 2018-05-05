/**
 * 邮件发送连接
 * @author doubeye
 * @version 1.0.0
 * mailto {String} 收件人
 * subject {String} 主题
 * body {String} 内容
 * text {String} 链接文字 ,默认为发送邮件
 */
com.doubeye.EmailSender = com.doubeye.Ext.extend(com.doubeye.Component, {
	text : '发送邮件',
	className : 'com.doubeye.EmailSender',
	init : function(){
		com.doubeye.EmailSender.superclass.init.call(this);
		return this;
	},  	
	render : function() {
		com.doubeye.EmailSender.superclass.render.call(this);
		jQuery('<a>', {
			href : this.__getUrl(),
			html : this.text
		}).appendTo(this.rootComponent);
		return this;
	},
	__getUrl : function(){
		return 'mailto:' + this.mailto + '?subject=' + this.subject + '&body=' + this.body;
	}
});
