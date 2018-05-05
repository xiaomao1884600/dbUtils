jQuery(document).ready(function() {
	test_SystemParametersEditor();
});

test_SystemParametersEditor = function() {
	jQuery('<input>', {
		value : 'test_SystemParametersEditor',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.SystemParametersEditor({
			parent : 'container'
		}).init().render();
	}).appendTo('#container').trigger('click');
};
