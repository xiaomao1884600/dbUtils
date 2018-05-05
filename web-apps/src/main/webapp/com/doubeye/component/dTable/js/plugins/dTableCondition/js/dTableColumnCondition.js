/**
 * jQuery Table字段条件
 * @author doubeye
 * @version 1.0.0
 * config
 * columnDefine {com.doubeye.dTableColumnDefine} 列定义信息，具体配置情况参见com.doubeye.dTableColumnDefine
 * conditionDecorator {com.doubeye.dTableConditionDecorator} table修饰器，用来通知dTable条件的变化
 * showConditionInTh {boolean} 是否在设置条件后在字段标题处显示该条件的值，默认为true，即显示
 */
com.doubeye.dTableColumnCondition = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableColumnCondition',
	rootElementTypeName : '<span>',
	value : null,
	__valueFloatEl : null,
	__valueEl : null,
	__titleValueEl : null,
	init : function(){
		com.doubeye.dTableColumnCondition.superclass.init.call(this);
		return this;
	},
	render : function(){
		var thiz = this;
		com.doubeye.dTableColumnCondition.superclass.render.call(this);
		jQuery('<span>').appendTo(this.rootComponent).addClass(this.classThemePrefix + 'main').click(function(event){
			thiz.__renderSimpleCondionFloatEl(event.clientX, event.clientY);
		});
		/*
		if ((this.columnOrderManager && this.columnOrderManager.className == 'com.doubeye.dTableMultiColumnOrderManager') && !(this.displayOrderSetup === false)) {
			this.__renderToAdvance();
		}
		*/
		return this;
	},
	__renderSimpleCondionFloatEl : function(left, top){
		var thiz = this;
		this.__valueFloatEl = jQuery('<div>').appendTo(this.rootComponent).click(function(event){
			event.stopPropagation();
		});
		this.__valueEl = jQuery('<input>', {
			type : 'text',
			value : this.value ? this.value : ''
		}).appendTo(this.__valueFloatEl);
		jQuery('<input>', {
			type : 'button',
			value : '确定'
		}).appendTo(this.__valueFloatEl).click(function(event){
			var value = thiz.__valueEl.val();
			thiz.setValue(value);
			thiz.conditionDecorator.doCondition();
		});
		this.__valueFloatEl.float({
			left : left + 16,
			top : top - this.__valueFloatEl.innerHeight() / 4
		});
	},
	/**
	 * 设置排序方法 
 	 * @param {String} value
	 */
	setValue : function(value) {
		//this.value = this.__valueEl.val();
		this.value = value;
		if (this.__valueFloatEl) {
			this.__valueFloatEl.unfloat({
				detach : true
			});
		}
		if (this.showConditionInTh) {
			if (this.__titleValueEl) {
				this.__titleValueEl.detach();
			}
			if (this.value) {
				var valueString =  this.value;
				if (this.value.length > 5) {
					valueString = this.value.substr(0,5) + '...';
				}
				this.__titleValueEl = jQuery('<label>', {
					text : '[' + valueString + ']',
					title : this.value
				}).appendTo(this.rootComponent).tooltip();
				/*
				this.__titleValueEl.tooltip({
					content : this.value
				});
				*/
			}
		}
	},
	/**
	 * 取得排序的值 
	 */
	getValue : function(){
		return this.value;
	},
	getConfig : function(){
		return (this.value ? {
			dataId : this.columnDefine.dataId,
			value : this.value
		} : null);
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
	}
});
