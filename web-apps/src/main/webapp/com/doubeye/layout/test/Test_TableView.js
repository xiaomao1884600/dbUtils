jQuery(document).ready(function() {
	test_TableView();
});

test_TableView = function() {
		jQuery('<input>', {
		value : 'test_TableView',
		type : 'button'
	}).click(function() {
		new com.doubeye.TableView({
			parent : 'container',
			viewId : 2,
			editorViewId : 3,
			actions : ['add', 'modify', 'delete', 'refresh']
		}).init().render();
	}).appendTo('#container').trigger('click');
};