/**
 * 带大图的缩略图组件
 * @author doubeye
 * @version 1.0.0
 * config
 * value : {Object} 其中thubmUrl表示缩略图的url，imgUrl表示原图的url，text表示图片显示不出来的提示
 */
com.doubeye.ImageExtend = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ImageExtend',
	thumbCmp : null,
	init : function() {
		com.doubeye.ImageExtend.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ImageExtend.superclass.render.call(this);
		this.__renderThumb();
		return this;
	},
	__renderThumb : function() {
		var thiz = this;
		this.thumbCmp = new com.doubeye.Image({
			parent : this.rootComponent,
			value : this.value.thumbUrl,
			text : this.value.text,
			click : function(){
				thiz.showOriginImage(thiz.value.imgUrl, thiz.value.text);
			}
		}).init().render();
	},
	setValue : function(value) {
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
