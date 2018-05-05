jQuery(document).ready(function() {
	test_TableManager();
});

test_TableManager = function() {
	jQuery('<input>', {
		value : 'test_TableManager',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.TableManager({
			parent : 'container'
		}).init().render();
		//test_tableStatus(c);
	}).appendTo('#container').trigger('click');
};
//此方法测试状态改变的样式
var test_tableStatus = function(c) {
	alert();
	c.__setTableStatus(com.doubeye.TableManager.TABLE_STATUS.CREATED);
	alert();
	c.__setTableStatus(com.doubeye.TableManager.TABLE_STATUS.META_MANAGED);
	alert();
	c.__setTableStatus(com.doubeye.TableManager.TABLE_STATUS.UNKNOWN);
	alert();
	c.__setTableStatus('dfasdfs');
	alert();
	c.__setTableStatus(com.doubeye.TableManager.TABLE_STATUS.OUT_OF_DATE);
}