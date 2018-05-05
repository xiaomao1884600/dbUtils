/**
 * 图像上传组件上传组件，由jQuery File Upload Plugin提供实现，继承自com.doubeye.FileUploader，用img显示已经上传成功的文件
 * config
 * filePropertyNames {Array<String>} 用来指明服务端返回的文件url的属性名，如果后台会生成多个文件，则应该用不同的文件属性名标明不同的文件，
 *  具体的文件属性名和用途需要服务端与客户端协商，以便能够在服务端正确删除所有生成的文件或记录
 *  默认值为['fileName']
 * thumbFilePropertyName : {String} 服务端返回的缩略图文件属性名，如果这个属性存在，图片上传成功后会显示缩略图，以节省版面，如果不存在，则使用originFilePropertyName属性
 * originFilePropertyName : {String}  服务端返回的图文件属性名，如果这个属性和thumbFilePropertyName属性存在，则点击缩略图时，会在新标签中显示原图
 * uploadUrl {URL} 文件上传的url
 * noAutoDelete {boolean} 当值为true时，在组件中删除一个图片时，不自动删除服务端图片，默认为false，即自动删除图片
 * 
 */
com.doubeye.ImageUploader = com.doubeye.Ext.extend(com.doubeye.FileUploader, {
	className : 'com.doubeye.ImageUploader',
	uploadEl : null,
	uploadedFileEl : null,
	fileProperyNames : null, 
	fileTypeExts : '*.*',
	init : function() {
		com.doubeye.ImageUploader.superclass.init.call(this);
		return this;
	},
	render : function() {
		this.fileTypeExts = '*.jpg; *.png; *.gif';
		this.fileTypeDesc = '图像文件';
		com.doubeye.ImageUploader.superclass.render.call(this);
		return this;
	},
	__processResult : function(fileObj, response, data) {
		var result = com.doubeye.Ext.decode(response);
	 	if (result.SUCCESS) {
	 		var value = com.doubeye.Ext.decode(result.RESULTS);
	 		for (var i = 0; i < value.length; i ++) {
	 			var thumbFile = this.thumbFilePropertyName ? value[i][this.thumbFilePropertyName] : null;
	 			var originFile = this.originFilePropertyName ? value[i][this.originFilePropertyName] : null;
	 			this.__renderUploadedFile(thumbFile, originFile, value[i]);
	 		}
	 	} else {
	 		alert("上传失败:" + response);
	 	}
	},
	__renderUploadedFile : function(thumbUrl, originUrl, fileObj) {
		var thiz = this;
		var div = jQuery('<span>').appendTo(this.uploadedFileEl);
		var img = jQuery('<img>' , {
			src : thumbUrl ? thumbUrl : originUrl,
			style : 'padding-left:20px'
		}).appendTo(div);
		if (thumbUrl) {
			img.data('originUrl', originUrl);
			img.click(function(){
				var obj = jQuery(this);
				var url = img.data('originUrl');
				window.open(url);
			});
		}
		var deleteEl = jQuery('<span>' , {
			'class' : this.classThemePrefix + 'delete',
			click : function() {
				var obj = jQuery(this);
				var fileObj = obj.data("fileObj");
				thiz.__doDelete(obj, fileObj);
			}
		}).appendTo(div);
		deleteEl.data("fileObj", fileObj);
	},
	setValue : function(value) {
		var valueArray = null;
		if (com.doubeye.Ext.isArray(value)) {
			valueArray = value;
		} else {
			valueArray = com.doubeye.Ext.decode(value);
			if (!com.doubeye.Ext.isArray(valueArray)) {
				return;
			}
		}
		for (var i = 0; i < valueArray.length; i ++) {
			var pic = valueArray[i];
			var thumbFile = this.thumbFilePropertyName ? pic[this.thumbFilePropertyName] : null;
			var originFile = this.originFilePropertyName ? pic[this.originFilePropertyName] : null;
			this.__renderUploadedFile(thumbFile, originFile, pic);
		}
	},
	clearValue : function() {
		this.uploadedFileEl.empty();
	}
});