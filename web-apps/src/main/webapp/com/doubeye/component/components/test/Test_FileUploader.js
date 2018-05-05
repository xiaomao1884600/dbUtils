jQuery(document).ready(function() {
	Test_UploadFileFile();
});

Test_UploadFileFile = function() {
	jQuery('<input>', {
		value : 'Test_UploadFileFile',
		type : 'button'
	}).click(function() {
		var cmp = new com.doubeye.FileUploader({
				parent : 'container',
				uploadUrl : '/FileUpload?action=uploadImage&toThumb=true',
				fileTypeExts : '*.jpg; *.png; *.gif',
				fileTypeDesc : '图像文件',
				filePropertyNames : ['fileName', 'thumbFileName']
			}).init().render();
	}).appendTo('#container').trigger('click');
};