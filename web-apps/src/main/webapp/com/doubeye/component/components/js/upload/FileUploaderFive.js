/**
 * 文件上传组件，由uploadifive提供实现，基于HTML5 
 * config
 * fileTypeExts {String} 逗号分隔的文件扩展名限制，默认为*.*
 * uploadUrl {URL} 文件上传的url
 * 
 */
com.doubeye.FileUploaderFive = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.FileUploaderFive',
	rootElementTypeName : '<input>',
	fileTypeExts : '*.*',
	init : function() {
		com.doubeye.FileUploaderFive.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.FileUploaderFive.superclass.render.call(this);
		this.rootComponent.attr('type', 'file');
		jQuery(this.rootComponent).uploadifive({
			fileTypeExts : this.fileTypeExts,
			uploader : this.uploadUrl,
		});
		return this;
	}
});
