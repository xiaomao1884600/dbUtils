/**
 *  
 * jQuery Table对象条件筛选插件
 * 该类会访问table.condition属性
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * showConditionInTh {boolean} 是否在设置条件后在字段标题处显示该条件的值，默认为true，即显示
 * conditions {Object}
 */
com.doubeye.dTableConditionDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableConditionDecorator',
	/**
	 * 保存所有的条件 
	 */
	columnConditions : null,
	showConditionInTh : true,
	init : function(){
		this.columnConditions = [];
		this.table.init();
		return this;
	},
	render : function(){
		if (com.doubeye.Ext.isArray(this.config.conditions)) {
			this.getTable().__additionParams.condition = com.doubeye.Ext.encode(this.config.conditions)
		}
		this.table.render();
		this.__addConditionSupport();
		if (com.doubeye.Ext.isArray(this.config.conditions)) {
			this.setValue(this.config.conditions);
		}
		return this;
	},
	__addConditionSupport : function(){
		var columnDefines = this.getTable().columnDefinesObj.getDataColumnDefines();
		var columnDefine, el;
		for (var i = 0; i < columnDefines.length; i ++) {
			columnDefine = columnDefines[i];
			//去掉明确指明不支持排序的列
			if (columnDefine.disableCondition === true) {
				continue;
			}
			el = jQuery('<span>', {
			}).insertBefore(columnDefine.resizeEl);
			var condition = new com.doubeye.dTableColumnCondition({
				columnDefine : columnDefine,
				parent : el,
				conditionDecorator : this,
				showConditionInTh : this.showConditionInTh
			}).init().render();
			this.columnConditions.push(condition);
		}
	},
	doCondition : function() {
		this.__setAdditionParams();
		var table = this.getTable().clear().getData();
	},
	__setAdditionParams : function(config){
		var condition, config = [];
		for (var i = 0; i < this.columnConditions.length; i ++) {
			condition = this.columnConditions[i];
			if (condition.getConfig() && condition.getConfig().value) {
				config.push(condition.getConfig());
			}
		}
		if (config.length > 0) {
			this.getTable().__additionParams.condition = com.doubeye.Ext.encode(config);
		} else {
			if (this.getTable().__additionParams.condition) {
				this.getTable().__additionParams.condition = null;
			}
		}
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
	},
	/**
	 * @param value {Object<dataId{String}, value{Mix}>} 
	 */
	setValue : function(values) {
		for (var i = 0; i < values.length; i ++) {
			for (var j = 0; j < this.columnConditions.length; j ++) {
				var value = values[i];
				var columnCondition = this.columnConditions[j];
				if (value.dataId == columnCondition.columnDefine.dataId) {
					columnCondition.setValue(value.value);
					break;
				}
			}
		}
	},
/*
	/**
	 * 获得标记为删除的列
	 * @return {Array<com.doubeye.Record>} 所有标记为删除的列
	 /
	getRowsMarkDeleted : function() {
		return this.table.getRowsMarkDeleted();
	},
	/**
	 * 获得新增的列
	 * @return {Array<com.doubeye.Record>} 所有新增的列
	 /
	getRowsAdded : function() {
		return this.table.getRowsAdded();
	},
	/**
	 * 获得新增后被标记为删除的行 
	 * @return {Array<com.doubeye.Record>} 所有新增后背被删除的行
	/
	getRowsAddedAndDeleted : function() {
		return this.table.getRowsAddedAndDeleted();
	},	
	/**
	 * 获得修改过的列
	 * @return {Array<com.doubeye.Record>} 所有修改过的列
	 /
	getRowsModified : function() {
		return this.table.getRowsModified();
	},
	/**
	* 获得指定类型的列
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Record>} 所有指定的列
	/
	__getSpecifiedStatusRows : function(status) {
		return this.table.__getSpecifiedStatusRows(status);
	}
*/	
});