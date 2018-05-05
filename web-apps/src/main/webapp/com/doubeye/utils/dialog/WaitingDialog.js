/**
 * 等待框
 * message : {String} 等候信息
 */
com.doubeye.WaitingDialog = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.WaitingDialog',
	parent : '@@onlyDialog@@',
	init : function() {
		com.doubeye.WaitingDialog.superclass.init.call(this);
		this.rootElementTypeName = '<div>';
		return this;
	},
	render : function() {
		com.doubeye.WaitingDialog.superclass.render.call(this);
		this.__renderContent();
		this.openDialog({
			resizable : false,
			modal : true
		});
		this.__removeTitle();
		return this;
	},
	__renderContent : function(){
		jQuery('<span>', {
			'class' : this.classThemePrefix + 'imageSpan'
		}).appendTo(this.rootComponent);
	},
	__removeTitle : function(){
		this.rootComponent.css('margin', 0).css('padding', 0);
		this.rootComponent.parents('.ui-dialog').css('margin', 0).css('padding', 0).css('height',80).css('width',85).find('.ui-dialog-titlebar').remove();
	}
});