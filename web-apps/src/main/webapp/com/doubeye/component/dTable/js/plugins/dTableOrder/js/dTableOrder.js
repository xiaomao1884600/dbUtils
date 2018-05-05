/**
 * jQuery Table字段排序对象
 * @author doubeye
 * @version 1.0.0
 * config
 * columnDefine {com.doubeye.dTableColumnDefine} 列定义信息，具体配置情况参见com.doubeye.dTableColumnDefine
 * columnOrderManager {com.doubeye.dTableColumncolumnOrderManager} 多列排序定义类
 * displayOrderSetup  {boolean} 是否显示所有列的排序设置
 * noNotify {boolean} 是否立即响应排序的改变
 * orderDecorator {com.doubeye.dTableOrderDecorator} table修饰器，用来通知dTable排序
 */
com.doubeye.dTableOrder = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableOrder',
	value : com.doubeye.constant.DB.ORDER_BY.NONE,
	rootElementTypeName : '<span>',
	__orderEl : null,
	init : function(){
		com.doubeye.dTableOrder.superclass.init.call(this);
		return this;
	},
	render : function(){
		com.doubeye.dTableOrder.superclass.render.call(this);
		this.rootComponent.addClass(this.classThemePrefix + 'main');
		this.__renderOrder();
		if ((this.columnOrderManager && this.columnOrderManager.className == 'com.doubeye.dTableMultiColumnOrderManager') && !(this.displayOrderSetup === false)) {
			this.__renderToAdvance();
		}
		return this;
	},
	__renderOrder : function(){
		var thiz = this;
		this.__orderEl = jQuery('<span>', {
			'class' : this.classThemePrefix + 'order ' + this.classThemePrefix + 'order-none'
		}).appendTo(this.rootComponent).click(function(){
			if (thiz.value == com.doubeye.constant.DB.ORDER_BY.NONE || thiz.value == com.doubeye.constant.DB.ORDER_BY.DESC) {
				thiz.value = com.doubeye.constant.DB.ORDER_BY.ASC;
				thiz.__orderEl.removeClass(thiz.classThemePrefix + 'order-none').removeClass(thiz.classThemePrefix + 'order-desc').addClass(thiz.classThemePrefix + 'order-asc');
			} else if (thiz.value == com.doubeye.constant.DB.ORDER_BY.NONE || thiz.value == com.doubeye.constant.DB.ORDER_BY.ASC) {
				thiz.value = com.doubeye.constant.DB.ORDER_BY.DESC;
				thiz.__orderEl.removeClass(thiz.classThemePrefix + 'order-none').removeClass(thiz.classThemePrefix + 'order-asc').addClass(thiz.classThemePrefix + 'order-desc');
			}
			if (thiz.columnOrderManager) {
				thiz.columnOrderManager.changeOrder(thiz);
			}
			if (thiz.noNotify != true) {
				thiz.orderDecorator.doOrder();
			}
			
		});
	},
	__renderToAdvance : function(){
		var thiz = this;
		jQuery('<span>', {
			'class' : this.classThemePrefix + 'toAdvance'
		}).appendTo(this.rootComponent).click(function(){
			thiz.columnOrderManager.openDialog({
				width : 620,
				height : 330
			});
		});
	},
	/**
	 * 设置排序方法 
 	 * @param {Enumeration} value 排序方法，可选值参考com.doubeye.canstant.DB.ORDER_BY
	 */
	setValue : function(value) {
		this.value = value;
		this.__orderEl.removeClass(this.classThemePrefix + 'order-none').removeClass(this.classThemePrefix + 'order-asc').removeClass(this.classThemePrefix + 'order-desc');
		if (value == com.doubeye.constant.DB.ORDER_BY.NONE) {
			this.__orderEl.addClass(this.classThemePrefix + 'order-none');
		} else if (value == com.doubeye.constant.DB.ORDER_BY.ASC){
			this.__orderEl.addClass(this.classThemePrefix + 'order-asc');
		} else if (value == com.doubeye.constant.DB.ORDER_BY.DESC) {
			this.__orderEl.addClass(this.classThemePrefix + 'order-desc');
		}
	},
	/**
	 * 取得排序的值 
	 */
	getValue : function(){
		return this.value;
	},
	getConfig : function(){
		return {
			dataId : this.columnDefine.dataId,
			order : this.getValue()
		};
	}
});
