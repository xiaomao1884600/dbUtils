jQuery(document).ready(function() {
	Test_ImageExtend();
});

Test_ImageExtend = function() {
	var image;
	jQuery('<input>', {
		value : 'Test_ImageExtend',
		type : 'button'
	}).click(function() {
		combo = new com.doubeye.ImageExtend({
			parent : 'container',
			value : {
				thumbUrl : '/redeemer/uploadTemp/6910ab7bjw1dwejd41ukjj_e9f6f73a-a23d-410b-9670-d29a0c8b5cc3_thumb.jpg',
				imgUrl : '/redeemer/uploadTemp/6910ab7bjw1dwejd41ukjj_6e30da1d-978b-48d6-96b0-251faf51cbf6.jpg',
				text : '无法显示'
			}
		}).init().render();
	}).appendTo('#container').trigger('click');
};