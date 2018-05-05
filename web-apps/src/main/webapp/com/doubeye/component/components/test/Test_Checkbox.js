jQuery(document).ready(function() {
	Test_Checkbox();
});

Test_Checkbox = function() {
	var cmp;
	jQuery('<input>', {
		value : 'Test_Checkbox',
		type : 'button'
	}).click(function() {
		cmp = new com.doubeye.Checkbox({
			parent : 'container',
			/*
			valueStyle : {
				checkedValue : true,
				uncheckedValue : false
			},
			*/
			valueStyle : {
				checkedValue : 1,
				uncheckedValue : 0
			},
			value : true,
			label : '测试复选框'
		}).init().render();
	}).appendTo('#container').trigger('click');
	var input = jQuery('<input>', {
		value : 'true'
	}).appendTo('#container');
	jQuery('<input>', {
		value : 'setValue',
		type : 'button'
	}).click(function() {
		cmp.setValue(input.val());
	}).appendTo('#container');
	jQuery('<input>', {
		value : 'getValue',
		type : 'button'
	}).click(function() {
		alert(cmp.getValue());
	}).appendTo('#container');
};