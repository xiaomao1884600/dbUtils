jQuery(document).ready(function() {
	test_ConnectionTree();
});

test_ConnectionTree = function() {
	jQuery('<input>', {
		value : 'test_ConnectionTree',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.ConnectionTree({
			parent : 'container'
		}).init().render();
	}).appendTo('#container').trigger('click');
};
