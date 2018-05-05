jQuery(document).ready(function() {
	test_EmailSender();
});

test_EmailSender = function() {
		jQuery('<input>', {
		value : 'test_EmailSender',
		type : 'button'
	}).click(function() {
		new com.doubeye.EmailSender({
			parent : 'container',
			mailto : 'doubeye@sina.com',
			subject : 'error report',
			body : 'dfjaojgoijapjfgiorjiodaoujiodjfiorhgiodnfjaioejfoakfpogjo',
			text : '错误报告'
		}).init().render();
	}).appendTo('#container').trigger('click');
};