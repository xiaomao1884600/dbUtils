jQuery(document).ready(function() {
	//test_normalDialog();
	//test_BubbleDialog();
	//test_AdvenceErrorDialog();
	test_WaitingDialog();
	//test_Ajax_AdvenceErrorDialog();
});

test_normalDialog = function() {
		jQuery('<input>', {
		value : 'test_normalDialog',
		type : 'button'
	}).click(function() {
		new com.doubeye.NormalDialog({
			message : 'fsdsfdsf'
		}).init().render();
	}).appendTo('#container').trigger('click');
};

test_BubbleDialog = function() {
		jQuery('<input>', {
		value : 'test_BubbleDialog',
		type : 'button'
	}).click(function() {
		new com.doubeye.BubbleDialog({
			message : 'fsdsfdsf',
			last : 3000,
			bg : true
		}).init().render();
	}).appendTo('#container').trigger('click');
};

test_AdvenceErrorDialog = function() {
		jQuery('<input>', {
		value : 'test_AdvenceErrorDialog',
		type : 'button'
	}).click(function() {
		new com.doubeye.AdvenceErrorDialog({
			title : 'cuowu',
			message : 'fsdsfdsf'
		}).init().render();
	}).appendTo('#container').trigger('click');
};

test_WaitingDialog = function() {
		jQuery('<input>', {
		value : 'test_WaitingDialog',
		type : 'button'
	}).click(function() {
		new com.doubeye.WaitingDialog({
			
		}).init().render();
	}).appendTo('#container').trigger('click');
};

test_Ajax_AdvenceErrorDialog = function() {
	jQuery(jQuery('<div>'), {
		html : 'fsdfgaioyhfioajfgiorjh'
	}).dialog({ dialogClass: 'alert' });
	jQuery(jQuery('<div>'), {
		html : 'fsdfgaioyhfioajfgiorjh'
	}).dialog();
	return;
	jQuery('<input>', {
		value : 'test_AdvenceErrorDialog',
		type : 'button'
	}).click(function() {
		var ajax = new com.doubeye.Ajax({
			url : '/Test',
			params : {
				action : 'getASQLException'
			}
		});
	}).appendTo('#container').trigger('click');
};