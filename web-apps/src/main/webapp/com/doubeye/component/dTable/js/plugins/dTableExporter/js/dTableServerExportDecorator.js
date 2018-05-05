/**
 * jQuery Table数据导出器(由后台导出为Excel或csv)
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 */
com.doubeye.dTableServerExportDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableServerExportDecorator',
	classThemePrefix : 'com-doubeye-default-dTableServerExportDecorator-',
	allRecordEl : '',
	fileTypeEl : null,
	fileTypes : [ 'excel', 'csv'],
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
			'class' :  this.classThemePrefix + 'el',
			value : '导出'
		}).appendTo(td).click(function(){
			thiz.doExport();
		});
		
		this.allRecordEl = jQuery('<input>', {
			type : 'checkbox',
			'class' :  this.classThemePrefix + 'el',
			checked : 'checked'
		}).appendTo(td);
		jQuery('<label>', {
			'class' :  this.classThemePrefix + 'el',
			text : '导出所有数据'
		}).appendTo(td);
		this.__renderFileType(td);
	},
	__renderFileType : function(td){
		jQuery('<label>', {
			'class' : this.classThemePrefix + 'el',
			text : '文件类型'
		}).appendTo(td);
		this.fileTypeEl = jQuery('<select>', {
			'class' :  this.classThemePrefix + 'el'
		}).appendTo(td);
		var type;
		for (var i = 0; i < this.fileTypes.length; i ++) {
			type = this.fileTypes[i];
			jQuery('<option>', {
				text : type,
				value : type
			}).appendTo(this.fileTypeEl);
		}
	},
	doExport : function(){
		this.__setAdditionParams();
		this.getTable().getData();
		this.getTable().__additionParams['export'] = null;
	},
	__setAdditionParams : function(){
		this.getTable().__additionParams['export'] = com.doubeye.Ext.encode({
			full : (this.allRecordEl.attr('checked') == 'checked'),
			fileType : this.fileTypeEl.val()
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