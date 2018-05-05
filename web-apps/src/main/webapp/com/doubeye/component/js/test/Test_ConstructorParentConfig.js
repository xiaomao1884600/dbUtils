/**
 * 测试不同构造函数中parent的不同的形式
 */
com.doubeye.Test_ConstructorParentConfig = com.doubeye.Ext.extend(com.doubeye.Component, {
	init : function(){
		com.doubeye.Test_ConstructorParentConfig.superclass.init.call(this);
	}, 
	render : function(){
		com.doubeye.Test_ConstructorParentConfig.superclass.render.call(this);
		/**
		 * 测试parent的String形式
		 */
		jQuery('<div>', {
			id : this.rootId + '_idTest'
		}).appendTo(this.rootComponent);
		var c = new com.doubeye.Test_Dummy({
			parent : this.rootId + '_idTest'
		});
		c.init();
		c.render();
		/**
		 * 测试parent的jQuery Element形式
		 */
		var div = jQuery('<div>').appendTo(this.rootComponent);
		c = new com.doubeye.Test_Dummy({
			parent : div
		});
		c.init();
		c.render();
		/**
		 * 测试parent的ExtJs component形式
		 */
		jQuery('<div>', {
			id : this.rootId + '_cmpTest'
		}).appendTo(this.rootComponent);
		var cmp = new Ext.Panel({
			renderTo : this.rootId + '_cmpTest',
			title : 'cmpTest',
			width : 100,
			height : 80
		});
		c = new com.doubeye.Test_Dummy({
			parent : cmp
		});
		c.init();
		c.render();
	}
});

/**
 * 测试类
 * @auther doubeye
 * @version 1.0.0 
 */
com.doubeye.Test_Dummy = com.doubeye.Ext.extend(com.doubeye.Component, {
	init : function(){
		com.doubeye.Test_Dummy.superclass.init.call(this);
	}, 
	render : function(){
		com.doubeye.Test_Dummy.superclass.render.call(this);
		jQuery('<div>', {
			html : 'DUMMY'
		}).appendTo(this.rootComponent);
		jQuery('<input>', {
		}).appendTo(this.rootComponent);
	}
});