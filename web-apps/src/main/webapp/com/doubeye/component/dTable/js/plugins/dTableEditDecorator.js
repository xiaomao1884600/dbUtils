/**
 *  
 * 为dTable加入加入行、删除行的功能
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * position : {'left'|'right'|'both'} ,checkbox在列中的位置，默认为left
 * 
 */
com.doubeye.dTableEditDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableEditDecorator',
	init : function(){
		this.table.init();
		var thiz = this;
		var editIndicatorConfig = [{
			dataId : '$$eidtIndicator',
			resizable : false,
			disableOrder : true,
			disableCondition : true,
			renderValue : function(rowObj, td, value, record, resultSet, rowIndex){
				jQuery('<span>', {
					'class' : 'com-doubeye-default-dTableRow-eidtIndicator'
				}).appendTo(td).click(function(){
					var obj = jQuery(this);
					rowObj.undelete();
				});
			}
		}];
		if (this.position == com.doubeye.constant.POSITION.BOTH_HORIZONTAL) {
			this.getTable().columnDefines = editIndicatorConfig.concat(this.getTable().columnDefines).concat(editIndicatorConfig);
		} else if (this.position == com.doubeye.constant.POSITION.RIGHT) {
			this.getTable().columnDefines = this.getTable().columnDefines.concat(editIndicatorConfig);
		} else {
			this.getTable().columnDefines = editIndicatorConfig.concat(this.getTable().columnDefines);
		}
		return this;
	},
	render : function(){
		this.table.render();
		return this;
	},
	/**
	 * 增加一行 
	 * @param {Object} data 新增行的数据，如果为空，则加入空行
	 * @return {com.doubeye.dTableRow>} 新增加的行对象，其中record属性中保存了com.doubeye.Record对象
	 */
	addRow : function(data) {
		var table = this.getTable(), thiz = this;
		if (com.doubeye.Ext.isFunction(table.beforeRowAdded)) {
			if (!table.beforeRowAdded(table.getCurrentRow())) {
				return;
			}
		}
		var columnDefines = table.columnDefinesObj.getDataColumnDefines();
		var record = new com.doubeye.Record({
			data : data ? data : {}
		});
		table.recordList.addRecord(record);
		var row = new com.doubeye.dTableRow({
				table : table,
				parent : table.tbodyEl,
				columnDefines : columnDefines,
				record : record,
				//recordList : table.recordList,
				resultSet : table.recordList
			}).init().render();
		table.dataRows.addRow(row);
		table.dataRows.setCurrentRow(row);
		row.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.ADDED);
		//为tr加入focus事件，当tr获得焦点时，改变当前的选中行
		row.rootComponent.focusin(function(event){		
			thiz.setCurrentRow(row);
		});
		if (com.doubeye.Ext.isFunction(table.afterRowAdded)) {
			table.afterRowAdded(row);
		}		
		return row;
	},
	/**
	 * 将选中的行标记为删除 
	 */
	markSelectedRowsDeleted : function() {
		var table = this.getTable(), rows = table.dataRows;
		var selectedRows = table.getSelectedRows();
		rows.markRowsDeleted(selectedRows);
		this.clearRowSelection();
	},
	deleteRows : function(rows) {
		var table = this.getTable(), record;
		for (var i = rows.length - 1; i >= 0; i --) {
			record = rows[i].getRecord();
			index = record.rowIndex;
			table.recordList.deleteRecord(index);
			table.dataRows.deleteRow(index);
		}
	},
	/**
	 * 将当前行改为指定行 
	 * @param row {com.doubeye.dTableRow} 将成为当前行的行对象
	 */
	setCurrentRow : function(row) {
		var table = this.getTable();
		//只有当现有的当前行不等于要设置的当前行时才出发事件
		if (row != table.getCurrentRow()) {
			//在改变行之前先做改变事件调用，如果事件返回false，则不改变当前行
			if (com.doubeye.Ext.isFunction(table.beforeRowChange)) {
				if (!table.beforeRowChange(table.getCurrentRow())){
					return;
				} 
			}
			table.dataRows.setCurrentRow(row);
			if (com.doubeye.Ext.isFunction(table.afterCurrentRowChanged)) {
				table.afterCurrentRowChanged(row);
			}
		}
	},
	/**
	 * 获得记录的主键字段值,如果没有设置设置值字段，则返回所有字段值
	 * @param record {com.doubeye.Record} 记录
	 * @param {boolean} returnInitValue 是否返回记录的初始值，默认为true 
	 */
	__getRecordKeyColumnValue : function(record, returnInitValue) {
		var result = {};
		if (returnInitValue !== false) {
			returnInitValue = true;
		}
		var keyColumns = this.getTable().getKeyColumns(), column;
		for (var i = 0; i < keyColumns.length; i ++) {
			column = keyColumns[i];
			if (returnInitValue) {
				result[column.dataId] = record.initData[column.dataId];
			} else {
				result[column.dataId] = record.getValueById(column.dataId);
			}
		}
		return result;
	},
	/**
	 * 获得表格数据改变的内容，结果范围为Object，其中added属性为所有新增的数据对象的数组，modified为所有新增的修改的对象的数组，deleted为所有删除的数据对象数组
	 * @return {Object{added : {Array<Object>}, modified : {Array<Object>}, deleted : {Array<Object>}}} 改变的数据对象 
	 */
	getModified : function() {
		var result = {added : [], modified : [], deleted : []};
		var modifiedRows = this.getRowsModified();
		var addedRows = this.getRowsAdded()
		var deletedRows = this.getRowsMarkDeleted()
		var modifiedAndDeletedRows = this.getRowsModifiedAndDeleted();
		var row, valueObj, record;
		for (var i = 0; i < modifiedRows.length; i ++) {
			row = modifiedRows[i];
			record = row.getRecord();
			valueObj = {
				data : record.getModifiedValue(),
				idData : this.__getRecordKeyColumnValue(record)
			};
			result.modified.push(valueObj);
		}
		for (var i = 0; i < addedRows.length; i ++) {
			row = addedRows[i];
			result.added.push(row.getRecord().getValue());
		}
		for (var i = 0; i < deletedRows.length; i ++) {
			row = deletedRows[i];
			result.deleted.push(row.getRecord().getValue());
		}
		return result;
	},
	/**
	 * 修改确认，删除标记删除的行，新增后标记删除的行以及修改后被删除的行，并将其余Record的状态改为normal 
	 */
	confirmChange : function() {
		var table = this.getTable();
		var markDeletedRows = table.getRowsMarkDeleted();
		this.deleteRows(markDeletedRows);
		var addedAndDeletedRows = table.getRowsAddedAndDeleted();
		this.deleteRows(addedAndDeletedRows);
		var modifiedAndDeletedRows = table.getRowsModifiedAndDeleted();
		this.deleteRows(modifiedAndDeletedRows);
		var rows = table.getAllRows(), row;
		for (var i = 0; i < rows.length; i ++) {
			row = rows[i];
			//row.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
			row.confirmChange();
		}
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
	},
/*	

	getSelectedRows : function() {
		return this.getTable().getSelectedRows();
	},
	renderData : function(datas) {
		this.getTable().renderData(datas);
	},
	
	/**
	 * 获得标记为删除的行
	 * @return {Array<com.doubeye.Record>} 所有标记为删除的行
	/
	getRowsMarkDeleted : function() {
		return this.table.getRowsMarkDeleted();
	},
	/**
	 * 获得新增的行
	 * @return {Array<com.doubeye.Record>} 所有新增的行
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
	 * 获得修改过的行
	 * @return {Array<com.doubeye.Record>} 所有修改过的行
	/
	getRowsModified : function() {
		return this.table.getRowsModified();
	},
	getRowsModifiedAndDeleted : function() {
		return this.table.getRowsModifiedAndDeleted();
	},
	/**
	* 获得指定类型的行
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Record>} 所有指定的行
	/
	__getSpecifiedStatusRows : function(status) {
		return this.table.__getSpecifiedStatusRows(status);
	}
*/
});

