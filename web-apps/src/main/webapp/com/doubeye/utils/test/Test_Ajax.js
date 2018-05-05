jQuery(document).ready(function() {
	test_ajax_success();
});

test_ajax_success = function() {
		jQuery('<input>', {
		value : 'test_ajax_success',
		type : 'button'
	}).click(function() {
		var ajax = new com.doubeye.Ajax({
			//url : '/ServletUtilsTest?action=doSomethingRight',
			//url : '/ServletUtilsTest?action=doSomethingWrong',
			//url : '/ServletUtilsTest?action=getASQLException',
			url : '/ServletUtilsTest?action=getRedirect',
			//showNoSuccessMessage : true,
			noWaitingDialog : true,
			sucessMessage : 'chenggong',
			errorMessage : 'cuowu',
			params : {
				aaa : 'dfsdf',
				bbb : 'dfsfs'
			}
		}).sendRequest();
	}).appendTo('#container').trigger('click');
};