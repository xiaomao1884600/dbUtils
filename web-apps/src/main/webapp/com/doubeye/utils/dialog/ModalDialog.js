/**
 * 高级异常显示框
 * title : {String} 标题
 */
com.doubeye.ModalDialog = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ModalDialog',
	parent : '@@onlyDialog@@',
	componentPanel : null,
	init : function() {
		com.doubeye.ModalDialog.superclass.init.call(this);
		this.rootElementTypeName = '<div>';
		return this;
	},
	render : function() {
		com.doubeye.ModalDialog.superclass.render.call(this);
		var thiz = this;
		var dialogConfig = {
			title : this.title,
			width : this.width ? this.width : 500,
			height : 400,
			resizable : false,
			modal: true,
			open : function() {
				jQuery('.ui-widget-overlay').click(function() {
					thiz.closeDialog();
				});
			}
		};
		com.doubeye.Ext.apply(dialogConfig, this.config);
		this.openDialog(dialogConfig);
		return this;
	}
});
