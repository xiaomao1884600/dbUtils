jQuery(document).ready(function() {
	Test_UploadImage();
});

Test_UploadImage = function() {
	jQuery('<input>', {
		value : 'Test_UploadImage',
		type : 'button'
	}).click(function() {
		var cmp = new com.doubeye.ImageUploader({
				parent : 'container',
				uploadUrl : '/FileUpload?action=uploadImage&toThumb=true',
				filePropertyNames : ['fileName', 'thumbFileName'],
				thumbFilePropertyName : 'thumbFileName',
 				originFilePropertyName : 'fileName'
			}).init().render();
	}).appendTo('#container').trigger('click');
};