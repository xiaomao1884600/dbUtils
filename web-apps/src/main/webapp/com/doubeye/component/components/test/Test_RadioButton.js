jQuery(document).ready(function() {
	Test_RadioButton();
});

Test_RadioButton = function() {
	var radios;
	jQuery('<input>', {
		value : 'Test_RadioButton',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container'
		}, com.doubeye.Ext.decode("{'options' : [{'label' : '保持默认', 'value' : null}, {'label' : 'NULLABLE', 'value' : 'NULLABLE'}, {label : 'NOT NULLABLE', 'value' : 'NOT NULLABLE'}], 'name' : 'nullable'}"));
		radios = new com.doubeye.RadioButton(config).init().render();
	}).appendTo('#container').trigger('click');
	jQuery('<input>', {
		value : 'Test_RadioButton_getValue',
		type : 'button'
	}).click(function(){
		alert(radios.getValue());
	}).appendTo('#container');
	jQuery('<input>', {
		value : 'Test_RadioButton_setValue',
		type : 'button'
	}).click(function(){
		radios.setValue('NOT NULLABLE');
		alert(radios.getValue());
	}).appendTo('#container');
};