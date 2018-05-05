/**
 * 文件上传组件，由jQuery File Upload Plugin提供实现
 * config
 * fileTypeExts {String} 分号分隔的文件扩展名限制，默认为*.*
 * fileTypeDesc {String} 允许文件的描述，默认为All Files，建议改变fileTypeExts后，根据实际情况改变此属性
 * filePropertyNames {Array<String>} 用来指明服务端返回的文件url的属性名，如果后台会生成多个文件，则应该用不同的文件属性名标明不同的文件，
 *  具体的文件属性名和用途需要服务端与客户端协商，以便能够在服务端正确删除所有生成的文件或记录
 *  默认值为['fileName']
 * uploadUrl {URL} 文件上传的url
 * noAutoDelete {boolean} 当值为true时，在组件中删除一个图片时，不自动删除服务端图片，默认为false，即自动删除图片
 * 
 */
com.doubeye.FileUploader = com.doubeye.Ext.extend(com.doubeye.DataSensor, {
	className : 'com.doubeye.FileUploader',
	uploadEl : null,
	uploadedFileEl : null,
	fileProperyNames : null, 
	fileTypeExts : '*.*',
	init : function() {
		this.filePropertyNames = [];
		com.doubeye.FileUploader.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.FileUploader.superclass.render.call(this);
		var thiz = this;
		this.uploadedFileEl = jQuery('<div>').appendTo(this.rootComponent);
		this.uploadEl = jQuery('<div>', {
			id : this.rootId + "upload"	
		}).appendTo(this.rootComponent);
		this.uploadEl.uploadify({
			fileTypeExts : this.fileTypeExts,
			fileTypeDesc : this.fileTypeDesc,
			uploader : com.doubeye.Utils.toFullPath(this.uploadUrl),
			swf : '../../lib/jQuery/uploadify/uploadify.swf',
			buttonText : '上传',
			auto : true,
			onUploadSuccess : function (fileObj, response, data) { 
			 	thiz.__processResult(fileObj, response, data);
			}
		});
		return this;
	},
	__processResult : function(fileObj, response, data) {
		var result = com.doubeye.Ext.decode(response);
	 	if (result.SUCCESS) {
	 		var value = com.doubeye.Ext.decode(result.RESULTS);
	 		for (var i = 0; i < value.length; i ++) {
	 			this.__renderUploadedFile(fileObj.name, value[i].fileName, value[i]);
	 		}
	 	} else {
	 		alert("上传失败:" + response);
	 	}
	},
	__renderUploadedFile : function(fileName, fileUrl, fileObj) {
		var thiz = this;
		var div = jQuery('<span>').appendTo(this.uploadedFileEl);
		jQuery('<a>' , {
			html : fileName,
			href : fileUrl,
			target : '_blank',
			style : 'padding-left:20px'
		}).appendTo(div);
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
	__doDelete : function(deleteEl, fileObj) {
		if (this.noAutoDelete) {
			var data = deleteEl.data("fileObj");
			var fileNames = [];
			for (var i = 0; i < this.filePropertyNames.length; i ++) {
				fileNames.push(data[this.filePropertyNames[i]]);
			}
			var fileNameString = fileNames.toString();
			new com.doubeye.Ajax({
				url : '/FileUpload?action=deleteFiles',
				showNoSuccessMessage : true,
				noWaitingDialog : true,
				params : {
					fileNames : fileNameString
				},
				processResult : function(datas){
					deleteEl.parent().detach();
				}
			}).sendRequest();
		} else {
			deleteEl.parent().detach();
		}
		
	},
	getValue : function() {
		var result = [];
		this.uploadedFileEl.find('.' + this.classThemePrefix + 'delete').each(function() {
			var obj = jQuery(this);
			var data = obj.data("fileObj");
			result.push(data);
		});
		return result;
	}
});