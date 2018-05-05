/**
 *  
 * jQuery Table对象的行复选框修饰器
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * position : {'left'|'right'|'both'} ,checkbox在列中的位置，默认为left
 */
com.doubeye.dTableRowCheckboxDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTableRowCheckboxDecorator',
	init : function(){
		this.table.init();
		var thiz = this;
		var checkboxConfig = [{
			dataId : '$$rowCheckbox',
			resizable : false,
			disableOrder : true,
			disableCondition : true,
			renderValue : function(rowObj, td, value, record, resultSet, rowIndex){
				jQuery('<input>', {
					type : 'checkbox'
				}).appendTo(td).click(function(){
					var trObj = jQuery(td.parents('tr'));
					var record = trObj.data('record');
					var checked = (jQuery(this).attr('checked') == 'checked');
					thiz.table.selectRow(record.rowIndex, checked);
					if (thiz.position == com.doubeye.constant.POSITION.BOTH_HORIZONTAL) {
						var trObj = jQuery(td.parent('tr'));
						var tdsObj = jQuery(trObj.find('td[dataid="$$rowCheckbox"]'));
						var checkboxes = tdsObj.find('input[type="checkbox"]');
						for (var i = 0; i < checkboxes.length; i ++) {
							if (checkboxes[i] != this) {
								jQuery(checkboxes[i]).attr('checked', checked);
							}
						}
					}
				});
			}
		}];
		if (this.position == com.doubeye.constant.POSITION.BOTH_HORIZONTAL) {
			this.getTable().columnDefines = checkboxConfig.concat(this.getTable().columnDefines).concat(checkboxConfig);
		} else if (this.position == com.doubeye.constant.POSITION.RIGHT) {
			this.getTable().columnDefines = this.getTable().columnDefines.concat(checkboxConfig);
		} else {
			this.getTable().columnDefines = checkboxConfig.concat(this.getTable().columnDefines);
		}
		return this;
	},
	clearRowSelection : function() {
		var dataRows = this.getTable().dataRows;
		for (var i = 0; i < dataRows.getSize(); i ++) {
			dataRows.getRow(i).rootComponent.find('td[dataid="$$rowCheckbox"] > input[type="checkbox"]').attr('checked', false);
		}
		this.table.clearRowSelection();
	},
	render : function(){
		this.table.render();
		return this;
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

