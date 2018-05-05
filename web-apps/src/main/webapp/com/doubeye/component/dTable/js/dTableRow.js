/**
 * dTable中显示记录的tr对象
 * config
 * columnDefines {Array<com.doubeye.dTableColumnDefines>} 列定义信息
 * record {com.doubeye.Record} 改行所要显示的数据对象
 * resultSet {com.doubeye.RecordList} 整个table的结果集
 * beforeRenderDataRow {function(row, record, resultSet, rowIndex)} 在绘制数据的一行之前触发的回调函数，
 * customManagedRecordStatus {boolean} 组件自己维护数据状态，不使用自动生成的维护信息，默认为false
 * 	参数为：
 *   table : {com.doubeye.dTable} dTable对象
 *   row {com.doubeye.Row} row对象，其中row.rootComponent为jQuery<tr>对象
 *   record {com.doubeye.Record} 数据对象
 *   resultSet com.doubeye.RecordList 数据集
 *   rowIndex {int} 结果集的行数，兼容用，可以通过record.rowIndex获得
 * @author doubeye
 * @version 1.0.0 
 */
com.doubeye.dTableRow = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableRow',
	customManagedRecordStatus : false,
	init : function() {
		this.rootElementTypeName = '<tr>';
		com.doubeye.dTable.superclass.init.call(this);
		return this;
	},
	render : function() {

		com.doubeye.dTableRow.superclass.render.call(this);
		this.__renderRow();

		return this;
	},
	__renderRow : function() {
		var record = this.record, recordList = this.recordList;
		if (com.doubeye.Ext.isFunction(this.beforeRenderDataRow)) {
			this.beforeRenderDataRow.call(this, this, record, recordList, record.rowIndex);
		}
		var columnDefines = this.columnDefines, columnDefine;
		this.rootComponent.data('record', record);
		for (var i = 0; i < columnDefines.length; i ++) {
			columnDefine = columnDefines[i];
			this.__renderCell(columnDefine);
		}
	},
	/**
	 * 绘制值的单元格
	 */
	__renderCell : function(columnDefine) {
		var data = this.record.data, record = this.record, thiz = this;
		var tdObj = jQuery('<td>', {
			dataId : columnDefine.dataId,
			style : 'text-align:' + columnDefine.contentHorizontalAlign + ';'
			,html : data[columnDefine.dataId]
		});
		/*
		if (columnDefine.hidden) {
			tdObj.hide();
		}
		var div = jQuery('<div>', {
			'class' : 'cellValue'
		}).appendTo(tdObj);
		//如果定义了列的render函数，则使用render函数进行绘制，否则按默认情况进行绘制
		if (com.doubeye.Ext.isFunction(columnDefine.renderValue)) {
			columnDefine.renderValue.call(this, this, tdObj, data[columnDefine.dataId], this.record, this.resultSet, this.record.rowIndex);
		} else if (columnDefine.component) {
			var config = {
				parent : div,
				value : data[columnDefine.dataId],
				//将row对象本身复制给每个组件，用来回调使用
				row : this,
				//onValueChange : this.customManagedRecordStatus === true ? null : function(newValue, oldValue) {
				onValueChange : function(newValue, oldValue) {
					if (record.isAnyValueChanged()) {
						thiz.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED);
					} else {
						thiz.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
					}
				}
			};
			config = com.doubeye.Ext.apply(config, columnDefine.componentConfig);
			var cmp = com.doubeye.Utils.getClassInstance(columnDefine.component, config).init().render();
			this.record.dataComponent[columnDefine.dataId ? columnDefine.dataId : ('dataId' + com.doubeye.Ext.id())] = cmp;
		} else if (columnDefine.componentRender) {
			var dataId = columnDefine.dataId;
			var configDataId = columnDefine.componentRender.configDataId;
			var configObj = com.doubeye.Ext.decode(data[configDataId]);
			if (!configObj) {
				configObj = {};
			}
			if (!configObj.componentConfig) {
				configObj.componentConfig = {};
			}
			configObj.componentConfig = com.doubeye.Ext.apply(configObj.componentConfig, {
				onValueChange : function(newValue, oldValue) {
					if (record.isAnyValueChanged()) {
						thiz.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED);
					} else {
						thiz.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
					}
				}
			});
			var render = new com.doubeye.CellComponentRender({
				parent : div,
				editorClassName : configObj.editorClassName,
				componentConfig : configObj.componentConfig,
				value : data[dataId]
			});
			record.dataComponent[columnDefine.dataId ? columnDefine.dataId : ('dataId' + com.doubeye.Ext.id())] = render.init().render();
			
		} else {
			div.html(data[columnDefine.dataId]);
		}
        */
        tdObj.appendTo(this.rootComponent);
	},
	/**
	 * 获得改行的record对象 
	 * @return {com.doubeye.Record} 该行的record对象
	 */
	getRecord : function() {
		return this.rootComponent.data('record');
	},
	getValue : function() {
		return this.record.getValue();
	},
	getValueById : function(dataId) {
		return this.record.getValueById(dataId);
	},
	setValueById : function(id, value) {
		this.record.modify(id, value);
		this.renderRecordStatus(this.record.getStatus());
	},
	markDelete : function() {
		this.record.markDelete();
		var recordStatus = this.record.getStatus();
		this.renderRecordStatus(recordStatus);
	},
	setStatus : function(status) {
		this.record.setStatus(status);
		var recordStatus = this.record.getStatus();
		this.renderRecordStatus(recordStatus);
	},
	confirmChange : function() {
		this.record.confirmChange();
		var recordStatus = this.record.getStatus();
		this.renderRecordStatus(recordStatus);
	},
	undelete : function() {
		this.record.undelete();
		var status = this.record.getStatus();
		this.renderRecordStatus(status);
	},
	renderRecordStatus : function(status) {
		if (status == com.doubeye.constant.DATA.RECORDSTATUS.ADDED) {
			this.rootComponent.find('.' + this.classThemePrefix + 'eidtIndicator').removeClass().addClass(this.classThemePrefix + 'eidtIndicator ' + this.classThemePrefix + 'eidtIndicator-new');
		} else if (status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED || status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE) {
			this.rootComponent.find('.' + this.classThemePrefix + 'eidtIndicator').removeClass().addClass(this.classThemePrefix + 'eidtIndicator ' + this.classThemePrefix + 'eidtIndicator-delete');
		} else if (status == com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED) {
			this.rootComponent.find('.' + this.classThemePrefix + 'eidtIndicator').removeClass().addClass(this.classThemePrefix + 'eidtIndicator ' + this.classThemePrefix + 'eidtIndicator-new_and_delete');
		} else if (status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED) {
			this.rootComponent.find('.' + this.classThemePrefix + 'eidtIndicator').removeClass().addClass(this.classThemePrefix + 'eidtIndicator ' + this.classThemePrefix + 'eidtIndicator-modified');
		} else if (status == com.doubeye.constant.DATA.RECORDSTATUS.NORMAL) {
			this.rootComponent.find('.' + this.classThemePrefix + 'eidtIndicator').removeClass().addClass(this.classThemePrefix + 'eidtIndicator ');
		}
	},
	isRowDeleted : function() {
		return this.record.isDeleted();
	},
	/**
	 * 获得key属性的值
	 * @return Object key属性的键值对
	 */	
	getKeyFieldValue : function() {
		var keyValue = {};
		var columnDefines = this.columnDefines, columnDefine;
		for (var i = 0; i < columnDefines.length; i ++) {
			columnDefine = columnDefines[i];
			if (columnDefine.key == true) {
				keyValue[columnDefine.dataId] = this.record.getValueById(columnDefine.dataId);
			}
		}
		return keyValue;
	}
});
