/**
 * 高级异常显示框
 * title : {String} 标题
 */
com.doubeye.ModalPanelDialog = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ModalPanelDialog',
	parent : '@@onlyDialog@@',
	componentPanel : null,
	init : function() {
		com.doubeye.ModalPanelDialog.superclass.init.call(this);
		this.rootElementTypeName = '<div>';
		return this;
	},
	render : function() {
		com.doubeye.ModalPanelDialog.superclass.render.call(this);
		this.__renderComponentPanel();
		var thiz = this;
		var dialogConfig = {
			title : this.title,
			width : 500,
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
	},
	__renderComponentPanel : function() {
		this.componentPanel = new com.doubeye.Panel({
			parent : this.rootComponent,
			layout : 'com.doubeye.ColumnLayout',
			layoutConfig : {
				columnCount : 2
			},
			collapsable : false
		}).init().render();
		return this;
	},
	addBunchItemsByConfig : function(itemConfigs) {
		this.componentPanel.addBunchItemsByConfig(itemConfigs);
	},
	addItemByConfig : function(itemConfig) {
		this.componentPanel.addItemByConfig(itemConfig);
	},
	addItem : function(component, label) {
		this.componentPanel.addItem(component, label);
	},
	clear : function() {
		this.componentPanel.clear();
	},
	getValue : function() {
		return this.componentPanel.getValue();
	},
	getModifiedValue : function() {
		return this.componentPanel.getModifiedValue();
	},
	clearValue : function() {
		this.componentPanel.clearValue();
	}, 
	setValue : function(value) {
		this.componentPanel.setValue(value);
	},
	autoAdjustHeight : function() {
		this.rootComponent.height(this.componentPanel.rootComponent.height());
	},
	show : function() {
		this.openDialog()
	},
	getComponent : function(id) {
		return this.componentPanel.getComponent(id);
	},
	/**
	 * 检查所有上的组件是否满足验证js端验证条件，如果所有组件都满足，则返回true，否则返回错误数组
	 * @return  如果所有组件都满足，则返回true，否则返回错误数组
	 * 数组结构如下{label<String>, message<String>} 第一个参数为组件的lable，第二个参数为错误信息
	 */
	checkValidity : function() {
		return this.componentPanel.checkValidity();
	}
});
