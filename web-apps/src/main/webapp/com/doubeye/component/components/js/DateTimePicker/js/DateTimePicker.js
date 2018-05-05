/**
 * 时间日期选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentTime {boolean} 是否显示当前月，默认为true，否则显示空
 */
com.doubeye.DateTimePicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.DateTimePicker',
	rootElementTypeName : '<DIV>',
	dateEl : null,
	timeEl : null,
	defaultCurrentTime : true,
	init : function() {
		com.doubeye.DateTimePicker.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.DateTimePicker.superclass.render.call(this);
		this.dateEl = new com.doubeye.DatePicker({
			parent : this.rootComponent,
			defaultCurrentDay : this.defaultCurrentTime
		}).init().render();
		this.timeEl = new com.doubeye.TimePicker({
			parent : this.rootComponent,
			defaultCurrentTime : this.defaultCurrentTime,
			rootElementStyle : 'font-family:Trebuchet MS,Tahoma,Verdana,Arial,sans-serif;font-size:1em;font-weight:bold;wertical-align:middle;display:inline;'
		}).init().render();
		if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	getValue : function (){
		return this.dateEl.getValue() + ' ' + this.timeEl.getValue();
	},
	/**
	 * 符合ISO_8601格式的日期时间，允许格式包括：yyyy-mm,yyyy-mm-dd,yyyy-mm-dd hh, yyyy-mm-dd hh:mi,yyyy-mm-dd hh:mi:ss，
	 * 不足部分用最小值代替（月份为1月，日期为1，时、分、秒都为00），其他格式一律视为不合法
 	 * @param {String} value
	 */
	setValue : function(value){
		var date, time, valid = false;
		if (/^[0-9][0-9][0-9][0-9]$/.test(value)) {//yyyy-mm形式
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
			var datePart = com.doubeye.Utils.String.getStringBefore(value, ' ');
			var timePart = com.doubeye.Utils.String.getStringAfter(value, ' ');
			date = this.__getDate(datePart);
			time = this.__getTime(timePart);
			this.dateEl.setValue(date);
			this.timeEl.setValue(time);
		}
	},
	__getDate : function(value) {
		var year, month, date;
		year = com.doubeye.Utils.String.getStringBefore(value, '-');
		month = com.doubeye.Utils.String.getStringBetween(value, '-', '-');
		if (!month || month == '') {
			month = '01';
		}
		if (value.indexOf('-', value.indexOf('-') + 1) >= 0) {
			date = com.doubeye.Utils.String.getStringAfterLastDelimiter(value, '-');
		} else {
			date = '01';
		}
		return year + '-' + month + '-' + date;
	},
	__getTime : function(value) {
		var hour = 0, minute = 0, second = 0, valid = false;
		if (/^(([01]{0,1}[0-9])|(2[0-3]))$/.test(value)) {//hh形式
			hour = parseInt(value, 10);
		} else if (/^(([01]{0,1}[0-9])|(2[0-3])):[0-5]{0,1}[0-9]$/.test(value)) {//hh:mi形式
			hour = parseInt(com.doubeye.Utils.String.getStringBefore(value, ':'), 10);
			minute = parseInt(com.doubeye.Utils.String.getStringBetween(value, ':', ':'));
		} else if (com.doubeye.Utils.RegExprestions.dateTime.iso_8601Time.test(value)) {//hh:mi:ss 形式
			hour = parseInt(com.doubeye.Utils.String.getStringBefore(value, ':'), 10);
			minute = parseInt(com.doubeye.Utils.String.getStringBetween(value, ':', ':'));
			second = parseInt(com.doubeye.Utils.String.getStringAfterLastDelimiter(value, ':'));
		}
		return hour + ':' + minute + ':' + second;
	}
});