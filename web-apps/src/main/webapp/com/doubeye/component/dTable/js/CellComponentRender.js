/**
 * 根据配置绘制dTable的Cell
 * @author doubeye
 * @version 1.0.0
 * config
 *  componentConfig {Object} Cell中组件的说明，具体解释如下、
 *   editorClassName {String<com.doubeye.DataSensor>} 组件的类名，组件必须继承自com.doubeye.DataSensor
 *   componentConfig {Object} 传递给组件构造函数的参数
 *  value {Mixed} 组件的值 
 */
com.doubeye.CellComponentRender = com.doubeye.Ext.extend(com.doubeye.Component, {
	init : function() {
		com.doubeye.CellComponentRender.superclass.init.call(this);
		return this;		
	},
	render : function() {
		var editorClassName = this.config.editorClassName
		if (!editorClassName) {
			editorClassName = 'com.doubeye.TextEdit';
		}
		var config = com.doubeye.Ext.apply(this.config.componentConfig, {
			parent : this.parent
		});
		var cmp = com.doubeye.Utils.getClassInstance(editorClassName, config);
		cmp.init().render();
		cmp.setValue(this.config.value);
		return cmp;
	}
});
