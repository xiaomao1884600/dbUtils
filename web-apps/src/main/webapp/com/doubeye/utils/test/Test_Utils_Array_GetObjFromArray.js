jQuery(document).ready(function() {
	test_Utils_Array_GetObjFromArray();
});

test_Utils_Array_GetObjFromArray = function() {
		jQuery('<input>', {
		value : 'test_Utils_Array_GetObjFromArray',
		type : 'button'
	}).click(function() {
		var array = [];
		array.push({
			f1 : 'a',
			f2 : 'b',
			i:0
		});
		array.push({
			f1 : 'c',
			f2 : 'd',
			i : 1
		});
		array.push({
			f1 : 'a',
			f2 : 'e',
			i : 2
		});
		//第二个元素
		/*
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.array.getObjectFromArray({
			f1 : 'c'
		}, array)));
		*/
		//空
		/*
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.array.getObjectFromArray({
			f1 : 'd'
		})));
		*/
		//第三个元素
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.array.getObjectFromArray({
			f1 : 'a',
			f2 : 'e'
		}, array)));
		//第三个元素
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.array.getObjectFromArray({
			f1 : 'a'
		}, array, 1)));
		//空
		alert(com.doubeye.Ext.encode(com.doubeye.Utils.array.getObjectFromArray({
			f1 : 'a',
			f2 : 'd'
		}, array)));
	}).appendTo('#container').trigger('click');
};