jQuery(document).ready(function(){
	var functionComp;
	var testEnterEdit = function() {
		var container = jQuery('#container');
		var className = 'com.doubeye.DetailBorderHelper';
		var configAttr = {};
		if (jQuery(this).attr('config')) {
			configAttr = Ext.decode(jQuery(this).attr('config'));
		}
		functionComp = com.doubeye.Utils.getClassInstance(className, Ext.apply(configAttr, {
			parent : 'container'
		}));
		functionComp.init();
		if (functionComp.render) {
			functionComp.render();
		}
		com.doubeye.Utils.enterEdit(functionComp, true);
	};
	jQuery('<input>', {
		type : 'button',
		value : 'testEnterEdit'
	}).click(function(){
		testEnterEdit();
	}).appendTo('#buttons');
	
	
	var testStopEdit = function() {
		com.doubeye.Utils.stopEdit(functionComp);
	};
	jQuery('<input>', {
		type : 'button',
		value : 'testStopEdit'
	}).click(function(){
		testStopEdit();
	}).appendTo('#buttons');
});