jQuery(document).ready(function() {
	Test_ComboAddElementDecorator();
});

Test_ComboAddElementDecorator = function() {
	jQuery('<input>', {
		value : 'Test_Combo',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			//selectOptions : [{text : '第一个测试', value : 1}, {text : '第二个测试', value : 2}]
			optionURL : '/Entity?action=get&tableId=4',
			selectOptionAdatper : {
				valueId : 'id',
				textId : 'name'
			},
			//optionGetterConfig : [{text : '第一个测试', value : 1}, {text : '第二个测试', value : 2}],
			/*
			optionGetter : function(config, combo) {
				var option;
				for (var i = 0; i < config.length; i ++) {
					option = config[i];
					jQuery('<option>', {
						text : option.text,
						value : option.value
					}).appendTo(combo.selectEl);
				}
			},
			*/
			addDataConfig : [{
				editorClassName : 'com.doubeye.TextEdit',
				label : '名称',
				name : 'name'
			}],
			addDataURL : '/Entity?action=addEntity&tableId=4'
		});
		var combo = new com.doubeye.ComboAddElementDecorator(config).init().render();
	}).appendTo('#container').trigger('click');
};



