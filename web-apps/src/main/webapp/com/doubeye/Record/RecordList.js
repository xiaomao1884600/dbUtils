/**
 * 封装dTable等行对象列表的对象
 * @param {Object} config
 * records : {com.doubeye.Record} 数据对象
 * ref : [com.doubeye.Record]
 */
com.doubeye.RecordList = function(config) {
	/**
	 * 在本地的行号
	 */
	this.records = [];
	com.doubeye.Ext.apply(this, config);
};

com.doubeye.RecordList.prototype = {
	/**
	 * 在末尾插入一行
	 * @param {Object} record
	 */
	addRecord : function(record) {
		if (!record) {
			record = this.__getEmptyRecordInstance(this.records.length);
		}
		record.rowIndex = this.records.length;
		this.records.push(record);
	},
	/**
	 * 在末尾插入一行
	 * @param {Object} data com.doubeye.Reocrd的data对象
	 * @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	 */
	addRecordData : function(data, status) {
		var record = new com.doubeye.Record({
			data : data,
			status : status ? status : com.doubeye.constant.DATA.RECORDSTATUS.ADDED
		});
		this.addRecord(record);
	},
	/**
	 * 将data添加到现有数据的末尾
	 * @param {Array(Object)} datas 要添加的数据 
	 *  * @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	 */
	addAllData : function(datas, status) {
		if (!datas || !com.doubeye.Ext.isArray(datas)){
			return;
		}
		for (var i = 0; i < datas.length; i ++) {
			this.addRecordData(datas[i], status);
		}
	},
	/**
	 * 在指定的行前插入一行
 	 * @param {com.doubeye.Record} record 行的数据
     * @param {int} index 指定的行编号
	 */
	insertBefore : function(record, index) {
		index = com.doubeye.Utils.array.fixArrayIndex(this.records, index);
		if (!record) {
			record = this.__getEmptyRecordInstance();
		}
		this.records.splice(index, 0, record);
		this.__rearrangeRecordIndex();
	},
	/**
	 * 在指定的行后插入一行
 	 * @param {com.doubeye.Record} record 行的数据
     * @param {int} index 指定的行编号
	 */	
	insertAfter : function(record, index) {
		index = com.doubeye.Utils.array.fixArrayIndex(this.records, index);
		if (!record) {
			record = this.__getEmptyRecordInstance();
		}
		this.records.splice(index + 1, 0, record);	
		this.__rearrangeRecordIndex();
	},
	/**
	 * 删除指定行 
 	 * @param {int} index
	 */
	deleteRecord : function(index) {
		index = com.doubeye.Utils.array.fixArrayIndex(this.records, index);
		this.records.splice(index, 1);
		this.__rearrangeRecordIndex();
	},
	/**
	 * 根据给定的条件删除所有符合条件的行，条件condition类型为Object，将condition中所有的属性与Record中的data对象做比较
 	 * @param {Object} condition 条件对象
 	 * @param {int} startIndex 开始查找的行号
	 */
	deleteByCondition : function(condition, startIndex) {
		startIndex = com.doubeye.Utils.array.fixArrayIndex(this.records, startIndex);
		var records = this.findAll(condition, startIndex);
		for (var i = records.length - 1; i >= 0; i --) {
			this.deleteRecord(records[i].rowIndex);
		}
		this.__rearrangeRecordIndex();
	},
	/**
	 * 根据给定的条件查找第一个符合条件的行，条件condition类型为Object，将condition中所有的属性与Record中的data对象做比较
 	 * @param {Object} condition 条件对象
 	 * @param {int} startIndex 开始查找的行号
 	 * @return {com.doubeye} 从startIndex开始第一个符合条件的行，如果没有符合条件的行，则返回null
	 */	
	find : function(condition, startIndex) {
		startIndex = com.doubeye.Utils.array.fixArrayIndex(this.records, startIndex);
		return com.doubeye.Utils.array.getObjectFromArray(condition, this.records, startIndex, 'data');
	},
	/**
	 * 根据给定的条件查找所有符合条件的行，条件condition类型为Object，将condition中所有的属性与Record中的data对象做比较
 	 * @param {Object} condition 条件对象
 	 * @param {int} startIndex 开始查找的行号
 	 * @return {Array<com.doubeye.Record>} 从startIndex开始所有符合条件的行，如果没有符合条件的行，则返回[]
	 */		
	findAll : function(condition, startIndex) {
		startIndex = com.doubeye.Utils.array.fixArrayIndex(this.records, startIndex);
		var results = [], result, i = startIndex;
		while (i < this.records.length) {
			result = com.doubeye.Utils.array.getObjectFromArray(condition, this.records, i, 'data');
			if (result == null) {
				break;
			} else {
				i = result.rowIndex + 1;
				results.push(result);
			}
		}
		return results;
	},
	/**
	 *  取得指定记录
	 *  @param {int} index 记录的行号
	 *  @return {com.doubeye.Record} 指定的行号
	 */
	get : function(index) {
		return this.records[index];
	},
	/**
	 * 清除记录集 
	 */	
	clear : function() {
		this.records = [];
	},
	/**
	 * 获得结果集的大小 
	 */
	getSize : function() {
		return this.records.length;
	},
	/**
	 * 获得标记为删除的列
	 * @return {Array<com.doubeye.Record>} 所有标记为删除的列
	 */
	getRowsMarkDeleted : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED);
	},
	/**
	 * 获得新增的列
	 * @return {Array<com.doubeye.Record>} 所有新增的列
	 */
	getRowsAdded : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.ADDED);
	},
	/**
	 * 获得修改过的列
	 * @return {Array<com.doubeye.Record>} 所有修改过的列
	 */
	getRowsModified : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED);
	},
	/**
	* 获得指定类型的列
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Record>} 所有指定的列
	*/
	__getSpecifiedStatusRows : function(status) {
		var result = [], record;
		for (var i = 0; i < this.records.length; i ++) {
			record = this.records[i];
			if (record.status == status) {
				result.push(record);
			}
		}
		return result;
	},
	/**
	 * 重新排列记录的rowIndex 
	 */
	__rearrangeRecordIndex : function() {
		for (var i = 0; i < this.records.length; i ++) {
			this.records[i].rowIndex = i;
		}
	},
	/**
	 * 获得一个空的com.doubeye.Record对象
     * @param {int} rowIndex 行号
     * @return {com.doubeye.Record} 空的com.doubeye.Record对象
	 */
	__getEmptyRecordInstance : function() {
		return new com.doubeye.Record({
			status : com.doubeye.constant.DATA.RECORDSTATUS.ADDED
		});
	},
	/**
	 * 对结果集里的数据进行排序 
     * @param {Array<Object>} configs 排序规则配置数组，配置如下{dataId : (String), order : (Enumeration<com.doubeye.constant.DB.ORDER_BY>)},其中为排序的属性值
	 * @param {Boolean} isNullSmallest 如果属性为null，或者为undefined，是否作为最小值看待，没人为true，如果值为false，控制则被视为最大值
	 */
	doSort : function(configs, isNullSmallest) {
		com.doubeye.ArraySortor.doSort(this.records, configs, true, 'data');
	}
}
