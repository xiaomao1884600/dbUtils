/**
 * 封装dTable等行数据的对象
 * @param {Object} config
 * data : {Object} 行的数据对象
 * status : {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} 数据的状态，枚举，参见
 * trObj : {Object<jQuery<tr>>} 显示该数据的行对象
 * rowIndex : {int} 在本地的行号，从0开始
 * @version 1.0.1
 * @history 
 * 1.0.1
 *   + 加入confirmChange方法，将记录的状态转换为normal，同时修改setStatus方法，当记录状态为增加、删除、增加后删除，删除后修改时，调用此方法不会将记录状态改变为normal或msied
 */
com.doubeye.Record = function(config) {
	/**
	 * 在本地的行号
	 */
	this.rowIndex = 0;
	this.status = com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
	this.data = {};
	/**
	 * 每个字段的值对应的component，如果值改变，方便进行值的同步 
	 * 类型为 Object<String, com.doubeye.Component>
	 */
	this.dataComponent = {};
	com.doubeye.Ext.apply(this, config);
	this.initData = com.doubeye.Utils.objectRefactor.clone(this.data);
};

com.doubeye.Record.prototype  = {
	/**
	 * 修改行中某列的值
     * @param {Object} id 列名
     * @param {Object} value 值
	 */
	modify : function(id, value) {
		this.data[id] = value;
		var cmp = this.dataComponent[id];
		if (cmp && com.doubeye.Ext.isFunction(cmp.setValue)) {
			cmp.setValue(value);
		}
		//如果新的值与初始值不相等，并且记录的状态不是com.doubeye.constant.DATA.RECORDSTATUS.ADD和com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED，则将记录的状态置为被修改
		if ((this.status != com.doubeye.constant.DATA.RECORDSTATUS.ADDED) && 
			(this.status != com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED) && 
			(this.status != com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED) && 
			(this.status != com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE)) {
			if (!com.doubeye.Utils.objectRefactor.equals(value, this.initData[id])) {
				//1.0.0
				//this.status = com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED;
				this.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED);
			} else {
				//1.0.0
				//this.status = com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
				this.setStatus(com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
			}
		}
	},
	/**
	 * 将记录标记为删除 
	 */
	markDelete : function() {
		if (this.status == com.doubeye.constant.DATA.RECORDSTATUS.ADDED || this.status == com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED) {
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED;
		} else if (this.status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED) {
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE;
		} else {
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED;
		}
	},
	undelete : function() {
		if (this.status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED) {
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
		} else if (this.status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE) {
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED;
		} else if (this.status == com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED){
			this.status = com.doubeye.constant.DATA.RECORDSTATUS.ADDED;
		}
	},
	/**
	 * 获得数据的状态 
	 */
	getStatus : function() {
		return this.status;
	},
	/**
	 * 将记录状态置为正常 
	 */
	setNormal : function() {
		alert('setNormal')
		this.status = com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
	},
	/**
	 * 将记录转换为初始值，即丢弃对记录的修改 
	 */
	restoreInitValue : function() {
		this.data = this.initData;
	},
	/**
	 * 设置列值与显示组件的关联
     * @param {String} id 数据的列名
     * @param {com.doubeye.Component} component 对应该字段的component
	 */
	setDataComponent : function(id, component) {
		this.dataComponent[id] = component;
	},
	/**
	 * 获得指定列的组件
     * @param {String} id 数据的列名
     * @return {com.doubeye.Component} 对应该字段的component，如果没有指定component，则返回空
	 */	
	getDataComponent : function(id) {
		return this.dataComponent[id];
	},
	setStatus : function(status) {
		/* 被注释代码为1.0.0版本，参见history 1.0.1
		if (status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED) {
			this.markDelete();
		} else {
			this.status = status;
		}
		if (status == com.doubeye.constant.DATA.RECORDSTATUS.NORMAL) {
			this.initData = com.doubeye.Utils.objectRefactor.clone(this.data);
		}
		*/
		
		if (status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED) {
			this.markDelete();
			return;
		}
		//如果已经是以下几种状态：新增，新增删除，修改删除，并准备将状态改为修改或普通是，忽略其修改，此时如果需将记录状态改为正常，需调用setConfirm()
		if ((this.status == com.doubeye.constant.DATA.RECORDSTATUS.ADDED || this.status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED || this.status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE) && 
		(status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED || status == com.doubeye.constant.DATA.RECORDSTATUS.NORMAL)) {
			return;
		} else {
			this.status = status;
		}

	},
	getValue : function() {
		var componentNames = com.doubeye.Utils.objectRefactor.getFields(this.dataComponent), name, cmp;
		for (var i = 0; i < componentNames.length; i++) {
			name = componentNames[i];
			cmp = this.getDataComponent(name);
			if (cmp && com.doubeye.Ext.isFunction(cmp.getValue)) {
				this.modify(name, this.getDataComponent(name).getValue());
			}
		}
		return this.data;
	},
	/**
	 * 获得指定列的值 
     * @param {String} dataId 要获得值的属性名
     * @retuen {Mixed} 指定属性的值
	 */
	getValueById : function(dataId) {
		//如果是一个组件，需要从组件中读取值
		var cmp = this.dataComponent[dataId]
		if (cmp && com.doubeye.Ext.isFunction(cmp.getValue)) {
			return cmp.getValue();
		} else {
			return this.data[dataId];
		}
	},
	/**
	 * 获得与初始化状态相比较时的呗改变的值，没有改变的值不包含在返回结果中
	 * @return 变化过的行数据
	 */
	getModifiedValue : function() {
		this.getValue();
		//感觉下一行有问题，两个fields都是取自，我改了一下，fields改为取自data
		//var result = {}, originFields = com.doubeye.Utils.objectRefactor.getFields(this.initData), fields = com.doubeye.Utils.objectRefactor.getFields(this.initData), fieldName;
		var result = {}, originFields = com.doubeye.Utils.objectRefactor.getFields(this.initData), fields = com.doubeye.Utils.objectRefactor.getFields(this.data), fieldName;
		
		for (var i = 0; i < originFields.length; i ++) {
			fieldName = originFields[i];
			if (!com.doubeye.Utils.objectRefactor.equals(this.initData[fieldName], this.data[fieldName])) {
				result[fieldName] = this.data[fieldName];
				for (var j = 0; j < fields.length; j ++) {
					if (fields[j] == fieldName) {
						fields.splice(j, 1);
					}
				}
			}
		}
		for (var i = 0; i < fields.length; i ++){
			fieldName = fields[i];
			if (!com.doubeye.Utils.objectRefactor.equals(this.initData[fieldName], this.data[fieldName])) {
				result[fieldName] = this.data[fieldName];
			}
		}
		
		return result;
	},
	getModifiedValueWithOriginalKeyValue : function() {
		var originKeyValue = {};
		var componentFields = com.doubeye.Utils.objectRefactor.getFields(this.dataComponent);
		for (var i = 0; i < componentFields.length; i ++) {
			var cmp = this.dataComponent[componentFields[i]];
			if (cmp.key == true) {
				originKeyValue[cmp.name] = this.getInitValueById(cmp.name);
			}		
		}
		return {
			value : this.getModifiedValue(),
			originKeyValue : originKeyValue
		}
	},
	isDeleted : function() {
		return this.status == com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED || this.status == com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED || this.status == com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE;
	},
	isAnyValueChanged : function() {
		if (this.status != com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED && this.status != com.doubeye.constant.DATA.RECORDSTATUS.NORMAL) {
			return false;
		}
		var componentNames = com.doubeye.Utils.objectRefactor.getFields(this.dataComponent), name, cmp, value;
		var change = false;
		for (var i = 0; i < componentNames.length; i++) {
			name = componentNames[i];
			cmp = this.getDataComponent(name);
			if (cmp && com.doubeye.Ext.isFunction(cmp.getValue)) {
				value = this.getDataComponent(name).getValue();
			}
			if (!com.doubeye.Utils.objectRefactor.equals(value, this.initData[name])) {
				change = true;
				break;
			}
		}
		if (!change) {
			this.setNormal();
		}
		return change;
	},
	confirmChange : function() {
		this.status = com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
		this.initData = com.doubeye.Utils.objectRefactor.clone(this.data);
	},
	setValue : function(value) {
		var fields = com.doubeye.Utils.objectRefactor.getFields(value), id;
		for (var i = 0; i < fields.length; i ++) {
			id = fields[i];
			this.data[id] = value[id];
			var cmp = this.dataComponent[id];
			if (cmp && com.doubeye.Ext.isFunction(cmp.setValue)) {
				cmp.setValue(value[id]);
			}
		}
		this.initData = com.doubeye.Utils.objectRefactor.clone(this.data);
	},
	getInitValueById : function(id) {
		return this.initData[id];
	},
	/**
	 * 获得key属性的值
	 * @return Object key属性的键值对
	 */
	getKeyFieldValue : function(){
		var keyValue = {};
		var componentFields = com.doubeye.Utils.objectRefactor.getFields(this.dataComponent);
		for (var i = 0; i < componentFields.length; i ++) {
			var cmp = this.dataComponent[componentFields[i]];
			if (cmp.key == true) {
				keyValue[cmp.name] = this.getValueById(cmp.name);
			}		
		}
		return keyValue;
	}
}
