/**
 * TableView中为每行添加的动作，每个动作对应一个按钮，根据配置将改行的值传递给动作
 * 注意：该类是所有动作的基类，扩展此类的子类必须实现prototype中的方法
 * text {String} 按钮上的文字
 * keyAsValue {boolean} 当此值为false时，将整个record传递给action，当此值为true是，仅将reocrd的key传递给action，默认为false
 * tableView {com.doubeye.TableView} 对tableView的引用，可以用来回调
 * row <com.doubeye.Row> 数据行对象的引用，@see com.doubeye.Row
 */
com.doubeye.Action = function(config) {
	this.config = config;
	this.keyAsValue = config.keyAsValue == true ? true : false;
	this.row = config.row;
	this.tableView = config.tableView;
};

com.doubeye.Action.prototype  = {
	/**
	 * 初始化行为，如有必要请覆盖 
	*/
	init : function() {
		
	},
	/**
	 * 执行动作
	 */
	doAction : function() {
		var value = this.keyAsValue ? this.row.getKeyFieldValue() : this.row.getValue();
		alert("不要直接使用，请使用扩展类，以下内容仅供调试. value :" + com.doubeye.Ext.encode(value));
	}
};