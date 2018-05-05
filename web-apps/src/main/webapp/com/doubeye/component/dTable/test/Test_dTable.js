jQuery(document).ready(function() {
	test_dTable_renderThs();
	//test_dTable_getDataFromServer();
});

var test_dTable_renderThs = function() {
	jQuery('<input>', {
		value : 'test_dTable_renderThs',
		type : 'button'
	}).click(function() {
		var column = {
			parent : table,
			columnDefines : [{
				columnName : 'ROWNUM',
				dataId : 'ROWNUM',
				label : '序号'
				
				,
				renderValue : function(rowObj, td, value, record, records, index) {
					var label = jQuery('<label>').appendTo(td);
					label.html('value:' + value);
					if (index % 2 == 0) {
						label.css('color', 'red');
					}
				}
				
			}, {
				columnName : 'TITLE',
				dataId : 'TITLE',
				label : '标题',
				component : 'com.doubeye.Label'
			}, {
				columnName : 'DEPARTMENT_DESC',
				dataId : 'DEPARTMENT_DESC',
				label : '主报单位'
			}, {
				label : 'a',
				children : [{
					columnName : 'SCY',
					dataId : 'a1',
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
					dataId : 'a2',
					label : 'a2',
					children : [{
						columnName : 'aa1',
						dataId : 'aa1'
					}, {
						columnName : 'aa2',
						dataId : 'aa2'
					}, {
						columnName : 'aa3',
						dataId : 'aa3'
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
						dataId : 'b11',
						label : 'b11'
					}, {
						columnName : 'BCY',
						dataId : 'b12',
						label : 'b12'
					}]
				}, {
					columnName : 'BCY',
					dataId : 'b2',
					label : 'b2'
				}]
			}, {
				columnName : 'LDPS',
				dataId : 'LDPS',
				label : '领导批示'
			}]
		};

		var data = [{ROWNUM : 1, TITLE : '第一条记录', DEPARTMENT_DESC : '一处', A1 : 'jfoaidjfioahgodfhoaf', aa1 : '第一条记录aa1', aa2 : '第一条记录aa2', aa3 : '第一条记录aa3', ZBCY : '第一条主报处室', b11 : '1b11', b12 : '1b12', b2 : '1b2', LDPS : '的附件傲娇地哦啊哈佛好的 '},
		{ROWNUM : 2, TITLE : '第二条记录', DEPARTMENT_DESC : '二处', A1 : 'djfioahjiofhgaiojfo', aa1 : '第二条记录aa1', aa2 : '第二条记录aa2', aa3 : '第二条记录aa3', ZBCY : '第二条主报处室', b11 : '2b11', b12 : '2b12', b2 : '2b2', LDPS : '的卷发哦哈奇偶发觉偶就'}];
		/*
		var data = [];
		for (var i = 0; i < 400; i ++) {
			data.push({ROWNUM : 1, TITLE : '第一条记录', DEPARTMENT_DESC : '一处', A1 : 'jfoaidjfioahgodfhoaf', aa1 : '第一条记录aa1', aa2 : '第一条记录aa2', aa3 : '第一条记录aa3', ZBCY : '第一条主报处室', b11 : '1b11', b12 : '1b12', b2 : '1b2', LDPS : '的附件傲娇地哦啊哈佛好的 '})
		}
		*/
		var config = {
			parent : 'container',
			columnDefines : column.columnDefines,
			data : data
		};
        var t1 = new Date().getTime();
		var table = com.doubeye.dTableFacory.getTable(config, {
			rowCheckbox : {position : com.doubeye.constant.POSITION.BOTH_HORIZONTAL},
			order : {
				multiColumn : true,
				orderConfigs : [{dataId : 'ROWNUM', order : com.doubeye.constant.DB.ORDER_BY.DESC}]
			},
			'export' : {
				local : true
			}
		});

		//try {
			table.init().render();
			//table.renderData();
		//} catch(e) {
		//	alert(e);
		//}
        var t2 = new Date().getTime();
        console.log(t2 - t1)
	}).appendTo('#container').trigger('click');
};

test_dTable_getDataFromServer = function() {
	var table;
	jQuery('<input>', {
		value : 'test_dTable_renderThs',
		type : 'button'
	}).click(function() {
		var column = [{
			dataId : 'start_time',
				label : '查询时间',
		}, {
			dataId : 'query_time',
			label : '持续时间',
			width : 100
		}, {
			dataId : 'lock_time',
			label : '锁时间'
		}, {
			dataId : 'rows_sent',
			label : '返回行数'
		}, {
			dataId : 'rows_examined',
			label : '扫描行数'
		}, {
			dataId : 'last_insert_id',
			label : 'last_insert_id'
		}, {
			dataId : 'server_id',
			label : 'server_id'
		}, {
			dataId : 'insert_id',
			label : 'insert_id'
		}, {
			dataId : 'thread_id',
			label : 'thread_id'
		}, {
			dataId : 'sql_text',
			label : '语句'
		}];
		var config = {
			dataURL : '/generalRouter?objectName=com.hxsd.services.productLine.e.SlowLogService&action=getSlowLogs',
			parent : 'container',
			columnDefines : column
		};
		table = com.doubeye.dTableFacory.getTable(config, {
			rowCheckbox : {position : com.doubeye.constant.POSITION.BOTH_HORIZONTAL},
			order : {
				multiColumn : true,
				orderType : 'server',
				orderConfigs : [{dataId : 'start_time', order : com.doubeye.constant.DB.ORDER_BY.DESC}]
			},
			page : {
				position : 'both',
				//currentPage : 2,
				recordPerPage : 35
			}
			/*,
			tableSearch : true,
			condition : {conditions : [{dataId : 'ID', value : 1}, {dataId : 'NAME', value : 'SQLServer2000AndBeforePooledJDBC'}]},

			'export' : {
				server : true
			},

			edit : true
			 */
		});
		
		//try {
			table.init().render();
			//table.renderData();
		//} catch(e) {
		//	alert(e);
		//}
		/** 捎带测试组件内查找功能，该组件已可以直接用dTable的plugin指定
		var search = new com.doubeye.ComponentSearch({
			component : table.getTable(),
			parent : 'container',
			selector : 'td'
		}).init().render();
		*/
		
		
		
		
		/**
		 * 测试IETableToExcel 
		 
		jQuery('<input>', {
			type : 'button',
			value : 'IETableToExcel'
		}).click(function(){
			var exporter = new com.doubeye.IETableToExcel({
				table : table.getTable().rootComponent,
				//renderStyle : true,
				valueSelector : '.cellValue'
			});
			exporter.getExcelFile();
		}).appendTo('#container');
		*/
	}).appendTo('#container').trigger('click');	
	jQuery('<input>', {
		value : 'dTable.getSelectedRows()',
		type : 'button'
	}).click(function() {
		table.getSelectedRows();
	}).appendTo('#container');
	jQuery('<input>', {
		value : 'dTable.clearRowSelection()',
		type : 'button'
	}).click(function() {
		table.clearRowSelection();
	}).appendTo('#container');
}
