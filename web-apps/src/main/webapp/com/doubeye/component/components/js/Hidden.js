/**
 * 隐藏组件
 *  
 */
com.doubeye.Hidden = com.doubeye.Ext.extend(com.doubeye.TextEdit, {
	noPositionInLayout : true,
	className : 'com.doubeye.Hidden',
	rootElementTypeName : '<input>',
	init : function() {
		com.doubeye.Hidden.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.Hidden.superclass.render.call(this);
		this.rootComponent.attr('type', 'hidden').val(this.value);
		return this;
	}
});
