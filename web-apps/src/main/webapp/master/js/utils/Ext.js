/**
 * 该类中主要封装Extjs的一些方法，以达到主键脱离Extjs的依赖
 * 目前已经实现了以下方法
 * Ext.isDefined()
 * Ext.isArray()
 * Ext.Date()
 * Ext.isString()
 * Ext.isBoolean()
 * Ext.isFunction()
 * Ext.id() 对该方法进行了简化
 */
com = {};
com.doubeye = {};
com.doubeye.Ext = {
	__i : 0,
	id : function(){
		return  "doubeye-gen-" + (++ this.__i);
	},
	isDefined : function(object) {
		return typeof object !== 'undefined';
	},
	isArray : function(object) {
		return Object.prototype.toString.apply(object) === '[object Array]';
	},
	isDate : function(object) {
		return Object.prototype.toString.apply(object) === '[object Date]';
	},
	isString : function(object) {
		return typeof object === 'string';
	},
	isBoolean : function(v) {
		return typeof v === 'boolean';
	},
	isFunction : function(e) {
		return typeof e === 'function';
	},
	isJSONObject : function(value) {
        return (typeof(value) == "object") &&
            (Object.prototype.toString.call(value).toLowerCase() == "[object object]") && (!value.length);
    },
    isJSONArray : function(value) {
        return (typeof(value) == "object") &&
            (!isNaN(value.length));
    },
    isJSON : function(value) {
        return com.doubeye.Ext.isJSONArray(value) || com.doubeye.Ext.isJSONObject(value);
    },
}; 