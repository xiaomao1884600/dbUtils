jQuery(document).ready(function() {
	test_AjaxManager();
});

test_AjaxManager = function() {
		jQuery('<input>', {
		value : 'test_AjaxManager',
		type : 'button'
	}).click(function() {
		/*
		setTimeout(function(){
			alert('1');
		}, 5000);
		alert('2');
		return;
		*/
		var ajaxManager = new com.doubeye.AjaxManager({
			maxConcurrency : 2
		});
		for (var i = 1; i <= 10; i ++) {
			var ajax = new com.doubeye.Ajax({
				url : '/ServletUtilsTest?action=multiAjaxTest',
				manager : ajaxManager
			});
			ajaxManager.addAjax(ajax);
		}
		ajaxManager.start();
	}).appendTo('#container').trigger('click');
};