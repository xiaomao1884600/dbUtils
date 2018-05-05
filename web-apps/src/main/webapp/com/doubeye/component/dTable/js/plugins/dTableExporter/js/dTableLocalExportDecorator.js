/**
 * jQuery Table数据导出器
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * valueSelector {jQuerySelector} 默认为空，即将整个cell输入，如果包含该值，则仅输入cell下符合条件的element中的内容，默认为'.cellValue'，与dTable的默认渲染匹配
 * renderStyle {boolean} 是否渲染表格的字体、边框样式等样式，默认为false，用来提高导出速度
 */
com.doubeye.dTableLocalExportDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableLocalExportDecorator',
	valueSelector : '.cellValue',
	renderStyle : false,
	init : function(){
		this.table.init();		
		return this;
	},
	render : function(){
		this.table.render();
		this.__renderExportComponent();
		return this;
	},
	__renderExportComponent : function(){
		var thiz = this;
		var trEl = jQuery('<tr>').prependTo(this.getTable().theadEl);
		var td = jQuery('<td>', {
			colSpan : this.getTable().columnDefinesObj.getDataColumnDefines().length
		}).appendTo(trEl);
		jQuery('<input>', {
			type : 'button',
			value : '导出到Excel'
		}).appendTo(td).click(function(){
			var exporter = new com.doubeye.IETableToExcel({
				table : thiz.getTable().rootComponent,
				valueSelector : thiz.valueSelector,
				renderStyle : thiz.renderStyle
			}).getExcelFile();
		});
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