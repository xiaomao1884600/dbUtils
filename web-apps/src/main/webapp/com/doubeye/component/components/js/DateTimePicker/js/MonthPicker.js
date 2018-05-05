/**
 * 月份选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentMonth {boolean} 是否显示当前月，默认为true，否则显示空
 *  defaultLocale {String} 默认月份显示的文字，该值比寻在locale属性中出现，否则使用中文（zh-cn）设置
 */
com.doubeye.MonthPicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.MonthPicker',
	rootElementTypeName : '<SELECT>',
	defaultCurrentMonth : true,
	defaultLocale : 'zh-cn',
	init : function() {
		com.doubeye.MonthPicker.superclass.init.call(this);
		if (!com.doubeye.MonthPicker.locale[this.defaultLocale]) {
			this.defaultLocale = 'zh-cn';
		}
		return this;
	},
	render : function() {
		com.doubeye.MonthPicker.superclass.render.call(this);
		this.__renderOptions();
		if (!this.value && this.defaultCurrentMonth === true) {
			this.setValue(jQuery.datepicker.formatDate('mm', new Date()));
		} else if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	__renderOptions : function() {
		var values = com.doubeye.MonthPicker.locale[this.defaultLocale], value;
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
		this.rootComponent.val(value);
	}
});
/**
 * 各种语言月份的表达 
 */
com.doubeye.MonthPicker.locale =  {
		'zh-cn' : [{
			text : '',
			value : null
		}, {
			text : '一月',
			value : '01'
		}, {
			text : '二月',
			value : '02'
		}, {
			text : '三月',
			value : '03'
		}, {
			text : '四月',
			value : '04'
		}, {
			text : '五月',
			value : '05'
		}, {
			text : '六月',
			value : '06'
		}, {
			text : '七月',
			value : '07'
		}, {
			text : '八月',
			value : '08'
		}, {
			text : '九月',
			value : '09'
		}, {
			text : '十月',
			value : '10'
		}, {
			text : '十一月',
			value : '11'
		}, {
			text : '十二月',
			value : '12'
		}]
	};