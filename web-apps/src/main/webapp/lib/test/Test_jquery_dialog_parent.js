jQuery(document).ready(function() {
	test_jQuery_dialog_parent();
});

var test_jQuery_dialog_parent = function() {
	jQuery('<input>', {
		value : 'test_dTable_renderThs',
		type : 'button'
	}).click(function() {
		jQuery('<div>', {
			html : 'sfoajojsf'
		}).dialog();
	}).appendTo('#container').trigger('click');
}