/**
 * 列类型选择器
 * config
 * columnTypeManager {com.doubeye.ColumnTypeManager} 数据库类型管理器
 * onChange : {function(value)} : 当列类型改变时触发的事件
 * 
 * @author doubeye
 * @version 1.0.1
 *   ! 将基类改为com.doubeye.DataSensor
 */
com.doubeye.ColumnTypeCombo = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.ColumnTypeCombo',
	/**
	 * 列类型管理器 
	 */
	columnTypeManager : null,
	rootElementStyle : 'width:100%;',
	/**
	 * 被选中的列定义，用例维护该下拉列表被选中的列定义信息
	 */
	__typeDefine : null,
	selectEl : null,
	init : function() {
		com.doubeye.ColumnTypeCombo.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ColumnTypeCombo.superclass.render.call(this);
		this.__renderSelect();
		
		if (com.doubeye.Ext.isFunction(this.onValueChange)) {
			this.bindValueChangeEvent(this.selectEl);
		}
		
		return this;
	},
	__renderSelect : function() {
		var types = this.columnTypeManager.supportedTypes, type, thiz = this;
		this.selectEl = jQuery('<select>', {
			style : 'width:100%;'
		}).appendTo(this.rootComponent).change(function(){
			if (com.doubeye.Ext.isFunction(thiz.onChange)) {
				thiz.__typeDefine = com.doubeye.Utils.objectRefactor.clone(jQuery(thiz.selectEl.children(':selected')).data('type'));
				thiz.onChange(thiz.__typeDefine);
			}
		});
		for (var i = 0; i < types.length; i ++) {
			type = types[i];
			jQuery('<option>', {
				value : type,
				text : type.typeName
			}).appendTo(this.selectEl).data('type', type);
		}
		this.__typeDefine = com.doubeye.Utils.objectRefactor.clone(jQuery(this.selectEl.children(':selected')).data('type'));
	},
	getValue : function() {
		return this.__typeDefine.getValue();
	},
	setValue : function(value){
		this.__typeDefine.setValue(value);
		if (value) {
			var options = this.selectEl.children(), option;
			for (var i = 0; i < options.length; i ++) {
				option = options[i];
				if (option.text == value.typeName) {
					jQuery(option).attr("selected",true);
					return;
				}
			}
		}
	}
});
