jQuery(document).ready(function() {
	test_baidu();
});

test_baidu = function() {
		jQuery('<input>', {
		value : 'test_baidu',
		type : 'button'
	}).click(function() {
		new com.doubeye.BaiduSearch({
			parent : 'container',
			showLogo : true,
			value : 'fjsodjs'
		}).init().render();
	}).appendTo('#container').trigger('click');
};