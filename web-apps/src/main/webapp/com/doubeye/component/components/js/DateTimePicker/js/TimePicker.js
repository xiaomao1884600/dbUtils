/**
 * 时间选择器
 * @version 1.0.0
 * @author doubeye
 * config
 *  defaultCurrentTime {boolean} 是否显示当前月，默认为true，否则显示空
 *  showHore {boolean} 是否显示小时部分，默认为true
 *  showMinete {boolean} 是否显示分钟部分，默认为true
 *  showSecond {boolean} 是否显示秒部分，默认为true
 */
com.doubeye.TimePicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.TimePicker',
	rootElementTypeName : '<DIV>',
	hourEl : null,
	minuteEl : null,
	secondEl : null,
	defaultCurrentTime : true,
	showHore : true,
	showMinete : true,
	showSecond : true,
	init : function() {
		com.doubeye.TimePicker.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.TimePicker.superclass.render.call(this);
		this.hourEl = this.__renderHour();
		jQuery('<label>', {
			text : ':'
		}).appendTo(this.rootComponent);
		this.minuteEl = this.__renderMinute();
		jQuery('<label>', {
			text : ':'
		}).appendTo(this.rootComponent);
		this.secondEl = this.__renderSecond();
		if (this.value) {
			this.setValue(this.value);
		} else if (this.defaultCurrentTime) {
			var time = new Date();
			this.hourEl.val(time.getHours());
			this.minuteEl.val(time.getMinutes());
			this.secondEl.val(time.getSeconds());
		}
		return this;
	},
	__renderHour : function(){
		var select = jQuery('<select>').appendTo(this.rootComponent);
		jQuery('<option>', {
			text : '',
			value : null
		}).appendTo(select);
		for (i = 0; i <= 23; i ++) {
			jQuery('<option>', {
				text : i < 10 ? '0' + i : '' + i,
				value : i
			}).appendTo(select);
		}
		return select;
	},
	__renderMinute : function(){
		var select = jQuery('<select>').appendTo(this.rootComponent);
		jQuery('<option>', {
			text : '',
			value : null
		}).appendTo(select);
		for (i = 0; i <= 59; i ++) {
			jQuery('<option>', {
				text : i < 10 ? '0' + i : '' + i,
				value : i
			}).appendTo(select);
		}
		return select;
	},
	__renderSecond : function(){
		return this.__renderMinute();
	},
	getValue : function (){
		var hour = this.hourEl.val(), minute = this.minuteEl.val(), second = this.secondEl.val();
		hour = hour < 10 ? '0' + hour : hour;
		minute = minute < 10 ? '0' + minute : minute;
		second = second < 10 ? '0' + second : second;
		return hour + ':' + minute + ':' + second;
	},
	/**
	 * 符合ISO_8601格式的时间，允许格式包括：hh, hh:mi, hh:mi:ss，其他格式一律视为不合法
 	 * @param {String} value
	 */
	setValue : function(value){
		var hour, minute = 0, second = 0, valid = false;
		if (/^(([01]{0,1}[0-9])|(2[0-3]))$/.test(value)) {//hh形式
			valid = true;
			hour = parseInt(value, 10);
		} else if (/^(([01]{0,1}[0-9])|(2[0-3])):[0-5]{0,1}[0-9]$/.test(value)) {//hh:mi形式
			valid = true;
			hour = parseInt(com.doubeye.Utils.String.getStringBefore(value, ':'), 10);
			minute = parseInt(com.doubeye.Utils.String.getStringBetween(value, ':', ':'));
		} else if (com.doubeye.Utils.RegExprestions.dateTime.iso_8601Time.test(value)) {//hh:mi:ss 形式
			valid = true;
			hour = parseInt(com.doubeye.Utils.String.getStringBefore(value, ':'), 10);
			minute = parseInt(com.doubeye.Utils.String.getStringBetween(value, ':', ':'));
			second = parseInt(com.doubeye.Utils.String.getStringAfterLastDelimiter(value, ':'));
		}
		if (valid) {
			this.hourEl.val(hour);
			this.minuteEl.val(minute);
			this.secondEl.val(second);
		}
	}
});