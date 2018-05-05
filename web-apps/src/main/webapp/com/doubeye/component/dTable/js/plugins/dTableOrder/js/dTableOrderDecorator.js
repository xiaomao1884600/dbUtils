/**
 *  
 * jQuery Table对象的排序修饰器
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * multiColumn {boolean} 是否支持多列排序，默认为false
 * orderType {'client'|'server'} 排序类型，client代表在客户端进行排序，server代表发送排序信息并重新获得数据，指定server排序，需服务端支持排序
 */
com.doubeye.dTableOrderDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableOrderDecorator',
	/**
	 * 多列排序设置器 
	 */
	columnOrderManager : null,
	multiColumn : false,
	orderType : 'client',
	init : function(){
		this.table.init();		
		return this;
	},
	render : function(){
		if (this.orderConfigs) {
			if (this.orderType == 'client') {
				com.doubeye.ArraySortor.doSort(this.getTable().data, this.orderConfigs, true);
			} else {
				this.getTable().__additionParams.order = com.doubeye.Ext.encode(this.orderConfigs);
			}
		}
		this.table.render();
		this.__createColumnOrderSetup();
		this.__addOrderSupport();
		if (this.orderConfigs) {
			this.columnOrderManager.setValue(this.orderConfigs);
		}
		return this;
	},
	__createColumnOrderSetup : function() {
		if (this.multiColumn === true) {
			this.columnOrderManager = new com.doubeye.dTableMultiColumnOrderManager({
				parent : '@@onlyDialog@@',
				orderDecorator : this
			}).init().render();
		} else {
			this.columnOrderManager = new com.doubeye.dTableSingleColumnOrderManager().init().render();
		}
	},
	__addOrderSupport : function(){
		var columnDefines = this.getTable().columnDefinesObj.getDataColumnDefines();
		var columnDefine, el;
		for (var i = 0; i < columnDefines.length; i ++) {
			columnDefine = columnDefines[i];
			//去掉明确指明不支持排序的列
			if (columnDefine.disableOrder === true) {
				continue;
			}
			el = jQuery('<span>', {
			}).insertBefore(columnDefine.resizeEl);
			var order = new com.doubeye.dTableOrder({
				columnDefine : columnDefine,
				parent : el,
				columnOrderManager : this.columnOrderManager,
				orderDecorator : this
			}).init().render();
			this.columnOrderManager.addColumn(order);
		}
	},
	doOrder : function() {
		var table = this.getTable();
		var configs = this.columnOrderManager.getValue();
		if (this.orderType == 'client') {
			table.recordList.doSort(configs, true);
			table.clear(true).renderData();
		} else if (this.orderType == 'server') {
			this.__setAdditionParams();
			this.getTable().clear().getData();
		}
	},
	__setAdditionParams : function(){
		this.getTable().__additionParams.order = com.doubeye.Ext.encode(this.columnOrderManager.getValue());
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
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