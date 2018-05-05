/**
 * 年度，季度选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentYearQuarter {boolean} 是否显示当前月，默认为true，否则显示空
 *  defaultLocale {String} 默认月份显示的文字，该值比寻在locale属性中出现，否则使用中文（zh-cn）设置
 */
com.doubeye.YearQuarterPicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.YearQuarterPicker',
	rootElementTypeName : '<DIV>',
	yearEl : null,
	quarterEl : null,
	defaultCurrentYearQuarter : true,
	defaultLocale : 'zh-cn',
	init : function() {
		com.doubeye.YearQuarterPicker.superclass.init.call(this);
		if (!com.doubeye.QuarterPicker.locale[this.defaultLocale]) {
			this.defaultLocale = 'zh-cn';
		}
		return this;
	},
	render : function() {
		com.doubeye.YearQuarterPicker.superclass.render.call(this);
		if (this.value) {
			this.setValue(this.value);
		}
		this.yearEl = new com.doubeye.YearPicker({
			parent : this.rootComponent,
			defaultCurrentYear : this.defaultCurrentYearQuarter
		}).init().render();
		this.quarterEl = new com.doubeye.QuarterPicker({
			parent : this.rootComponent,
			defaultCurrentQuarter : this.defaultCurrentYearQuarter,
			rootElementStyle : 'font-family:Trebuchet MS,Tahoma,Verdana,Arial,sans-serif;font-size:1em;font-weight:bold;wertical-align:middle;'
		}).init().render();
		return this;
	},
	getValue : function (){
		return this.yearEl.getValue() + '~' + this.quarterEl.getValue();
	},
	/**
	 * 符合ISO_8601格式的日期时间，允许格式包括：yyyy-mm,yyyy-mm-dd,yyyy-mm-dd hh, yyyy-mm-dd hh:mi,yyyy-mm-dd hh:mi:ss，其他格式一律视为不合法
	 * 或yyyy~qq，即年度~季度格式
 	 * @param {String} value
	 */
	setValue : function(value){
		var year, quarter, valid = false;
		if (/^[0-9][0-9][0-9][0-9]~[0]{0,1}[1-4]$/.test(value)) {//yyyy~qq形式
			valid = true;
		} else if (/^[0-9][0-9][0-9][0-9]-[0-1]{0,1}[0-9]$/.test(value)) {//yyyy-mm形式
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
			quarter = this.__getQuarter(value);
			this.yearEl.setValue(year);
			this.quarterEl.setValue(quarter);
		}
	},
	__getYear : function(value) {
		if (value.indexOf('~') > 0) {
			return com.doubeye.Utils.String.getStringBefore(value, '~');
		} else {
			return com.doubeye.Utils.String.getStringBefore(value, '-');
		}
	},
	__getQuarter : function(value) {
		if (value.indexOf('~') > 0) {
			return com.doubeye.Utils.String.getStringAfter(value, '~');
		} else {
			var month;
			if (value.indexOf('-', value.indexOf('-')) >= 0) {
				month = com.doubeye.Utils.String.getStringBetween(value, '-', '-');
			} else {
				month = com.doubeye.Utils.String.getStringAfter(value, '-');
			}
			if (month) {
				var quarter = Math.ceil(month / 3);
				quarter = quarter > 4 ? null : quarter;
				return quarter;
			} else {
				return null;
			}
		}
	}
});