com.doubeye.databaseManager.table.column.Column = com.doubeye.Ext.extend(com.doubeye.Component, {
	/**
	 * column所属于的表格对象
	 */
	table : null,
	/**
	 * 列的名称
	 */
	columnName : '',
	/**
	 * 类类型对象
	 */
	columnType : null,
	/**
	 * 长度 
	 */
	length : 0,
	/**
	 * 精度 
	 */
	scale : 0
});