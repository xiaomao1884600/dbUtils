/**
 * 单行编辑框组件
 * @author doubeye
 * @version 1.0.1
 * config
 * value : {String} 组件需要显示的值
 * 
 * @history
 * 1.0.1:
 * ! 基类由com.doubeye.Component改为com.doubeye.DataSensor
 */
com.doubeye.Password = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.Password',
	rootElementTypeName : '<input>',
	init : function() {
		com.doubeye.Password.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.Password.superclass.render.call(this);
		this.rootComponent.attr('type', 'password').val(this.value);
		return this;
	},
	getValue : function() {
		return this.rootComponent.val();
	},
	setValue : function(value) {
		this.rootComponent.val(value);
	},
	clearValue : function() {
		this.rootComponent.val('');
	}
});
