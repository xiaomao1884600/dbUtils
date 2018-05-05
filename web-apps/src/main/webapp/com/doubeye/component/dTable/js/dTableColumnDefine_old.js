/**
 * 用来定义dTable使用的标题信息（列定义信息）
 * @author doubeye
 * @version 1.0.0
 * config 
 * 	label {String} 列的显示名
 *  dataId {String} 数据中的属性名
 *  width {int/percent/'*'} 宽度，可以是像素，百分比以及默认
 *  children {com.doubeye.dTableColumnDefine} 子列（复合表头时使用） 
 *  resizePosition {'left'|'right'|'both'} 可以调整大小时间的位置，默认为both，即左右都可以调整
 */
com.doubeye.dTableColumnDefine = com.doubeye.Ext.extend(com.doubeye.Component, {
	/**
	 *是否处于resize状态中 
	 */
	resizing : false,
	className : 'com.doubeye.dTableColumnDefine',
	init : function() {
		this.config = com.doubeye.Ext.apply(this.config, {
			resizePosition : 'both'
		});
		com.doubeye.dTableColumnDefine.superclass.init.call(this);
		var config = this.config;
		this.rootElementTypeName = '<th>';
		return this;
	},
	render : function() {
		//if (this.parentEl[0].tagName.toLowerCase() != 'table') {
		if (this.parentEl[0].tagName.toLowerCase() != 'row') {
			throw 'com.doubeye.dTableColumnDefine 的父组件必须是<table>';
		}
		com.doubeye.dTableColumnDefine.superclass.render.call(this);
		this.rootComponent.html(this.label);
		jQuery('<div>', {
			html : '||',
			style : 'float:right'
		}).appendTo(this.rootComponent).draggable({ axis: "x"});
		return;
		this.rootComponent.html('dsffd').resizable({handles:"all",            //定义可变化大小的方向,可选值"n, e, s, w, ne, se, sw, nw","all"代表全部
			helper:"proxy",
			//aspectRatio: true,        //按照原有的比例缩放 默认是false
			autoHide: true,          //是否自动隐藏变化控制器
			transparent: false,
			//grid: [10, 10],            //定义x,y轴两个方向的变化步进,单位px
			animate: true,           //定义延迟是否变化大小
			animateDuration: "slow", //变化速度
			animateEasing: "swing",
			ghost: true,             //是否显示变化
			resize:function(e,ui){}}).css('position', 'absolute').addClass('td');
		return;
		var thiz = this;

		var processMouseMoveEvent = function(event){
			var obj = jQuery(this);
			var resizePosition = thiz.resizePosition;
			var left = com.doubeye.Utils.event.getEventOffset(obj, event).left;
			if (((resizePosition == 'left' || resizePosition == 'both') && left < 5) || 
				((resizePosition == 'right' || resizePosition == 'both') && (obj.width() - left < 5))) {
				obj.addClass('resizeElement-hover');
			} else {
				obj.removeClass('resizeElement-hover');
			}
		}
		this.rootComponent.addClass('th').html('th').hover(function(event){
			jQuery(this).html('moving');
			jQuery(this).bind('mousemove', processMouseMoveEvent);
		}, function(){
		}).mousedown(function(event){
			thiz.processMouseDownEvent(this, event);
		}).mouseup(function(event){
			thiz.processMouseUpEvent(this, event);
		});
	},
	/**
	 * 处理鼠标在列上的事件 
 	* @param {Object} sorce th对象
 	* @param {Object} event 事件对象
	 */
	processMouseMoveEvent : function(event) {
		console.log('moving');
		var obj = jQuery(source);
		var resizePosition = this.resizePosition;
		var left = com.doubeye.Utils.event.getEventOffset(obj, event).left;
		if (((resizePosition == 'left' || resizePosition == 'both') && left < 5) || 
			((resizePosition == 'right' || resizePosition == 'both') && (obj.width() - left < 5))) {
			obj.addClass('resizeElement-hover');
		} else {
			obj.removeClass('resizeElement-hover');
		}
	},
		/**
	 * 处理鼠标在列上的单击事件事件 
 	* @param {Object} sorce th对象
 	* @param {Object} event 事件对象
	 */
	processMouseDownEvent : function(source, event) {
		var obj = jQuery(source);
		if (obj.hasClass('resizeElement-hover')) {
			this.resizing = true;
		}
		obj.mousemove(function(evnet){
			if (this.lastX > 0) {
				var width = obj.width();
				console.log(width + ':' + this.lastX +':' + event.screenX);
				obj.css('width', width - (event.screenX - this.lastX) + 'px');	
			}
			this.lastX = event.screenX;
		});
	},
	processMouseUpEvent : function(source, event) {
		this.resizing = false;
		this.lastX = 0;
	},
	processMouseMoveEvent : function(source, event) {
		var obj = jQuery(source);
		if (this.resize) {
			var width = obj.width();
			jQuery(this).width(width + event.y);
		}
	}
});
