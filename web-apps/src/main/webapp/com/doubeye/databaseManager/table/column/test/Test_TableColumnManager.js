jQuery(document).ready(function() {
	test_TableColumnManager();
});

test_TableColumnManager = function() {
	var c;
	jQuery('<input>', {
		value : 'test_TableManager',
		type : 'button'
	}).click(function() {
		c = new com.doubeye.TableColumnManager({
			parent : 'container',
			databaseName : com.doubeye.ColumnTypeManager.database.ORACLE
		}).init().render();
		var record = c.__columnDefineTable.addRow().getRecord();
		record.modify('columnName', 'column1');
	}).appendTo('#container').trigger('click');
	jQuery('<input>', {
		value : 'test_TableManager_highlightCurrentRow',
		type : 'button'
	}).click(function() {
		c.__columnDefineTable.getTable().tbodyEl.find('com-doubeye-default-highlight').removeClass('com-doubeye-default-highlight');
		if (c.__columnDefineTable.getTable().getCurrentRow()) {
			c.__columnDefineTable.getTable().getCurrentRow().rootComponent.addClass('com-doubeye-default-highlight');
		}
	}).appendTo('#container').trigger('click');	
	
	jQuery('<input>', {
		value : 'test_TableManager_getColumnValue',
		type : 'button'
	}).click(function() {
		var values = c.getValue();
		alert(com.doubeye.Ext.encode(values));
	}).appendTo('#container');

};