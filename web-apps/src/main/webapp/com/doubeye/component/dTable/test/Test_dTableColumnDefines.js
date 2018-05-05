jQuery(document).ready(function() {
	var columns = testMultiLevelColumnDefines();
	var columnDefines = [];
	columns.getDeepestColumnDefines(columns.columnDefines, columnDefines);
	//testSimpeColumnDefines();
});
var testSimpeColumnDefines = function() {
	var column;
	jQuery('<input>', {
		value : 'Test_dTableColumnDefines',
		type : 'button'
	}).click(function() {
		var table = jQuery('<table>').appendTo('#container');
		column = new com.doubeye.dTableColumnDefines({
			parent : table,
			columnDefines : [{
				columnName : 'ROWNUM',
				dataId : 'ROWNUM',
				label : '序号'
			}, {
				columnName : 'TITLE',
				dataId : 'TITLE',
				label : '标题'
			}, {
				columnName : 'DEPARTMENT_DESC',
				dataId : 'DEPARTMENT_DESC',
				label : '主报单位'
			}, {
				label : 'a',
				dataId : 'a'
			}, {
				label : 'b',
				dataId : 'b'
			}, {
				columnName : 'LDPS',
				dataId : 'LDPS',
				label : '领导批示'
			}]
		});
		try {
			column.init().render();
		} catch(e) {
			alert(e);
		}
	}).appendTo('#container').trigger('click');
	return column;
};
var testMultiLevelColumnDefines = function() {
	var column;
	jQuery('<input>', {
		value : 'Test_dTableColumnDefines',
		type : 'button'
	}).click(function() {
		var table = jQuery('<table>').appendTo('#container');
		column = new com.doubeye.dTableColumnDefines({
			parent : table,
			columnDefines : [{
				columnName : 'ROWNUM',
				dataId : 'ROWNUM',
				label : '序号'
			}, {
				columnName : 'TITLE',
				dataId : 'TITLE',
				label : '标题'
			}, {
				columnName : 'DEPARTMENT_DESC',
				dataId : 'DEPARTMENT_DESC',
				label : '主报单位'
			}, {
				label : 'a',
				children : [{
					columnName : 'SCY',
					dataId : 'SCY',
					label : 'a1',
					toCustomString : function(text) {
						if (text == '1') {
							return '√';
						} else {
							return null;
						}
					}
				}, {
					columnName : 'BCY',
					dataId : 'BCY',
					label : 'a2',
					children : [{
						columnName : 'aa1'
					}, {
						columnName : 'aa2'
					}, {
						columnName : 'aa3'
					}]
				}, {
					columnName : 'ZBCY',
					dataId : 'ZBCY',
					label : 'a3',
					toCustomString : function(text) {
						if (text == '1') {
							return '√';
						} else {
							return null;
						}
					}
				}]
			}, {
				label : 'b',
				children : [{
					label : 'b1',
					children : [{
						columnName : 'BCY',
						dataId : 'BCY',
						label : 'b11'
					}, {
						columnName : 'BCY',
						dataId : 'BCY',
						label : 'b12'
					}]
				}, {
					columnName : 'BCY',
					dataId : 'BCY',
					label : 'b2'
				}]
			}, {
				columnName : 'LDPS',
				dataId : 'LDPS',
				label : '领导批示'
			}]
		});
		try {
			column.init().render();
		} catch(e) {
			alert(e);
		}
	}).appendTo('#container').trigger('click');
	return column;
}; 