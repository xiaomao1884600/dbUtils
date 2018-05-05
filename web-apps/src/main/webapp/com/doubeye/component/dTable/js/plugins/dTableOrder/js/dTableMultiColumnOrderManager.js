/**
 * jQuery Table排序设置器
 * @author doubeye
 * @version 1.0.0
 * config
 * columns {Array<com.doubeye.dTableOrder>} 列定义信息数组，具体配置情况参见com.doubeye.dTableOrder
 * orderDecorator {com.doubeye.dTableOrderDecorator} table修饰器，用来通知dTable排序
 */
com.doubeye.dTableMultiColumnOrderManager = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableMultiColumnOrderManager',
	//value : com.doubeye.constant.DB.ORDER_BY.NONE,
	/**
	 * 所有列
	 */
	allColumns : null,
	/**
	 * 所有参与排序的字段（Array<com.doubeye.dTableOrder>） 
	 */
	orderedColumns : null,
	/**
	 * 保存所有列的页面元素 
	 */
	allColumnEl : null,
	/**
	 * 保存所有加入排序的列的页面元素 
	 */
	orderedColumnEl : null,
	init : function(){
		this.allColumns = [];
		this.orderedColumns = [];
		com.doubeye.dTableMultiColumnOrderManager.superclass.init.call(this);
		return this;
	},
	render : function(){
		com.doubeye.dTableMultiColumnOrderManager.superclass.render.call(this);
		this.__renderAllColumns();
		this.__renderSeperator();
		this.__renderOrderedColumns();
		this.__renderClear();
		this.__renderButton();
		return this;
	},
	__renderAllColumns : function(){
		var column;
		this.allColumnEl = jQuery('<div>', {
			'class' : this.classThemePrefix + 'allColumns' + ' com-doubeye-default-float-left'
		}).appendTo(this.rootComponent);
		if (this.columns) {
			for (var i = 0; i < this.columns.length; i ++) {
				column = this.columns[i];
				this.addColumn(column);				
			}
		}
	},
	__renderSeperator : function() {
		jQuery('<div>', {
			'class' : 'com-doubeye-default-float-left'
		}).appendTo(this.rootComponent);
	},
	__renderOrderedColumns : function() {
		var column;
		var div = jQuery('<div>', {
			'class' : this.classThemePrefix + 'orderedColumns' + ' com-doubeye-default-float-right'
		}).appendTo(this.rootComponent);
		this.orderedColumnEl = jQuery('<ul>').appendTo(div);
		if (this.columns) {
			for (var i = 0; i < this.columns.length; i ++) {
				column = this.columns[i];
				this.addOrderedColumns(column);				
			}
		}
	},
	__renderClear : function(){
		jQuery('<div>', {
			'class' : 'com-doubeye-default-clear-float' 
		}).appendTo(this.rootComponent);
	},
	/**
	 * 绘制按钮栏 
	 */
	__renderButton : function(){
		var div = jQuery('<div>', {
			'class' : this.classThemePrefix + 'buttonDiv'
		}).appendTo(this.rootComponent);
		var thiz = this;
		jQuery('<input>', {
			type : 'button',
			value : '确定'
		}).appendTo(div).click(function(){
			thiz.confirmChange();
		});
		jQuery('<input>', {
			type : 'button',
			value : '取消'
		}).appendTo(div).click(function(){
			thiz.rootComponent.dialog('close');
		});
	},
	/**
	 * 增加一个列 
 	 * @param {com.doubeye.dTableOrder} column 
	 */
	addColumn : function(column) {
		var thiz = this;
		var columnEl = jQuery('<div>' , {
			'class' : this.classThemePrefix + 'columnItem'
		}).appendTo(this.allColumnEl);
		jQuery('<input>', {
			type : 'checkbox'
		}).appendTo(columnEl).data('orderObj', column).click(function(){
			var obj = jQuery(this);
			var selected = obj.attr('checked') == 'checked';
			var order = obj.data('orderObj');
			if (selected) {
				thiz.addOrderedColumns(order);
			} else {
				thiz.removeOrderedColumn(order);
			}
		});
		jQuery('<label>', {
			text : column.columnDefine.label + "（" + column.columnDefine.dataId + "）"
		}).appendTo(columnEl);
		this.allColumns.push(column);
	},
	/**
	 * 增加一个排序列 
 	 * @param {com.doubeye.dTableOrder} column 
	 */
	addOrderedColumns : function(column) {
		var thiz = this;
		var columnEl = jQuery('<li>' , {
			}).appendTo(this.orderedColumnEl);
		var div = jQuery('<div>', {
			'class' : this.classThemePrefix + 'columnItem'
		}).appendTo(columnEl);
		jQuery('<label>', {
			text : column.columnDefine.label + "（" + column.columnDefine.dataId + "）"
		}).appendTo(div);
		var order = new com.doubeye.dTableOrder({
			columnDefine : column.columnDefine,
			displayOrderSetup : false,
			parent : div,
			noNotify : true
		}).init().render();
		this.orderedColumns.push(order);
		columnEl.data('orderObj', order);
		var value = (column.getValue() == com.doubeye.constant.DB.ORDER_BY.NONE ? com.doubeye.constant.DB.ORDER_BY.ASC : column.getValue());
		order.setValue(value);
		//允许拍序列调整先后顺序
		this.orderedColumnEl.sortable();
	},
	/**
	 * 移除一个排序列 
 	 * @param {com.doubeye.dTableOrder} column 
	 */	
	removeOrderedColumn : function(column) {
		var lis = this.orderedColumnEl.find('li');
		for (var i = 0; i < lis.length; i ++) {
			var o = jQuery(lis[i]).data('orderObj');
			if (column.columnDefine.dataId == o.columnDefine.dataId) {
				jQuery(lis[i]).remove();
				break;
			}					
		}
		for (var i = 0; i < this.orderedColumns.length; i ++) {
			if (this.orderedColumns[i].columnDefine.dataId == column.columnDefine.dataId) {
				this.orderedColumns.splice(i, 1);
				break;
			}
		}
	},
	/**
	 * 修改排序列的排序值，如果拍序列尚未tianjia,则将该列添加到新的拍序列
	  * @param {com.doubeye.dTableOrder} column 
	 */
	changeOrder : function(column) {
		if (column.getValue() == com.doubeye.constant.DB.ORDER_BY.NONE) {
			this.removeOrderedColumn(column);
		} else {
			var inputObjs = jQuery(this.allColumnEl.find('input'));
			var order, inputObj;
			for (var i = 0; i < inputObjs.length; i ++) {
				inputObj = jQuery(inputObjs[i]);
				order = inputObj.data('orderObj');
				if (order.columnDefine.dataId == column.columnDefine.dataId) {
					inputObj.attr('checked', 'checked');
					break;
				}
			}
			for (var i = 0; i < this.orderedColumns.length; i ++) {
				if (this.orderedColumns[i].columnDefine.dataId == column.columnDefine.dataId) {
					this.orderedColumns[i].setValue(column.getValue());
					return;
				}
			}
			this.addOrderedColumns(column);
		}
	},
	/**
	 * 打开对话框
	 */
	openDialog : function(config){
		for (var i = 0; i < this.allColumns.length; i ++) {
			this.changeOrder(this.allColumns[i]);
		}
		com.doubeye.dTableMultiColumnOrderManager.superclass.openDialog.call(this, config);
	},
	/**
	 * 使列排序修改生效 
	 */
	confirmChange : function() {
		this.orderedColumns = [];
		var lis = this.orderedColumnEl.find('li');
		for (var i = 0; i < lis.length; i ++) {
			var order = jQuery(lis[i]).data('orderObj');
			if (order) {
				this.orderedColumns.push(order);
			}
		}
		
		var column, orderedColumn, find;
		for (var i = 0; i < this.allColumns.length; i ++) {
			column = this.allColumns[i];
			find = false;
			for (var j = 0; j < this.orderedColumns.length; j ++) {
				orderedColumn = this.orderedColumns[j];
				if (column.columnDefine.dataId == orderedColumn.columnDefine.dataId) {
					find = true;
					break;
				}
			}
			if (find) {
				column.setValue(orderedColumn.getValue());
			} else {
				column.setValue(com.doubeye.constant.DB.ORDER_BY.NONE);
			}
		}
		this.rootComponent.dialog('close');
		this.orderDecorator.doOrder();
	},
	getValue : function() {
		var result = [];
		for (var i = 0; i < this.orderedColumns.length; i ++) {
			result.push(this.orderedColumns[i].getConfig());
		}
		return result;
	},
	/**
	 * 设置排序规则
 	 * @param {Object} orderConfig
	 */
	setValue : function(orderConfigs) {
		for (var i = 0; i < orderConfigs.length; i ++) {
			var orderConfig = orderConfigs[i];
			for (var j = 0; j < this.allColumns.length; j ++) {
				var column = this.allColumns[j];
				if (column.columnDefine.dataId == orderConfig.dataId) {
					column.setValue(orderConfig.order);
					break;
				}
			}
		}
	}
});