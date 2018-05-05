jQuery(document).ready(function() {
	test_ConnectionManager();
});

test_ConnectionManager = function() {
	jQuery('<input>', {
		value : 'test_ConnectionManager',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.ConnectionManager({
			parent : 'container'
		}).init().render();
	}).appendTo('#container').trigger('click');
};
