/**
 *  
 * Checkbox 复选框
 * @author doubeye
 * @version 1.0.1
 * config
 * value : {String} 组件需要显示的值
 * valueStyle {Object} 组件取值说明，具体参数如下：
 *   checkedValue {Mix} 当组件选中时的返回值，默认为true
 *   uncheckedValue {Mix} 当组件未选中的是否的返回值，默认为false
 * label {String} 复选框的说明
 * labelPosition {Enumeration<com.doubeye.Checkbox.LABEL_POSITION>}, 复选框的说明相对于复选框的位置，默认为Checkbox.LABEL_POSITION.RIGHT
 * @history
 * 1.0.1:
 * ! 基类由com.doubeye.Component改为com.doubeye.DataSensor
 */
com.doubeye.Checkbox = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.Checkbox',
	rootElementTypeName : '<div>',
	checkbox : null,
	labelPosition : 1,
	init : function() {
		this.options = [];
		com.doubeye.Checkbox.superclass.init.call(this);
		this.name = this.name ? this.name : com.doubeye.Ext.id();
		if (!this.valueStyle) {
			this.valueStyle = {
				checkedValue : true,
				uncheckedValue : false
			}
			if (!this.valueStyle.checkedValue) {
				this.valueStyle.checkedValue = true;
			}
			if (!this.valueStyle.uncheckedValue) {
				this.valueStyle.uncheckedValue = false;
			}
		}
		return this;
	},
	render : function() {
		com.doubeye.Checkbox.superclass.render.call(this);
		if (this.labelPosition == com.doubeye.Checkbox.LABEL_POSITION.LEFT) {
			this.__renderLabel();	
		}
		this.__renderCheckbox();
		if (this.labelPosition == com.doubeye.Checkbox.LABEL_POSITION.RIGHT) {
			this.__renderLabel();	
		}
		if (com.doubeye.Ext.isFunction(this.onValueChange)) {
			this.bindValueChangeEvent(this.checkbox);
		}
		return this;
	},
	__renderLabel : function() {
		var padding = 'padding-' + ((this.labelPosition == com.doubeye.Checkbox.LABEL_POSITION.LEFT) ? 'right' : 'left') + ':5px;'; 
		jQuery('<label>', {
			text : this.label,
			style : padding
		}).appendTo(this.rootComponent);
	},
	__renderCheckbox : function() {
		this.checkbox = jQuery('<input>', {
			type : 'checkbox'
		}).appendTo(this.rootComponent);
		if (this.value) {
			this.setValue(this.value);
		}
	},
	getValue : function() {
		if (this.checkbox.attr('checked')) {
			return this.valueStyle.checkedValue;
		} else {
			return this.valueStyle.uncheckedValue;
		}
	},
	setValue : function(value) {
		if (this.valueStyle.checkedValue == true) {
			if (value == 'true') {
				value = true;
			} else if (value == 'false') {
				value = false;
			}
		} else if (!isNaN(this.valueStyle.checkedValue)) {
			value = Number(value);
		}
		if (value === this.valueStyle.checkedValue) {
			this.checkbox.attr('checked', 'checked');
		} else {
			this.checkbox.attr('checked', false);
		}
	}
});
com.doubeye.Checkbox.LABEL_POSITION = {
	LEFT : 0,
	RIGHT : 1
};
