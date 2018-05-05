/**
 * jQuery Table排序设置器
 * @author doubeye
 * @version 1.0.0
 * config
 * columns {Array<com.doubeye.dTableOrder>} 列定义信息数组，具体配置情况参见com.doubeye.dTableOrder
 * orderConfigs　{Array<Object>} 其中每个Object对应一个排序规则，有dataId(String)和order(Enumeration<com.doubeye.constant.DB.ORDER_BY>)组成
 */
com.doubeye.dTableSingleColumnOrderManager = function(config){
	this.className = 'com.doubeye.dTableSingleColumnOrderManager';
	/**
	 * 所有列 
	 */
	allColumns = null;
	/**
	 * 所有参与排序的字段（Array<com.doubeye.dTableOrder>） 
	 */
	orderedColumns = null;
};
com.doubeye.dTableSingleColumnOrderManager.prototype = {
	init : function(){
		this.allColumns = [];
		this.orderedColumns = [];
		return this;
	},
	render : function(){
		return this;
	},
	/**
	 * 增加一个列 
 	 * @param {com.doubeye.dTableOrder} column 
	 */
	addColumn : function(column) {
		this.allColumns.push(column);
	},
	/**
	 * 增加一个排序列 
 	 * @param {com.doubeye.dTableOrder} column 
	 */
	addOrderedColumns : function(column) {
		this.orderedColumns = [];
		this.orderedColumns.push(column);
	},
	changeOrder : function(column) {
		if (column.getValue() == com.doubeye.constant.DB.ORDER_BY.NONE) {
			this.orderedColumns = [];
		} else {
			this.addOrderedColumns(column);
		}
		for (var i = 0; i < this.allColumns.length; i ++) {
			var allColumn = this.allColumns[i];
			if (allColumn.columnDefine.dataId != column.columnDefine.dataId) {
				allColumn.setValue(com.doubeye.constant.DB.ORDER_BY.NONE);
			}
		}
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
				if (column.columnDefine.dataId != orderConfig.dataId) {
					column.setValue(com.doubeye.constant.DB.ORDER_BY.NONE);
				} else {
					column.setValue(orderConfig.order);
					this.addOrderedColumns(column);
				}
			}
		}
	}
};