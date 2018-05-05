/**
 * 数据库连接管理器
 * config
 * afterSave : function(datasourceConfig, added) 保存成功后的事件
 */
com.doubeye.ConnectionManager = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ConnectionManager',
	datasourceType : null,
	configPanel : null,
	advenceConfigPanel : null,
	__buttonPanel : null,
	__dsNamePanel : null,
	__dsNameEl : null,
	_dsDecriptionEl : null,
	init : function() {
		com.doubeye.ConnectionManager.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ConnectionManager.superclass.render.call(this);
		this.__rednerDatasourceType();
		this.__renderDatasourceNamePanel();
		this.__renderConfigPanel();
		this.__renderButtonPanel();
		this.datasourceType.rootComponent.change();
		return this;
	},
	__rednerDatasourceType : function() {
		var thiz = this;
		var div = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<label>', {
			text : '数据源类型：'
		}).appendTo(div);
		this.datasourceType = new com.doubeye.Combo({
			parent : div,
			optionGetter : function(){
				thiz.__getAllSupportedConnectionTypes(null, this);
			},
		  	onChange : function(event, value, optObj) {
		  		thiz.__changeConnectionType(optObj.data('uiConfig'));
		  	}
		}).init().render();
	},
	__renderDatasourceNamePanel : function() {
		this.__dsNamePanel = new com.doubeye.Panel({
			parent : this.rootComponent,
			layout : 'com.doubeye.ColumnLayout',
			items : [{
				label : '数据源id',
				editorClassName : 'com.doubeye.Hidden',
				name : 'id'
			}, {
				label : '数据源名称',
				editorClassName : 'com.doubeye.TextEdit',
				name : 'name'
			}, {
				label : '数据源描述',
				editorClassName : 'com.doubeye.TextEdit',
				name : 'description'
			}]
		}).init().render();
		this.__dsNamePanel.hide();
	},
	__getAllSupportedConnectionTypes : function(config, combo){
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=getAllSupportedConnectionTypes',
			async: false,
		  	cache : false,
		  	showNoSuccessMessage : true,
		  	noWaitingDialog : true,
		  	processResult : function(result) {
		  		thiz.__processConeectionTypes(result, combo);
		  	}
		}).sendRequest();
	},
	__processConeectionTypes : function(datas, combo) {
		if (!com.doubeye.Ext.isArray(datas)) {
			return;
		}
		var option, optionEl;
		for (var i = 0; i < datas.length; i ++) {
			option = datas[i];
			optionEl = jQuery('<option>', {
				text : option.text,
				value : option.typeId
			}).appendTo(combo.selectEl);
			optionEl.data('uiConfig', option.propertyInfo);
		}
	},
	__changeConnectionType : function(newTypeConfigs) {
		if (!com.doubeye.Ext.isArray(newTypeConfigs)) {
			return;
		}
		this.configPanel.clear();
		this.advencedConfigPanel.clear();
		var normalConfigs = [], advancedConfigs = [];
		for (var i = 0; i < newTypeConfigs.length; i ++) {
			if (newTypeConfigs[i].advanced == 'true' || newTypeConfigs[i].advanced == true) {
				advancedConfigs.push(newTypeConfigs[i]);
			} else {
				normalConfigs.push(newTypeConfigs[i]);
			}
		}
		if (advancedConfigs.length == 0) {
			this.advencedConfigPanel.hide();
		} else {
			this.advencedConfigPanel.show();
		}
		this.configPanel.addBunchItemsByConfig(normalConfigs);
		this.advencedConfigPanel.addBunchItemsByConfig(advancedConfigs);
		this.__dsNamePanel.show();
	},
	__renderConfigPanel : function() {
		this.configPanel = new com.doubeye.Panel({
			parent : this.rootComponent,
			layout : 'com.doubeye.ColumnLayout',
			layoutConfig : {
				columnCount : 2
			}
		}).init().render();
		this.advencedConfigPanel = new com.doubeye.Panel({
			parent : this.rootComponent,
			layout : 'com.doubeye.ColumnLayout',
			title : '高级属性',
			collapsable : true
		}).init().render();
	},
	__renderButtonPanel : function() {
		var thiz = this;
		this.__buttonPanel = jQuery('<div>').appendTo(this.rootComponent);
		new com.doubeye.Button({
			parent : this.__buttonPanel,
			text : '测试数据源',
			action : function(){
				thiz.testDataSourceConfig();
			}
		}).init().render();
		new com.doubeye.Button({
			parent : this.__buttonPanel,
			text : '保存',
			action : function(){
				thiz.save();
			}
		}).init().render();
	},
	testDataSourceConfig : function(){
		var value = this.getValue();
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=testDatasouceConfig',
			params : value,
			successMessage : '测试成功'
		}).sendRequest();
	},
	getValue : function() {
		var value = this.__dsNamePanel.getValue();
		var config = this.configPanel.getValue();
		com.doubeye.Ext.apply(config, this.advencedConfigPanel.getValue());
		value.typeId = this.datasourceType.getValue();
		value.config = com.doubeye.Ext.encode(config);
		return value;
	},
	save : function() {
		var value = this.getValue(), thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=saveDatasouceConfig',
			params : value,
			successMessage : '保存成功',
			processResult : function(data){
				new com.doubeye.BubbleDialog({message : '保存成功'}).init().render();
				var added = isNaN(value.id) || value.id == '';
				if (added) {
					thiz.__dsNamePanel.setValue({id : data.dsId});
					value.id = data.dsId;
				}
				if (com.doubeye.Ext.isFunction(thiz.afterSave)) {
					thiz.afterSave(value, added);
				}
			}
		}).sendRequest();
	},
	/**
	 * 清除所有组件的值 
	 */
	clearValue : function() {
		this.__dsNamePanel.clearValue();
		this.configPanel.clearValue();
		this.advencedConfigPanel.clearValue();
	},
	setValue : function(value) {
		this.datasourceType.setValue(value.typeId);
		this.__dsNamePanel.setValue(value);
		this.configPanel.setValue(value.config);
		this.advencedConfigPanel.setValue(value.config);
	}
});