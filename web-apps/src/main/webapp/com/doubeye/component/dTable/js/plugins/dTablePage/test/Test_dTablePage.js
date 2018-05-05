jQuery(document).ready(function() {
	test_dTablePage_TextRender();
});

test_dTablePage_TextRender = function() {
	jQuery('<input>', {
		value : 'test_ajax_success',
		type : 'button'
	}).click(function() {
		var page = new com.doubeye.dTablePagingTextRender({
			parent : 'container',
			needLastPage : true
		}).init().render();
	}).appendTo('#container').trigger('click');
};