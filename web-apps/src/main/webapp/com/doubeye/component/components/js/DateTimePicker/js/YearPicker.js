/**
 * 年份选择器 
 * @version 1.0.0
 * @author doubeye
 */
com.doubeye.YearPicker = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.YearPicker',
	rootElementTypeName : '<INPUT>',
	pickerEl : null,
	defaultCurrentYear : true,
	init : function() {
		com.doubeye.YearPicker.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.YearPicker.superclass.render.call(this);
		this.pickerEl = this.rootComponent.spinner({});
		this.pickerEl.keydown(function(event) {
			return com.doubeye.Utils.RegExprestions.number.naturalNumber.test(String.fromCharCode(event.keyCode)) || event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39;
		});
		if (!this.value && this.defaultCurrentYear === true) {
			this.setValue(jQuery.datepicker.formatDate('yy', new Date()));
		} else if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	getValue : function (){
		if (!com.doubeye.Utils.RegExprestions.number.naturalNumber.test(this.pickerEl.val())) {
			return '';
			this.pickerEl.val('');
		}
		return this.rootComponent.val();
	},
	setValue : function(value){
		if (!com.doubeye.Utils.RegExprestions.number.naturalNumber.test(value)) {
			return;
		}
		this.pickerEl.val(value);
	}
});
