/**
 * 年度，月份选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentYearMonth {boolean} 是否显示当前月，默认为true，否则显示空
 *  defaultLocale {String} 默认月份显示的文字，该值比寻在locale属性中出现，否则使用中文（zh-cn）设置
 */
com.doubeye.YearMonthPicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.YearMonthPicker',
	rootElementTypeName : '<DIV>',
	yearEl : null,
	monthEl : null,
	defaultCurrentYearMonth : true,
	defaultLocale : 'zh-cn',
	init : function() {
		com.doubeye.YearMonthPicker.superclass.init.call(this);
		if (!com.doubeye.MonthPicker.locale[this.defaultLocale]) {
			this.defaultLocale = 'zh-cn';
		}
		return this;
	},
	render : function() {
		com.doubeye.YearMonthPicker.superclass.render.call(this);
		if (this.value) {
			this.setValue(this.value);
		}
		this.yearEl = new com.doubeye.YearPicker({
			parent : this.rootComponent,
			defaultCurrentYear : this.defaultCurrentYearMonth
		}).init().render();
		this.monthEl = new com.doubeye.MonthPicker({
			parent : this.rootComponent,
			defaultCurrentMonth : this.defaultCurrentYearMonth,
			rootElementStyle : 'font-family:Trebuchet MS,Tahoma,Verdana,Arial,sans-serif;font-size:1em;font-weight:bold;wertical-align:middle;'
		}).init().render();
		return this;
	},
	getValue : function (){
		return this.yearEl.getValue() + '-' + this.monthEl.getValue();
	},
	/**
	 * 符合ISO_8601格式的日期时间，允许格式包括：yyyy-mm,yyyy-mm-dd,yyyy-mm-dd hh, yyyy-mm-dd hh:mi,yyyy-mm-dd hh:mi:ss，其他格式一律视为不合法
 	 * @param {String} value
	 */
	setValue : function(value){
		var year, month, valid = false;
		if (/^[0-9][0-9][0-9][0-9]-[0-1]{0,1}[0-9]$/.test(value)) {//yyyy-mm形式
			valid = true;
		} else if (com.doubeye.Utils.RegExprestions.dateTime.iso_8601Date.test(value)) {//yyyy-mm-dd形式
			valid = true;
		} else if (/^[0-9][0-9][0-9][0-9]-[0-1]{0,1}[0-9]-[0-3]{0,1}[0-9] (([01]{0,1}[0-9])|(2[0-3]))$/.test(value)) {//yyyy-mm-dd hh形式
			valid = true;
		} else if (/^[0-9][0-9][0-9][0-9]-[0-1]{0,1}[0-9]-[0-3]{0,1}[0-9] (([01]{0,1}[0-9])|(2[0-3])):[0-5]{0,1}[0-9]$/.test(value)) {//yyyy-mm-dd hh:mi形式
			valid = true;
		} else if (com.doubeye.Utils.RegExprestions.dateTime.iso_8601DateTime.test(value)) {
			valid = true;
		}
		if (valid) {
			year = this.__getYear(value);
			month = this.__getMonth(value);
			this.yearEl.setValue(year);
			this.monthEl.setValue(month);
		}
	},
	__getYear : function(value) {
		return com.doubeye.Utils.String.getStringBefore(value, '-');
	},
	__getMonth : function(value) {
		if (value.indexOf('-', value.indexOf('-') + 1) >= 0) {
			return com.doubeye.Utils.String.getStringBetween(value, '-', '-');
		} else {
			return com.doubeye.Utils.String.getStringAfter(value, '-');
		}
	}
});