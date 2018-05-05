/**
 * jQuery Table的组件内文本搜索器
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 */
com.doubeye.dTableTableSearchDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableTableSearchDecorator',
	init : function(){
		this.table.init();		
		return this;
	},
	render : function(){
		this.table.render();
		this.__renderSearchComponent();
		return this;
	},
	__renderSearchComponent : function(){
		var trEl = jQuery('<tr>').prependTo(this.getTable().theadEl);
		var td = jQuery('<td>', {
			colSpan : this.getTable().columnDefinesObj.getDataColumnDefines().length
		}).appendTo(trEl);
		var config = {
			parent : td,
			component : this.getTable(),
			selector : '.cellValue'
		};
		new com.doubeye.ComponentSearch(config).init().render();
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
	}	
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