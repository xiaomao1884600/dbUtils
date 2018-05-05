jQuery(document).ready(function() {
	test_TableViewerDesigner();
});

test_TableViewerDesigner = function() {
		jQuery('<input>', {
		value : 'test_TableViewerDesigner',
		type : 'button'
	}).click(function() {
		new com.doubeye.TableViewerDesigner({
			parent : 'container',
		}).init().render();
	}).appendTo('#container').trigger('click');
};