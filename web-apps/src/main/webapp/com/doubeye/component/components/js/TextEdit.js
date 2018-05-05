/**
 * 单行编辑框组件
 * @author doubeye
 * @version 1.0.2
 * config
 * value : {String} 组件需要显示的值
 * 
 * @history
 * 1.0.1:
 * ! 基类由com.doubeye.Component改为com.doubeye.DataSensor
 * + 加入属性valueType，值为com.doubeye.constant.DATA.DATATYPE枚举中的一个，默认为String
 */
com.doubeye.TextEdit = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.TextEdit',
	rootElementTypeName : '<input>',
	valueType : com.doubeye.constant.DATA.DATATYPE.STRING,
	init : function() {
		com.doubeye.TextEdit.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.TextEdit.superclass.render.call(this);
		//this.rootComponent.attr('type', 'input').val(this.value);
		this.rootComponent.val(this.value);
		if (com.doubeye.Ext.isFunction(this.onValueChange)) {
			this.bindValueChangeEvent();
		}
		return this;
	},
	getValue : function() {
		var returnValue = null;
		var valueFromComp = this.rootComponent.val();
		if (this.valueType == com.doubeye.constant.DATA.DATATYPE.STRING) {
			returnValue = valueFromComp;
		} else if (this.valueType == com.doubeye.constant.DATA.DATATYPE.NUMBER){
			return parseInt(valueFromComp, 10);
		} else if (this.valueType == com.doubeye.constant.DATA.DATATYPE.BOOLEAN){
			if (valueFormComp === "false") {
				returnValue = false;
			} else {
				rreturnValue = Boolean(valueFormComp);
			}
		} else {
			returnValue = this.rootComponent.val();	
		}
		return returnValue;
	},
	setValue : function(value) {
		this.rootComponent.val(value);
	},
	clearValue : function() {
		this.rootComponent.val('');
	}
});
