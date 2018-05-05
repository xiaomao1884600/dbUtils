jQuery(document).ready(function() {
	Test_Image();
});

Test_Image = function() {
	var image;
	jQuery('<input>', {
		value : 'Test_Image',
		type : 'button'
	}).click(function() {
		combo = new com.doubeye.Image({
			parent : 'container',
			value : '/redeemer/uploadTemp/6910ab7bjw1dwejd41ukjj_e9f6f73a-a23d-410b-9670-d29a0c8b5cc3_thumb.jpg',
			text : '无法显示'
		}).init().render();
	}).appendTo('#container').trigger('click');
};