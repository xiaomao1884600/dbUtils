jQuery(document).ready(function() {
	Test_Panel();
});

Test_Panel = function() {
	var panel;
	jQuery('<input>', {
		value : 'Test_Panel',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			title : '测试',
			layout : 'com.doubeye.ColumnLayout',
			layoutConfig : {
				columnCount : 2
			},
			collapsable : true
		});
		panel = new com.doubeye.Panel(config).init().render();
	
		var text = new com.doubeye.TextEdit({
			parent : 'container',
			value : 1
		}).init().render();
		panel.addItem(text, '测试');
	
		panel.addItemByConfig({
			editorClassName : 'com.doubeye.TextEdit',
			label : '占一行',
			wholeRow : true
		});
		
		
		text = new com.doubeye.TextEdit({
			parent : 'container',
			value : 1,
			wholeRow : true
		}).init().render();
		panel.addItem(text, '另一个站一行');
		
		text = new com.doubeye.TextEdit({
			parent : 'container',
			value : 2
		}).init().render();
		panel.addItem(text, '测试2');
	}).appendTo('#container').trigger('click');
};