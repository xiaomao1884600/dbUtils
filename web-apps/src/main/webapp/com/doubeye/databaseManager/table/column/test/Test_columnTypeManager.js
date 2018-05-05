jQuery(document).ready(function() {
	test_ColumnTypeManager();
});

test_ColumnTypeManager = function() {
	jQuery('<input>', {
		value : 'test_ColumnTypeManager',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.ColumnTypeManager({
			databaseName : com.doubeye.ColumnTypeManager.database.ORACLE
		});
		c.getAllSupportedDataType();
	}).appendTo('#container').trigger('click');
};