/**
 * 表的列管理器
 * config
 * databaseName {String} 数据库类型，值为com.doubeye.ColumnTypeManager.database中的一个
 */
com.doubeye.TableColumnManager = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.TableColumnManager',
	/**
	 *  数据库类型，值为com.doubeye.ColumnTypeManager.database中的一个
	 */
	databaseName : null,
	/**
	 * 数据库数据类型管理器
	 */
	__columnTypeManager : null,
	/**
	 * 列定义表格 
	 */
	__columnDefineTable : null,
	/**
	 * 列详细信息面板 
	 */
	__columnDetialPanel : null,
	init : function() {
		com.doubeye.TableColumnManager.superclass.init.call(this);
		this.__columnTypeManager = new com.doubeye.ColumnTypeManager(this.databaseName);
		this.__columnTypeManager.getAllSupportedDataType();
		jQuery('<div>', {
			'class' : this.themePrefix + 'clear'
		}).appendTo(this.rootComponent);
		return this;
	},
	render : function() {
		com.doubeye.TableColumnManager.superclass.render.call(this);
		this.__renderToolbar();
		this.__renderColumnDefineTable();
		this.__renderColumnDetialPanel();
		return this;
	},
	__renderToolbar : function() {
		var thiz = this;
		var div = jQuery('<div>', {
			'class' : 'ui-widget-header'
		}).appendTo(this.rootComponent);
		jQuery('<button>', {
			id : this.rootId + '_addColumn',
			text : '新增'
		}).appendTo(div).button().click(function(event){
			thiz.addColumn();
		});
		jQuery('<button>', {
			id : this.rootId + '_removeColumn',
			text : '删除'
		}).appendTo(div).button().click(function(){
			thiz.removeSelectedColumn();
		});
	},
	__renderColumnDefineTable : function() {
		var thiz = this;
		var div = jQuery('<div>', {
			'class' : this.themePrefix + 'float-left' 
		}).appendTo(this.rootComponent);
		var columnDefines = [{
			label : '列名',
			columnName : 'columnName',
			dataId : 'columnName',
			keyColumn : true,
			component : 'com.doubeye.TextEdit',
			componentConfig : {
				rootElementStyle : 'width:100%;'
			},
			width : '240'
		}, {
			label : '数据类型',
			columnName : 'dataType',
			dataId : 'dataType',
			component : 'com.doubeye.ColumnTypeCombo',
			componentConfig : {
				columnTypeManager : this.__columnTypeManager,
				onChange : function(value) {
					thiz.columnTypeChanged(value);
				}
			},
			width : 240
		}];
		this.__columnDefineTable = com.doubeye.dTableFacory.getTable({
			parent : div,
			columnDefines : columnDefines,
			autoLoad : false,
			beforeRowAdded : function(currentRow) {
				return thiz.beforeCurrnetRowChange(currentRow);
			},
			afterRowAdded : function(addedRow) {
				var dataTypeCmp = addedRow.getRecord().getDataComponent('dataType');
				dataTypeCmp.selectEl.change();
			},
			beforeRowChange : function(currentRow) {
				return thiz.beforeCurrnetRowChange(currentRow);
			},
			afterCurrentRowChanged : function(newRow) {
				thiz.columnDefineActivated(newRow);
			}
		}, {
			rowCheckbox : {position : com.doubeye.constant.POSITION.LEFT},
			edit : true
		}).init().render();
		var i= 1; 
	},
	/**
	 * 绘制列详细信息面板 
	 */
	__renderColumnDetialPanel : function() {
		var thiz = this;
		var div = jQuery('<div>', {
			'class' : this.themePrefix + 'float-left',
			style : 'padding-left:20px;'
		}).appendTo(this.rootComponent);
		this.__columnDetailPanel = new com.doubeye.ColumnDetailPanel({
			table : this.__columnDefineTable,
			parent : div,
			columnTypeManager : this.__columnTypeManager,
			onChange : function(){
				thiz.__columnDetailPanel.getValue();
			},
			afterAnyValueChanged : function() {
				var value = thiz.__columnDetailPanel.getValueFromPanel();
				thiz.__columnDefineTable.getCurrentRow().setValueById('dataType', value);
			}
		}).init().render();
	},
	/**
	 * 加入列
	 */
	addColumn : function(column) {
		var row = this.__columnDefineTable.addRow();
		if (column) {
			row.setValueById('columnName', column.columnName);
			this.__columnDetailPanel.setLength(column.length);
			this.__columnDetailPanel.setScale(column.scale);
			var dataType = this.__serverColumnTypeResultAdatper(column);
			this.columnTypeChanged(dataType);
			//由于数据结构比较复杂，因此没有直接获得数据，而是将列的定义信息加入到列定义表格中，需要将record的状态转为正常，
			//并且设置此状态时应该在设置完列的各属性之后进行，避免列变为修改状态
			//row.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
			row.confirmChange();
		}
	},
	/**
	 * 将服务器传递回来的列定义信息转换为columnCombo需要的对象 
	 */
	__serverColumnTypeResultAdatper : function(column) {
		var dataTypeName = column.dataType.dataTypeName;
			var dataType = this.__columnTypeManager.getTypeByName(dataTypeName);
		var result = {
			typeName : dataType.typeName,
			javaClassName : dataType.javaClassName,
			length : column.length,
			scale : column.scale,
			additionalProperties : {}
		};
		var additionalProperties = column.dataType.additionalProperties, additionalPropertie;
		for (var i = 0; i < additionalProperties.length; i ++) {
			additionalPropertie = additionalProperties[i];
			result.additionalProperties[additionalPropertie.name] = additionalPropertie.propertyValue;
		}
		return result;
	},
	/**
	 * 删除选中的列 
	 */
	removeSelectedColumn : function() {
		this.__columnDefineTable.markSelectedRowsDeleted();
	},
	/**
	 * 确认修改 
	 */
	confirmChange : function() {
		this.__columnDefineTable.confirmChange();
	},
	columnTypeChanged : function(value) {
		this.__columnDetailPanel.setType(value);
		this.__columnDefineTable.getCurrentRow().getRecord().modify('dataType', value);
	},
	/**
	 * 在转换列编辑状态之前触发，判断列的配置是否正确 
	 */	
	beforeCurrnetRowChange : function(currentRow) {
		if (!currentRow) {
			return true;
		}
		var errMsgs = this.__columnDetailPanel.getInvalidMessages();
		if (errMsgs.length > 0) {
			this.__columnDetailPanel.showErrorMessages(errMsgs);
			return false;
		} else {
			var record = this.__columnDefineTable.getCurrentRow().getRecord();
			//record.columnName = this.record.getValue('columnName');
			var value = this.__columnDetailPanel.getValue();
			var columnName = record.getValueById('columnName');
			record.modify('dataType', value);
			record.modify('columnName', columnName);
			return true;
		}
	},
	columnDefineActivated : function(newRow){
		var typeDefine = newRow.getValueById('dataType');
		if (typeDefine) {
			this.__columnDetailPanel.setLength(typeDefine.length);
			this.__columnDetailPanel.setScale(typeDefine.scale);
		}
		this.columnTypeChanged(typeDefine);
	},
	getModifiedValue : function() {
		var value = this.__columnDetailPanel.getValue();
		this.__columnDefineTable.getCurrentRow().getRecord().modify('dataType', value);
		var result = this.__columnDefineTable.getModified();
		//将重命名的列单独列出
		var modifiedColumns = result.modified;
		result.renamed = [];
		for (var i = 0; i < modifiedColumns.length; i ++) {
			var modifiedColumn = modifiedColumns[i];
			if (modifiedColumn.idData.columnName != modifiedColumn.data.columnName) {
				result.renamed.push({
					originColumnName : modifiedColumn.idData.columnName,
					newColumnName : modifiedColumn.data.columnName
				});
			}
		} 
		return result;
	},
	getValue : function() {
		var value = this.__columnDetailPanel.getValue();
		this.__columnDefineTable.getCurrentRow().getRecord().modify('dataType', value);
		var result = this.__columnDefineTable.getValue();
		return result;
	},
	isValid : function(){
		var row, notDeletedRowCount = 0;
		var currentRow = this.__columnDefineTable.getCurrentRow();
		if (currentRow) {
			currentRow.getValue();
			if (!currentRow.isRowDeleted()) {
				var errMsgs = this.__columnDetailPanel.getInvalidMessages();
				if (errMsgs.length > 0) {
					return errMsgs;
				}		
			}
		}
		for (var i = 0; i < this.__columnDefineTable.getRecordCount(); i ++) {
			row = this.__columnDefineTable.getRow(i);
			if (!row.isRowDeleted()) {
				var columnName = row.getValueById('columnName');
				notDeletedRowCount ++
				if (!columnName) {
					return '列名不能为空';
				}
			}
		}
		if (notDeletedRowCount == 0) {
			if (this.__columnDefineTable.getRecordCount() == 0) {
				return '数据表至少需要包含1个列'
			} else {
				return '数据表至少需要包含1个没有被删除的列'
			}
		}
		return '';
	}
});
