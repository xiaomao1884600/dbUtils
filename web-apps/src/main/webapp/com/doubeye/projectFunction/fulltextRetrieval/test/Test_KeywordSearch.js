jQuery(document).ready(function() {
	test_KeywordSearch();
});

test_KeywordSearch = function() {
	jQuery('<input>', {
		value : 'test_KeywordSearch',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.KeywordSearch({
			parent : 'container',
			keyword : '数据'
		}).init().render();
	}).appendTo('#container').trigger('click');
};
