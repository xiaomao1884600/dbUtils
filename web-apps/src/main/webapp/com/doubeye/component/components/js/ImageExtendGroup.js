/**
 * 带大图的缩略图组件
 * @author doubeye
 * @version 1.0.0
 * config
 * thumbPropertyName : {String} 值中每个图片缩略图的属性名
 * imagePropertyName : {String} 值中每个图片原始图的属性名
 * value : {Array<String, String>}
 */
com.doubeye.ImageExtendGroup = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ImageExtendGroup',
	thumbPropertyName : 'thumbFileName',
	imagePropertyName : 'fileName',
	init : function() {
		com.doubeye.ImageExtendGroup.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ImageExtendGroup.superclass.render.call(this);
		if (this.value) {
			this.setValue(this.value);
		}
		return this;
	},
	__renderThumb : function(config) {
		var thiz = this;
		new com.doubeye.Image({
			parent : this.rootComponent,
			value : config[this.thumbPropertyName],
			text : this.text,
			click : function(){
				thiz.showOriginImage(config[thiz.imagePropertyName], thiz.text);
			}
		}).init().render();
	},
	setValue : function(value) {
		var images = com.doubeye.Ext.decode(value);
		if (com.doubeye.Ext.isArray(images)) {
			for (var i = 0; i < images.length; i ++) {
				var config = images[i];
				this.__renderThumb(config);
			}
		}
	},
	showOriginImage : function(url, text) {
		var origin = new com.doubeye.ModalPanelDialog({
			
		}).init().render();
		origin.addItemByConfig({
			editorClassName : 'com.doubeye.Image',
			value : url,
			text : text
		});
	}
});
