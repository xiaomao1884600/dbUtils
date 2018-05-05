jQuery(document).ready(function() {
	test_float();
});

test_float = function() {
	jQuery('<input>', {
		value : 'test_float',
		type : 'button'
	}).click(function() {
		var el = jQuery('<div>', {
			'class' : 'testfloat',
			html : 'fapfoeqjhfopjqeriupkop'
		}).appendTo('#container');
		el.float({
			//left : 300,
			//top : 500
		});
	}).appendTo('#container').trigger('click');
	jQuery('<input>', {
		value : 'test_unfloat',
		type : 'button'
	}).click(function() {
		jQuery('.testfloat').unfloat({
			//hide : true
			detach : true
		});
	}).appendTo('#container');
};