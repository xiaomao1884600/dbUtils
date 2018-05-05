jQuery(document).ready(function() {
	test_ColumnTypeCombo();
});

test_ColumnTypeCombo = function() {
	jQuery('<input>', {
		value : 'test_ColumnTypeCombo',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.ColumnTypeManager({
			databaseName : com.doubeye.ColumnTypeManager.database.ORACLE
		});
		c.getAllSupportedDataType();
		var combo = new com.doubeye.ColumnTypeCombo({
			columnTypeManager : c,
			parent : 'container'
		});
		combo.init().render();
	}).appendTo('#container').trigger('click');
};