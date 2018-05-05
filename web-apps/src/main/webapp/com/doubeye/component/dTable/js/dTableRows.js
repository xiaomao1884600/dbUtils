/**
 * dTable中显示记录的tr对象列表
 * config
 * 
 * event afterCurrentRowChanged : function(com.doubeye.dTableRow newRow) 当前行改变后触发的事件
 * 
 * @author doubeye
 * @version 1.0.0 
 */
com.doubeye.dTableRows = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableRows',
	rows : null,
	/**
	 * com.doubeye.dTableRow 当前行编辑的行 
	 */	
	currentRow : null,
	__currentComponent : null,
	init : function() {
		this.rootElementTypeName = '<tbody>';
		this.rows = [];
		com.doubeye.dTableRows.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.dTableRows.superclass.render.call(this);
		return this;
	},
	/**
	 * 加入一行
     * @param {com.doubeye.dTableRow} row 行对象
	 */
	addRow : function(row){
		this.rows.push(row);
		this.addRowStyle();
		//this.setCurrentRow(row);
	},
	addRowStyle : function(){
		var thiz = this;
		this.rootComponent.children('tr').removeClass(this.classThemePrefix + 'rowOdd');
		this.rootComponent.children('tr:odd').addClass(this.classThemePrefix + 'rowOdd');
		this.rootComponent.children('tr').hover(function(){jQuery(this).addClass(thiz.classThemePrefix + 'rowHover')}, 
			function(){jQuery(this).removeClass(thiz.classThemePrefix + 'rowHover')});
		this.rootComponent.find('td').hover(function(){jQuery(this).addClass(thiz.classThemePrefix + 'tdHover')}, 
			function(){jQuery(this).removeClass(thiz.classThemePrefix + 'tdHover')});
	},
	/**
	 * 改变行的选择状态，如果不指定select，则函数为选择行
	 * @param {number} rowIndex 行号
	 * @param {boolean} select true为选择改行， false为取消改行的选择，如果不指定，则默认为true
	 */
	selectRow : function(rowIndex, select) {
		if (select != false) {
			select = true;
		}
		if (select) {
			this.rows[rowIndex].rootComponent.addClass(this.classThemePrefix + 'rowSelected');
		} else {
			this.rows[rowIndex].rootComponent.removeClass(this.classThemePrefix + 'rowSelected');
		}
	},
	/**
	 * 获得所有选中的行 
	 */
	getSelectedRows : function() {
		var result = [];
		for (var i = 0; i < this.rows.length; i ++) {
			if (this.rows[i].rootComponent.hasClass(this.classThemePrefix + 'rowSelected')) {
				result.push(this.rows[i]);
			}
		}
		return result;
	},
	/**
	 * 去掉所有行的选中状态 
	 */
	clearRowSelection : function() {
		for (var i = 0; i < this.rows.length; i ++) {
			this.rows[i].rootComponent.removeClass(this.classThemePrefix + 'rowSelected');
		}
	},
	/**
	* 获得指定类型的列
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Record>} 所有指定的列
	*/	
	getSpecifiedStatusRows : function(status) {
		var result = [], record;
		for (var i = 0; i < this.rows.length; i ++) {
			record = this.rows[i].getRecord();
			if (record.status == status) {
				result.push(this.rows[i]);
			}
		}
		return result;
	},
	/**
	 * 将指定的行标记为删除 
	 * @params rows Array<com.doubeye.Record> 要被删除的行的数组
	 */	
	markRowsDeleted : function(rows) {
		var rowIndex, data, row, rowcord;
		for (var i = 0; i < rows.length; i ++) {
			row = rows[i];
			row.markDelete();
		}
	},
	/**
	 * 将指定的行删除 
	 * @params rows Array<com.doubeye.Record> 要被删除的行的数组 
	 */
	deleteRows : function(rows) {
		var record, index;
		for (var i = this.rows.length - 1; i >= 0; i --) {
			trObj = this.rows[i].rootComponent;
			record = this.rows[i].getRecord();
			index = record.rowIndex;
			trObj.detach();
			this.rows.splice(index, 1);
		}
		this.addRowStyle();
	},
	/**
	 * 将指定的行删除 
	 * @params index {int} 要被删除的行的行号 
	 */	
	deleteRow : function(index) {
		var row = this.rows[index], trObj = row.rootComponent;
		trObj.detach();
		this.rows.splice(index, 1);
		this.addRowStyle();
	},
	/**
	 * 获得行对象的数量 
	 */
	getSize : function() {
		return this.rows.length;
	},
	/**
	 * 获得指定的行
 	 * @param {int} index 指定的行号
	 */
	getRow : function(index) {
		return this.rows[index];
	},
	/**
	 * 将当前行改为指定行 
	 * @param row {com.doubeye.dTableRow} 将成为当前行的行对象
	 */
	setCurrentRow : function(row) {
		if (this.currentRow) {
			this.currentRow.rootComponent.find('td').removeClass(this.classThemePrefix + 'current');
		}
		row.rootComponent.find('td').addClass(this.classThemePrefix + 'current');
		this.currentRow = row;
	},
	getValue : function() {
		var result = [];
		for (var i = 0; i < this.rows.length; i ++) {
			result.push(this.rows[i].getValue());
		}
		return result;
	},
	clear : function() {
		this.rows = [];
	}
});