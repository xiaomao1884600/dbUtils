/**
 * 工具条
 * @author doubeye
 * @version 1.0.0 
 */
com.doubeye.ToolBar = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ToolBar',
	__itemPanel : null,
	__layout : null,
	init : function() {
		com.doubeye.ToolBar.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ToolBar.superclass.render.call(this);
		this.rootComponent.addClass(this.classThemePrefix + 'root ui-widget-header ui-corner-all');
		this.__createLayout();
		return this;
	},
	__renderItemPanel : function(){
		this.__itemPanel = jQuery('<div>').appendTo(this.rootComponent);
	},
	__createLayout : function(){
		var config = {
			parent : this.rootComponent,
			needLabel : false
		};
		config = com.doubeye.Ext.apply(config, this.layoutConfig);
		var layout = 'com.doubeye.FlowLayout';
		this.__layout = com.doubeye.Utils.getClassInstance(layout, config).init().render();
	},
	addBunchItemsByConfig : function(itemConfigs) {
		this.__layout.addBunchItemsByConfig(itemConfigs);
	},
	addItemByConfig : function(itemConfig) {
		this.__layout.addItemByConfig(itemConfig);
	},
	addItem : function(component, label) {
		this.__layout.addItem(component, label);
	},
	clear : function() {
		this.__layout.clear();
	},
	getValue : function() {
		return null;
	},
	clearValue : function() {
	}, 
	setValue : function(value) {
	}
})
