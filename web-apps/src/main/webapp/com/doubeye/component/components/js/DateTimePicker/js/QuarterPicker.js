/**
 * 季度选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentQuarter {boolean} 是否显示当前季度，默认为true，否则显示空
 *  defaultLocale {String} 默认季度显示的文字，该值比寻在locale属性中出现，否则使用中文（zh-cn）设置
 */
com.doubeye.QuarterPicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.QuarterPicker',
	rootElementTypeName : '<SELECT>',
	defaultCurrentQuarter : true,
	defaultLocale : 'zh-cn',
	init : function() {
		com.doubeye.QuarterPicker.superclass.init.call(this);
		if (!com.doubeye.MonthPicker.locale[this.defaultLocale]) {
			this.defaultLocale = 'zh-cn';
		}
		return this;
	},
	render : function() {
		com.doubeye.QuarterPicker.superclass.render.call(this);
		this.__renderOptions();
		if (!this.value && this.defaultCurrentQuarter === true) {
			var month = parseInt(jQuery.datepicker.formatDate('mm', new Date()), 10);
			var quarter = Math.ceil(month / 3);
			quarter = quarter > 4 ? null : quarter;
			this.setValue(quarter);
		} else if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	__renderOptions : function() {
		var values = com.doubeye.QuarterPicker.locale[this.defaultLocale], value;
		for (var i = 0; i < values.length; i ++) {
			value = values[i];
			jQuery('<option>', {
				value : value.value,
				text : value.text
			}).appendTo(this.rootComponent);
		}
	},
	getValue : function (){
		return this.rootComponent.val();
	},
	setValue : function(value){
		value = parseInt(value, 10);
		if (value > 4) {
			value = null;
		}
		this.rootComponent.val(value);
	}
});
/**
 * 各种语言月份的表达 
 */
com.doubeye.QuarterPicker.locale =  {
		'zh-cn' : [{
			text : '',
			value : null
		}, {
			text : '第一季度',
			value : 1
		}, {
			text : '第二季度',
			value : 2
		}, {
			text : '第三季度',
			value : 3
		}, {
			text : '第四季度',
			value : 4
		}]
	};
