/**
 *  
 * jQuery Table对象
 //TODO 暂时没有计划做这个类
 * config
 * columnDefines {com.doubeye.dTableColumnDefines} 列定义信息，具体配置情况参见com.doubeye.dTableColumnDefines
 * data {JSON} 表格中的数据，其中字段名与columnDefines中的dataId属性对应
 * dataRootElementName : 数据结果中的数据节点属性，默认为'',表示整个data都是结果集
 * dataURL {URL} 数据获取的url
 * dataParams {Object} 获取数据的参数
 * autoLoad {boolean} 如果autoLoad为true,且dataURL不为空，则在表格绘制好后自动载入数据
 * 
 * event
 * beforeRenderDataRow {function(row, record, resultSet, rowIndex)} 在绘制数据的一行之前触发的回调函数，
 * 	参数为：
 *   row {com.doubeye.Row} row对象，其中row.rootComponent为jQuery<tr>对象
 *   record {com.doubeye.Record} 数据对象
 *   resultSet com.doubeye.RecordList 数据集
 *   rowIndex {int} 结果集的行数，兼容用，可以通过record.rowIndex获得
 * 
 * beforeRowChange : function(com.doubeye.Row currentRow) : 在行改变以前的回调函数，如果函数返回false，则不改变当前行
 * beforeRowAdded : function(com.doubeye.Row currentRow) : 在增加一行之前执行的回调函数，如果返回fasle，则新行不被增加 
 * afterRowAdded  : function(com.doubeye.Row addedRow) : 在增加一行之后触发事件
 * afterCurrentRowChanged : function(com.doubeye.dTableRow newRow) 当前行改变后触发的事件
 * @author doubeye
 * @version 1.0.1 
 * @history
 * 1.0.1
 * 	! 修改表格数据的存放方式，将数据从Array<Object>改为com.doubeye.RecordList
 *  + 加入对数据的部分操作
 */
