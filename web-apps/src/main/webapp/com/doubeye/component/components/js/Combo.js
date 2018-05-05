/**
 * 下拉选择框
 * @author doubeye
 * @version 1.0.3
 * config
 * value : {Mix} 组件需要显示的值
 * selectOptions {Array<Object{Mix value, String text>}} 组成下拉列表的选项
 * optionURL {URL} 获取组成下拉列表选项的url，url应该返回的类型为{RESULTS : selectionOptions}，
 * 其中selectionOptions的类型如上所述
 * selectOptionAdatper {Object} 用来将url获得的结果集转换为
 * optionGetter : function(Object config, com.doubeye.Combo combo) : 获得下拉列表内容的回调函数，完全有使用者定义
 * optionGetterConfig : {Object} 将此参数传递给optionGetter方法option所使用的{value, text对象}
 * 该参数使用如下:{valueId : [结果集中value的属性名], textId : [结果集中text的属性名]}}
 * 注意：设置下拉列表内容的方法的优先级为  optionGetter > optionURL > selectOptions
 * onChange : function(event, value, selectedOptionObj) : 值改变的事件
 * @history
 * 1.0.2
 *  + config中加入selectOptionAdapter参数
 * 1.0.3
 *  ! 修改setValue(value) 方法，如果value的值为下拉列表项的文字，也可以正确设置Combo的值（方便字典情况下使用）。如果设置的value在值中已经出现，则按正常逻辑设置
 */
com.doubeye.Combo = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.Combo',
	//rootElementTypeName : '<select>',
	selectEl : null,
	init : function() {
		com.doubeye.Combo.superclass.init.call(this);
		return this;
	},
	render : function() {
		var thiz = this;
		com.doubeye.Combo.superclass.render.call(this);
		this.selectEl = jQuery('<select>').appendTo(this.rootComponent);
		this.__renderOptions();
		if (com.doubeye.Ext.isFunction(this.onChange)) {
			this.selectEl.change(function(event){
				thiz.onChange(event, thiz.getValue(), jQuery(thiz.selectEl.children(':selected')));
			});
		}
		this.afterRendering();
		return this;
	},
	__clearOptions : function() {
		this.selectEl.empty();
	},
	__renderOptions : function(){
		var thiz = this;
		this.__clearOptions();
		if (com.doubeye.Ext.isFunction(this.optionGetter)) {
			this.optionGetter(this.optionGetterConfig, this);
		} else if (this.optionURL){
			new com.doubeye.Ajax({
				url : this.optionURL,
				async: false,
			  	cache : false,
			  	showNoSuccessMessage : true,
			  	noWaitingDialog : true,
			  	processResult : function(result) {
			  		thiz.__processOptions(result);
			  	}
			}).sendRequest();
		} else if (this.selectOptions) {
			this.__processOptions(this.selectOptions);
		}
	},
	__processOptions : function(options) {
		var valueId = (this.selectOptionAdatper && this.selectOptionAdatper.valueId) ? this.selectOptionAdatper.valueId : 'value'; 
		var textId = (this.selectOptionAdatper && this.selectOptionAdatper.textId) ? this.selectOptionAdatper.textId : 'text';
		if (!com.doubeye.Ext.isArray(options)) {
			return;
		}
		var option;
		for (var i = 0; i < options.length; i ++) {
			option = options[i];
			jQuery('<option>', {
				text : option[textId],
				value : option[valueId]
			}).appendTo(this.selectEl);
		}
	},
	getValue : function() {
		return this.selectEl.val();
	},
	setValue : function(value) {
		if (this.selectEl.children('option[value="' + value + '"]').length > 0) {
			this.selectEl.val(value);			
		}  else {
			var options = this.selectEl.children('option');
			for (i = 0; i < options.length; i ++) {
				var optionObj = jQuery(options[i]);
				if (optionObj.text() == value) {
					this.selectEl.val(optionObj.val());	
				}
			}
		}
		if (com.doubeye.Ext.isFunction(this.onChange)) {
			var optionEl = jQuery(jQuery(this.selectEl).find('option[value=' + value + ']'));
			this.onChange(null, value, optionEl);
		}
	},
	disable : function() {
		this.selectEl.attr('disabled', 'true');
	},
	enable : function() {
		this.selectEl.removeAttr('disabled');
	}
});