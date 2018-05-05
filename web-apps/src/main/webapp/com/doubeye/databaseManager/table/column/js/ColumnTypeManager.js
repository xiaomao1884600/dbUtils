/**
 * 列数据类型 
 * config
 * databaseName : 数据库名称 ，值为com.doubeye.databaseManager.database中的一种
 */
com.doubeye.ColumnTypeManager = function(config) {
	com.doubeye.Ext.apply(this, config);
};

com.doubeye.ColumnTypeManager.prototype = {
	className : 'com.doubeye.ColumnTypeManager',
	/**
	 * 数据库名称 ，值为com.doubeye.databaseManager.database中的一种
	 */
	databaseName : null,
	/**
	 * 改数据库所支持的所有数据类型 Array<com.doubeye.ColumnType>
	 */
	supportedTypes : null,
	getAllSupportedDataType : function() {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=getAllDataTypesByDatabaseType', 
			params : {
				database : this.databaseName
			},
			processResult : function(datas){
				thiz.__processTypes(datas);
			},
			async : false,
			noWaitingDialog : true
		}).sendRequest();
	},
	__processTypes : function(types) {
		this.supportedTypes = [];
		if (com.doubeye.Ext.isArray(types)) {
			for (var i = 0; i < types.length; i ++) {
				this.supportedTypes.push(new com.doubeye.ColumnType(types[i]));
			}	
		}
	},
	/**
	 * 根据给定的类型名称获得类型信息
     * @param {Object} typeName
	 */
	getTypeByName : function(typeName) {
		for (var i = 0; i < this.supportedTypes.length; i ++) {
			if (this.supportedTypes[i].typeName == typeName) {
				return com.doubeye.Utils.objectRefactor.clone(this.supportedTypes[i]);
				//return this.supportedTypes[i];
			}
		}
	}
};

com.doubeye.ColumnTypeManager.database = {
	ORACLE : 'ORACLE'
};
