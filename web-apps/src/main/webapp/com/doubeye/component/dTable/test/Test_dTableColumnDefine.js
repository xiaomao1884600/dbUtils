jQuery(document).ready(function(){
	//Test_parent_must_be_table();
	Test_parent_parent_is_table();
});

var Test_parent_must_be_table = function(){
	jQuery('<input>', {
		value : 'Test_parent_must_be_table',
		type : 'button'
	}).click(function(){
		var column = new com.doubeye.dTableColumnDefine({
			parent : '#container'
		});
		try {
			column.init().render();
		} catch(e) {
			alert(e);
		}
	}).appendTo('#container');
};

var Test_parent_parent_is_table = function(){
	jQuery('<input>', {
		value : 'Test_parent_parent_is_table',
		type : 'button'
	}).click(function(){
		var table = jQuery('<table>').appendTo('#container');
		var row = jQuery('<tr>').appendTo(table);
		var column = new com.doubeye.dTableColumnDefine({
			parent : row,
			label : 'sffs'
		});
		column.init().render();
		var column2 = new com.doubeye.dTableColumnDefine({
			parent : row,
			label : 'sffs'
		});
		column2.init().render();
		
		row = jQuery('<tr>').appendTo(table);
		jQuery('<td>', {html : 'row1 td1'}).appendTo(row).addClass('th');
		jQuery('<td>', {html : 'row1 td2'}).appendTo(row).addClass('th');;
		
		row = jQuery('<tr>').appendTo(table);
		jQuery('<td>', {html : 'row2 td1'}).appendTo(row).addClass('th');;
		jQuery('<td>', {html : 'row2 td2 and many'}).appendTo(row).addClass('th');;
	}).appendTo('#container').trigger('click');
};