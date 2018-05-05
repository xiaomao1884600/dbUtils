jQuery(document).ready(function() {
	test_columnOrderSetup_dialog();
});

var test_columnOrderSetup_dialog = function() {
	jQuery('<input>', {
		value : 'test_columnOrderSetup_dialog',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.dTableColumnOrderSetup({
			parent : '@@onlyDialog@@'
		}).init().render().openDialog();
	}).appendTo('#container').trigger('click');
};