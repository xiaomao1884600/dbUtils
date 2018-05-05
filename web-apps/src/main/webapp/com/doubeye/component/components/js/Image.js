/**
 * Image组件
 * @author doubeye
 * @version 1.0.0
 * config
 * value : {url} 获得图片的url
 * text : {String} 无法获得图片时的替换文字
 * event
 * click : {function} 点击图片的回调函数
 */
com.doubeye.Image = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.Image',
	rootElementTypeName : '<img>',
	init : function() {
		com.doubeye.Image.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.Image.superclass.render.call(this);
		this.rootComponent.attr('src', this.value);
		if (this.text) {
			this.rootComponent.attr('alt', this.text);
		}
		var thiz = this;
		if (com.doubeye.Ext.isFunction(this.click)) {
			this.rootComponent.click(function() {
				thiz.click();
			});
		}
		return this;
	},
	setValue : function(value) {
		this.rootComponent.html(value);
		this.rootComponent.attr('src', this.url);
	}
});
