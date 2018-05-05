jQuery(document).ready(function() {
	Test_ToolBar();
});

Test_ToolBar = function() {
	var panel;
	jQuery('<input>', {
		value : 'Test_ToolBar',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container'
		});
		toolbar = new com.doubeye.ToolBar(config).init().render();
		var button = new com.doubeye.Button({
			parent : 'container',
			text : '我是按钮',
			action : function() {
				alert('woshianniu')
			}
		}).init().render();
		toolbar.addItem(button);
		button = new com.doubeye.Button({
			parent : 'container',
			text : '我是按钮',
			action : function() {
				alert('woshianniu')
			}
		}).init().render();
		toolbar.addItem(button);
	}).appendTo('#container').trigger('click');
};