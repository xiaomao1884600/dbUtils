jQuery(document).ready(function() {
	Test_Combo();
});

Test_Combo = function() {
	var combo;
	jQuery('<input>', {
		value : 'Test_Combo',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			//selectOptions : [{text : '第一个测试', value : 1}, {text : '第二个测试', value : 2}]
			//optionURL : '/ServletUtilsTest?action=getComboOptions'
			optionGetterConfig : [{text : '第一个测试', value : 1}, {text : '第二个测试', value : 2}],
			optionGetter : function(config, combo) {
				var option;
				for (var i = 0; i < config.length; i ++) {
					option = config[i];
					jQuery('<option>', {
						text : option.text,
						value : option.value
					}).appendTo(combo.selectEl);
				}
			}
		});
		combo = new com.doubeye.Combo(config).init().render();
		alert(combo.getValue());
		combo.setValue(2);
		alert(combo.getValue());
	}).appendTo('#container').trigger('click');
};