/**
 *  
 * RadioButton组
 * @author doubeye
 * @version 1.0.1
 * config
 * value : {String} 组件需要显示的值
 * options : {Array{label<String>, value<Mixed>}} 组成RadioButton的选项，其中label属性为显示值，value为返回值
 * 
 * @history
 * 1.0.1:
 * ! 基类由com.doubeye.Component改为com.doubeye.DataSensor
 */
com.doubeye.RadioButton = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.radioButton',
	init : function() {
		this.options = [];
		com.doubeye.RadioButton.superclass.init.call(this);
		this.name = this.name ? this.name : com.doubeye.Ext.id();
		return this;
	},
	render : function() {
		com.doubeye.RadioButton.superclass.render.call(this);
		this.__renderRadioButtons();
		return this;
	},
	__renderRadioButtons : function(){
		var option, radio;
		for (var i = 0; i < this.options.length; i ++) {
			option = this.options[i];
			jQuery('<label>', {
				html : option.label + '：'
			}).appendTo(this.rootComponent);
			radio = jQuery('<input>', {
				type : 'radio',
				name : this.name
			}).appendTo(this.rootComponent).data('value', option.value);
			if (i < this.options.length - 1) {
				jQuery('<label>', {
					html : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
				}).appendTo(this.rootComponent);
			}
		}
		if (this.options.length > 0){
			jQuery(this.rootComponent.find('input[type=radio]:first')).attr('checked', 'checked');
		}
	},
	getValue : function() {
		return this.rootComponent.find('input[type=radio]:checked').data('value');
	},
	setValue : function(value) {
		var radios = this.rootComponent.find('input[type=radio]'), radioObj;
		for (var i = 0; i < radios.length; i ++) {
			radioObj = jQuery(radios[i]);
			if (radioObj.data('value') == value) {
				radioObj.attr('checked', 'checked');
				break;
			}
		}
	}
});
