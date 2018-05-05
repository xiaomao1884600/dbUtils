/**
 * 时间日期选择组件
 * 该组件封装了jQueryUI.DatePicker，将其默认日期格式按照框架规范表示
 * config
 * defaultCurrentDay {Boolean}当默认日期为空是，是否设置当前值为当前日期，默认为true，如果为false，在选择实际的日期前组件返回空日期
 * value {String} 日期值
 * format {String} 日期格式，默认为yy-mm-dd，即符合ISO_8601规范，该格式详解如下（摘自jQuery DatePicker Widget 文档）
 * Format a date into a string value with a specified format.

    The format can be combinations of the following:

    d - day of month (no leading zero)
    dd - day of month (two digit)
    o - day of the year (no leading zeros)
    oo - day of the year (three digit)
    D - day name short
    DD - day name long
    m - month of year (no leading zero)
    mm - month of year (two digit)
    M - month name short
    MM - month name long
    y - year (two digit)
    yy - year (four digit)
    @ - Unix timestamp (ms since 01/01/1970)
    ! - Windows ticks (100ns since 01/01/0001)
    '...' - literal text
    '' - single quote
    anything else - literal text

    There are also a number of predefined standard date formats available from $.datepicker:

    ATOM - 'yy-mm-dd' (Same as RFC 3339/ISO 8601)
    COOKIE - 'D, dd M yy'
    ISO_8601 - 'yy-mm-dd'
    RFC_822 - 'D, d M y' (See RFC 822)
    RFC_850 - 'DD, dd-M-y' (See RFC 850)
    RFC_1036 - 'D, d M y' (See RFC 1036)
    RFC_1123 - 'D, d M yy' (See RFC 1123)
    RFC_2822 - 'D, d M yy' (See RFC 2822)
    RSS - 'D, d M y' (Same as RFC 822)
    TICKS - '!'
    TIMESTAMP - '@'
    W3C - 'yy-mm-dd' (Same as ISO 8601)
 */
com.doubeye.DatePicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.DatePicker',
	rootElementTypeName : '<INPUT>',
	format : 'yy-mm-dd',
	pickerEl : null,
	defaultCurrentDay : true,
	init : function() {
		com.doubeye.DatePicker.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.DatePicker.superclass.render.call(this);
		this.pickerEl = this.rootComponent.datepicker({
			altFormat: this.format,
			changeYear: true,
			changeMonth : true,
			showButtonPanel: true
		});
		if (!this.value && this.defaultCurrentDay === true) {
			this.setValue(new Date());
		} else if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	getValue : function (){
		return this.rootComponent.val();
	},
	clearValue : function() {
		if (this.defaultCurrentDay === true) {
			this.setValue(new Date());
		} else {
			this.rootComponent.val(null);
		}
	},
	setValue : function(value){
		if (!value) {
			this.rootComponent.val('');
		} else if(com.doubeye.Ext.isDate(value)) {
			this.rootComponent.val(jQuery.datepicker.formatDate(this.format, value));
		} else {
			var date = Date.parse(value);
			if (isNaN(date)) {
				if (this.defaultCurrentDay === true) {
					this.setValue(new Date());
				} else {
					this.rootComponent.val(null);
				}
			} else {
				date = new Date(date);
				this.rootComponent.val(jQuery.datepicker.formatDate(this.format, date));
			}
		}	
	}
});
