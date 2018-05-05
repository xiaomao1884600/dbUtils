jQuery(document).ready(function() {
	test_dTableOrder_render();
});

var test_dTableOrder_render = function() {
	jQuery('<input>', {
		value : 'test_dTable_renderThs',
		type : 'button'
	}).click(function() {
		var c = new com.doubeye.dTableOrder({
			parent : 'container'
		}).init().render();
		c.setValue(com.doubeye.constant.DB.ORDER_BY.DESC);
	}).appendTo('#container').trigger('click');
};
