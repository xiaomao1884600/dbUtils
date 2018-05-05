/**
 * 列数据类型 
 * config 
 * javaClassName {String} 定义这种数据类型的java类
 * dataTypeName {String} 数据库的数据类型名称
 * needLength {Boolean} 该类型定义是否有长度
 * needScale {Boolean} 该类型定义是否有精度
 * length {int} 长度
 * scale {int} 精度
 */
com.doubeye.ColumnType = function(config) {
	this.additionalProperties = [];
	com.doubeye.Ext.apply(this, config);
	if (config.deprecated === "true") {
		this.deprecated = true;
	} else if (config.deprecated === "false") {
		this.deprecated = false;
	}
};

com.doubeye.ColumnType.prototype = {
	className : 'com.doubeye.ColumnType',
	/**
	 * 定义这种数据类型的java类
	 */
	javaClassName : null,
	/**
	 *  数据库的数据类型名称
	 */
	typeName : null,
	/**
	 * 该类型定义是否有长度 
	 */
	needLength : false,
	/**
	 * 该类型定义是否有精度 
	 */
	needScale : false,
	/**
	 * 长度 
	 */
	length : 0,
	/**
	 * 精度 
	 */
	scale : 0,
	/**
	 * 异构数据库统一名称 
	 */
	universalDataTypeName : null,
	/**
	 * ansi名称 
	 */
	ansiDataTypeName : null,
	/**
	 * 最大长度 
	 */
	maxLength : 0,
	/**
	 * 最大精度 
	 */
	maxScale : 0,
	/**
	 * 是否不赞成使用
	 */
	deprecated : false,
	additionalProperties : null,
	getValue : function(){
		var result = {};
		result.typeName = this.typeName;
		result.javaClassName = this.javaClassName;
		if (this.needLength) {
			result.length = this.length;
		}
		if (this.needScale) {
			result.scale = this.scale;
		}
		result.additionalProperties = this.additionalProperties;
		return result;
	},
	//TODO 将列的值回写到对象上
	setValue : function(value) {
		com.doubeye.Ext.apply(this, value);
	}
};