com.doubeye.TableView = com.doubeye.Ext.extend(com.doubeye.dTable, {
	/**
	 * 列定义对象
	 */
	columnDefinesObj : null,
	tbodyEl : null,
	theadEl : null,
	tfootEl : null,
	dataRootElementName : '',
	/**
	 * 由各种修饰器或客户端程序设置的额外的获取数据的参数，在getData是将这些值传到服务端
	 */
	__additionParams : null,
	/**
	 *com.doubeye.dTableRows 保存所有数据tr引用的数组 
	 */
	dataRows : null,
	/**
	 * 表格的数据 
	 */
	data : null,
	recordList : null,
	init : function() {
		this.dataParams = {};
		this.__additionParams = {};
		this.data = [];
		com.doubeye.dTable.superclass.init.call(this);
		this.rootElementTypeName = '<table class="grid" cellspacing="0" cellpadding="0" border="0">';
		this.rootElementStyle = 'border-collapse:collapse;outline:0 none;';
		this.recordList = new com.doubeye.RecordList();
		return this;
	},
	render : function() {
		com.doubeye.dTable.superclass.render.call(this);
		this.theadEl = jQuery('<thead>').appendTo(this.rootComponent);
		this.tfootEl = jQuery('<tfoot>').appendTo(this.rootComponent);
		this.__renderThs();
		this.dataRows = new com.doubeye.dTableRows({
			parent : this.rootComponent
		}).init().render();		
		this.tbodyEl = this.dataRows.rootComponent;
		if ((this.autoLoad != true && !this.dataURL) || (this.data && com.doubeye.Ext.isArray(this.data) && (this.data.length > 0))) {
			this.__getRecordsFormDataArray(this.data);
			this.renderData(this.recordList);
		} else if (this.dataURL){
			this.getData();
		}
		return this;
	},
	/**
	 * 将data数组转换为Record数组 
	 * @params datas (Array<Object>) 记录数组
	 * @defaultStatus {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} 记录的状态，默认为NORMAL
	 */
	__getRecordsFormDataArray : function(datas, defaultStatus) {
		defaultStatus = defaultStatus ? defaultStatus : com.doubeye.constant.DATA.RECORDSTATUS.NORMAL;
		if (datas && com.doubeye.Ext.isArray(datas)) {
			var record;
			for (var i = 0; i < datas.length; i ++) {
				record = new com.doubeye.Record({
					data : datas[i],
					status : defaultStatus
				});
				this.recordList.addRecord(record);
			}
		}
	},
	/**
	 * 获取数据 
	 */
	getData : function() {
		var params = com.doubeye.Ext.apply(this.dataParams, this.__additionParams);
		new com.doubeye.Ajax({
			url : this.dataURL,
			params : params,
			processResult : this.__processData,
			processCaller : this
		}).sendRequest();
	},
	__processData : function(datas) {
		if (this.dataRootElementName) {
			datas = datas[this.dataRootElementName];
		}
		this.__getRecordsFormDataArray(datas, com.doubeye.constant.DATA.RECORDSTATUS.NORMAL);
		this.renderData();
	},
	/**
	 * 获得向后台查询参数的函数，多为plugins设置参数 
	 */
	__getQueryParams : function() {
		return {};
	},
	/**
	 * 绘制列头 
	 */
	__renderThs : function() {
		var config = com.doubeye.Ext.apply({columnDefines : this.columnDefines}, {parent : this.theadEl});
		this.columnDefinesObj = new com.doubeye.dTableColumnDefines(config);
		this.columnDefinesObj.init().render();
		this.__resizeable(this.theadEl);
	},
	/** 
	 * 表头resize事件
	 */
	__resizeable:function(element){
		element.mousemove(function(e){
            var tdDom = e.target, tdEl = jQuery(tdDom), me = this;
            if(!me.__draging){//由于drag事件和mousemove事件会有冲突，所以通过标志位判断
				if(tdEl.hasClass('resizable-th')){
	            	if (tdDom) {
		                var pos = tdEl.position(), 
		                    x = pos.left, 
		                    w = tdEl.width(), 
		                    ex = e.pageX;
		                if(x + w - 13 < ex) {
		                	//如果在右边，则resize自己
		                	me.resizableOriginalWidth = w; //记录原始宽度
		                	me.resizableOriginalEl = tdEl
		                    me.style.cursor = 'col-resize';
		                    return;
		                }
		            }
	            }
	            me.resizableOriginalWidth = -1;
	            me.resizableOriginalEl = null;
	            me.style.cursor = 'auto';
			}
		}).mouseout(function(e) {
			var me = this;
			if(!me.__draging){
				me.resizableOriginalWidth = -1;
				me.resizableOriginalEl = null;
	            this.style.cursor = 'auto';
			}
        }).draggable({
        	axis: 'x',
			opacity: 0.0001, //设置透明度是为了将clone的可拖动组件隐藏
			helper: 'clone',
			start : function(e) {
				this.__initX = e.pageX;
				this.__draging=true;
			},
			drag : function(e){
				var me = this,
					width = me.resizableOriginalWidth + e.pageX - me.__initX;

				if(me.resizableOriginalEl){
					me.resizableOriginalEl.width(width);
				}
			},
			stop : function(e) {
				var me = this;
				me.__initX = 0;
				me.resizableOriginalWidth = 0;
				me.resizableOriginalEl = null;
				me.__draging=false;
			}
		});
	},	
	__renderTbody : function() {
		this.tbodyEl = jQuery('<tbody>').appendTo(this.rootComponent);
	},
	/**
	 * 去掉表格中已经渲染的数据 
	 * @params {boolean} keepData 是否保留数据，默认为false
	 */
	clear : function(keepData) {
		if (keepData === true) {
			
		} else {
			this.data = [];
			this.recordList.clear();
			this.dataRows.clear();
		}
		this.tbodyEl.empty();
		return this;
	},
	/**
	 * 绘制表格数据
	 * @param {Array<Object>} data 数据
	*/
	renderData : function(datas) {
		var thiz = this, record;
		var columnDefines = this.columnDefinesObj.getDataColumnDefines(), row;
		for (var i = 0; i < this.recordList.getSize(); i ++) {
			record = this.recordList.get(i);
			row = new com.doubeye.dTableRow({
				table : this.getTable(),
				parent : this.tbodyEl,
				columnDefines : columnDefines,
				record : record,
				resultSet : this.recordList
			}).init().render();
			this.dataRows.addRow(row);
		}
	},
	/**
	 * 改变行的选择状态，如果不指定select，则函数为选择行
	 * @param {number} rowIndex 行号
	 * @param {boolean} select true为选择改行， false为取消改行的选择，如果不指定，则默认为true
	 */
	selectRow : function(rowIndex, select) {
		this.dataRows.selectRow(rowIndex, select);
	},
	/**
	 * 获得所有选中的行 
	 */
	getSelectedRows : function() {
		return this.dataRows.getSelectedRows();
	}, 
	/**
	 * 去掉所有行的选中状态 
	 */
	clearRowSelection : function() {
		this.dataRows.clearRowSelection();
	},
	/**
	 * 删除所选行 
	 * @params rows {Object} 要删除的行对象（目前为trObj）
	 */
	deleteRows : function(rows) {
		
	},
	getTable : function() {
		var table = this;
		while (table.table) {
			table = table.table;
		}
		return table;
	},
	/**
	 * 获得标记为删除的行
	 * @return {Array<com.doubeye.Row>} 所有标记为删除的行
	 */
	getRowsMarkDeleted : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.MARKDELETED);
	},
	/**
	 * 获得新增的行
	 * @return {Array<com.doubeye.Row>} 所有新增的行
	 */
	getRowsAdded : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.ADDED);
	},
	/**
	 * 获得新增后被标记为删除的行 
	 * @return {Array<com.doubeye.Record>} 所有新增后背被删除的行
	 */
	getRowsAddedAndDeleted : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.NEW_AND_DELETED);
	},
	/**
	 * 获得修改过的行
	 * @return {Array<com.doubeye.Row>} 所有修改过的行
	 */
	getRowsModified : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED);
	},
	/**
	 * 获得修改后被删除的行
	 * @return {Array<com.doubeye.Row>} 所有修改后被删除的行
	 */	
	getRowsModifiedAndDeleted : function() {
		return this.__getSpecifiedStatusRows(com.doubeye.constant.DATA.RECORDSTATUS.MODIFIED_AND_DELETE);
	},
	/**
	* 获得指定类型的行
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Row>} 所有指定的行
	*/
	__getSpecifiedStatusRows : function(status) {
		return this.dataRows.getSpecifiedStatusRows(status);
	},
	/**
	 * 得到正在编辑的row,也就是当前获得焦点的row 
	 */
	getCurrentRow : function(){
		return this.getTable().dataRows.currentRow;
	},
	/**
	 * 获得表格的行数 
	 */
	getRecordCount : function(){
		return this.getTable().dataRows.getSize();
	},
	/**
	 * 获得所有行 
	 */
	getAllRows : function() {
		return this.getTable().dataRows.rows;
	},
	/**
	 * 获得指定行  
 	 * @param {Object} index
	 */
	getRow : function(index) {
		var table = this.getTable();
		if (index >= table.dataRows.getSize()) {
			return null;
		} else {
			return table.dataRows.getRow(index);
		}
	},
	getValue : function() {
		var result = [], rows = this.getAllRows();
		for (var i = 0; i < rows.length; i ++) {
			result.push(rows[i].getValue());
		}
		return result;
	},
		/**
	 * 获得列定义中的主键字段，如果没有定义主键字段（即在列定义中，没有keyColumn == true的字段），则返回[]
	 * @return {Array[com.doubeye.dTableColumnDefile]} 主键字段数组 
	 */
	getKeyColumns : function() {
		return this.getTable().columnDefinesObj.getKeyColumns();
	},
	refresh : function() {
		this.clear();
		this.getData();
	}
});
