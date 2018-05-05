/**
 * 横向图片绘制组件
 * 组件包括三个img，可以为每个img指定repeat-x,src和width参数
 * @author doubeye
 * @version 1.0.0
 * css :
 * config构造对象
 * height {integer} 组件的高度单位为像素
 * imgConfigs {Object} 设置imgConfigs的属性，详细属性如下
 * 	 imgConfigs = {left : <String>, background : <String>, right : <String>} 
 *   其中left表示左侧图片的相对地址，background表示背景图片相对地址，right表示右侧图片相对地址
 */
//com.doubeye.HorizotalAutoFixedImageRender = Ext.extend(com.doubeye.Component, {
com.doubeye.HorizotalAutoFixedImageRender = com.doubeye.Ext.extend(com.doubeye.Component, {
	init : function(){
		com.doubeye.MainFrame.superclass.init.call(this);
		this.classThemePrefix = this.themePrefix + 'HorizotalAutoFixedImageRender-';
		var thiz = this;
		jQuery(window).resize(function(e){
			thiz.resize();
		});
	},
	render : function() {
		this.rootComponent = jQuery('<div>', {
			style : 'clear:both;'
		}).appendTo(this.parentEl);
		var height = this.config.height ? this.config.height + 'px' : '100px';
		var ul = jQuery('<ul>', {
			style : 'list-style:none outside none;height:' + height
		}).appendTo(this.rootComponent);
		if (this.config.imgConfigs.background) {
			this.rootComponent.attr('style', this.rootComponent.attr('style') + 'background-repeat: repeat-x;background-image: url("' + this.config.imgConfigs.background + '");');
		}
		if (this.config.imgConfigs.left) {
			this.renderImageElement(this.config.imgConfigs.left, 'float:left;').appendTo(ul);
		}
		if (this.config.imgConfigs.right) {
			this.renderImageElement(this.config.imgConfigs.right, 'float:right;').appendTo(ul);
		}
	},
	/**
	 * 绘制图片元素
	 */
	renderImageElement : function(src, style) {
		var li = jQuery('<li>', {
			style : style ? style : ''
		});
		var img = jQuery('<img>', {
			src : src
		}).appendTo(li);
		return li;
	},
	resize : function() {
		var width = 0; 
		var imgs = this.rootComponent.find('img');
		if (imgs.length > 1) {
			jQuery(imgs).each(function(){
				width += jQuery(this).width();
			});
			if (this.rootComponent.width() < width) {
				jQuery(imgs[imgs.length - 1]).hide();
			} else {
				jQuery(imgs[imgs.length - 1]).show();
			}
		}
	}
});
