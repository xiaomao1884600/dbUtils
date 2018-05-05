jQuery(document).ready(function() {
	test_utils_viewer();
});

test_utils_viewer = function() {
		jQuery('<input>', {
		value : 'test_utils_viewer',
		type : 'button'
	}).click(function() {
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.viewer.getColumnDefineByViewerId(5)));
	}).appendTo('#container').trigger('click');
};