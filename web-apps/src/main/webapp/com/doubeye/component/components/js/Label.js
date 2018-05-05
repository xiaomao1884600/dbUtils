/**
 * Label组件
 * @author doubeye
 * @version 1.0.0
 * config
 * value : {Mix} 组件需要显示的值
 */
com.doubeye.Label = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.Label',
	rootElementTypeName : '<LABEL>',
	init : function() {
		com.doubeye.Label.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.Label.superclass.render.call(this);
		this.rootComponent.html(this.value);
		return this;
	},
	setValue : function(value) {
		this.rootComponent.html(value);
	}
});
