/**
 * 数据感知基础组件
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * 
 * 
 * 构造函数
 * component {com.doubeye.Component} 被数据感知组件修饰的组件对象
 * data {Object} 数据感知属性包含以下配置项
 * 	column {Column} 该组件所属的列对象，该列对象定义组件数据所属的数据表、数据源等信息
 * 	defaultValue {Mixed} 该组件的默认值，当没有指定值时组件值定位默认值
 * 	validators {Object<String,String>[]} 判断改组件值是否合法的正则表达式，如果同时指定validators属性和validateFunction()方法，则先检查validator,再调用validateFunction()
 * 		validators为校验正则表达是的数组，结构为对象，里面的属性为：
 * 		name {String} 校验名称
 * 		reg {String} 正则表达式
 * 		getInvalidMessage {function(value) { return errorMessage}} 获取校验失败显示信息，value为组件的值
 *  validateFunction {function(value)} 校验获得的值是否有效，如果返回空字符串或true则有效，否则认为无效，如果返回值为字符串，则表示无效信息
 *  TODO 加入简单的逻辑表达式判断（例如一个字段不能小于另外一个字段）和校验类支持
 * 	fromValue {function(value)} 将存储的值转换为UI组件上的方法
 * 	getValue {function() {return value}} 将组件的显示值转换为储存值
 * authority {Object} 组件的权限信息
 * getValue (function()) 获得组件的值，如果定义了getValue,则返回组件的存储值，否则直接返回组件的值
 * getJSON {function()} 获得组件的json表示
 * isValid {function()} 如果返回空字符串则有效，否则认为无效，且返回值表示无效信息
 * needInvalidNofity {boolean}: 是否需要组件无效时提醒，默认为true
 * invalidNofity {function(invalidMessage : String)} 组件无效时ui提醒
 * removeInvalidNotify(function()) 移除无效UI提醒
 * onValueChange function(newValue, oldValue) 当组件值改变时的回调函数
 */
com.doubeye.DataSensor = com.doubeye.Ext.extend(com.doubeye.Component, {
	needInvalidNofity : true,
	/**
	 * 组件值是否被修改过 
	 */
	valueChanged : false,
	onValueChange : null,
	initValue : null,
	init : function(){
		this.data = {};
		com.doubeye.DataSensor.superclass.init.call(this);
	},
	getValue : function() {
		if (this.data.fromValue && typeof(eval(this.data.fromValue))=="function") {
			return this.fromValue();
		} else {
			return "";
		}
	},
	isValid : function(value) {
		var valid = null;
		if (this.data.validators) {
			var reg, validator;
			for (var i = 0; i < this.data.validators.length; i ++) {
				validator = this.data.validators[i];
				reg = new RegExp(eval(this.data.validators[i].reg));
				if (!reg.exec(value)) {
					if (validator.getInvalidMessage && typeof(eval(validator.getInvalidMessage))=="function") {
						valid = validator.getInvalidMessage(value);
					} else {
						valid = (this.data.column ? this.data.column.columnLabel : this.label) + '校验失败';
					}
					break;
				}
			}	
		}
		if (valid == null) {
			if (this.data.validateFunction && typeof(eval(this.data.validateFunction))=="function") {
				valid =  this.data.validateFunction(value);
			}
		}
		if (valid) {
		} else {
			valid = true;
		}
		if (this.needInvalidNofity) {
			if (valid === true) {
				this.removeInvalidNotify();
			} else {
				this.invalidNofity(valid);
			}
		}
		return (valid === true || valid == '') ? '' : valid;
	},
	invalidNofity : function(invalidMessage) {
		this.rootComponent.addClass(this.themePrefix + 'invalid');
	},
	removeInvalidNotify : function() {
		this.rootComponent.removeClass(this.themePrefix + 'invalid');
	},
	getJSON : function() {
		var obj = {};
		obj[column.tableName] = {};
		obj[column.tableName][column.columnName] = this.getValue();
		return obj;
	},
	bindValueChangeEvent : function(valueComponent) {
		var thiz = this;
		if (!valueComponent) {
			valueComponent = this.rootComponent;
		}
		valueComponent.change(function(){
			if (com.doubeye.Ext.isFunction(thiz.onValueChange)) {
				thiz.onValueChange(thiz.getValue(), thiz.initValue);
			}
			thiz.initValue = thiz.getValue(); 
		});
	}
});