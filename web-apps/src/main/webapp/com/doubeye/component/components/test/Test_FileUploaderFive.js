jQuery(document).ready(function() {
	Test_UploadFileFile();
});

Test_UploadFileFile = function() {
	jQuery('<input>', {
		value : 'Test_UploadFileFile',
		type : 'button'
	}).click(function() {
		var cmp = new com.doubeye.FileUploaderFive({
				parent : 'container',
				uploadUrl : '',
				fileTypeExts : '*.jpg,*.bmp,*.png'
			}).init().render();
	}).appendTo('#container').trigger('click');
};