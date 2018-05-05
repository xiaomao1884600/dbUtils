jQuery(document).ready(function() {
	test_ModalDialog();
});

test_ModalDialog = function() {
		jQuery('<input>', {
		value : 'test_ModalDialog',
		type : 'button'
	}).click(function() {
		var dialog = new com.doubeye.ModalPanelDialog({
			title : 'cuowu'
		}).init().render();
		var text = new com.doubeye.TextEdit({
			parent : 'container',
			value : 1
		}).init().render();
		dialog.addItem(text, '测试');
		text = new com.doubeye.TextEdit({
			parent : 'container',
			value : 2
		}).init().render();
		dialog.addItem(text, '测试2');
		dialog.autoAdjustHeight();
	}).appendTo('#container').trigger('click');
};