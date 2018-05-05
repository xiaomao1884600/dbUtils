/**
 * 测试组件改变大小的事件传递，测试通过，应捕捉window的resize事件
 */
com.doubeye.Test_ComponentRecursiveResize = com.doubeye.Ext.extend(com.doubeye.Component, {
	init : function() {
		com.doubeye.Test_ComponentRecursiveResize.superclass.init.call(this);
	},
	render : function() {
		com.doubeye.Test_ComponentRecursiveResize.superclass.render.call(this);
		var parentWidth = this.parentEl.width();
		for (var i = 1; i <= 5; i ++) {
			var div = jQuery('<div>').appendTo(this.rootComponent);
			var cmp = new com.doubeye.Test_Resizeable({
				parent : div,
				index : i
			});
			cmp.init();
			cmp.render();
			this.resizeNotifyComponents.push(cmp);
		}
		var thiz = this;
		jQuery('<input>', {
			type : 'button',
			value : '改变大小'
		}).click(function() {
			thiz.setSize(800, 400);
		}).appendTo(this.rootComponent);
	},
	resize : function(){
		var parentWidth = this.parentEl.width();
		jQuery.each(this.resizeNotifyComponents, function(){
			this.div.html(this.div.html() + 'changed');
		});
	}
});


com.doubeye.Test_Resizeable = com.doubeye.Ext.extend(com.doubeye.Component, {
	/**
	 * 测试用
	 */
	div : null,
	init : function() {
		com.doubeye.Test_Resizeable.superclass.init.call(this);
	},
	render : function() {
		com.doubeye.Test_Resizeable.superclass.render.call(this);
		var parentWidth = this.parentEl.attr('width');
		this.div = jQuery('<div>' , {
			html : this.config.index,
			style : 'border:' + this.config.index + 'px solid;width:' + (this.config.index * 10)+ '%;'
		}).appendTo(this.rootComponent);
	}
});
