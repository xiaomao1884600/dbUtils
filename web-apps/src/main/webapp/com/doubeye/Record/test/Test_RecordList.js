jQuery(document).ready(function() {
	test_RecordList();
});

test_RecordList = function() {
		jQuery('<input>', {
		value : 'test_RecordList',
		type : 'button'
	}).click(function() {
		var array = [];
		array.push({
			f1 : 'a',
			f2 : 'b'
		});
		array.push({
			f1 : 'c',
			f2 : 'd'
		});
		array.push({
			f1 : 'a',
			f2 : 'e'
		});
		array.push({
			f1 : 'a',
			f2 : 'b'
		});
		array.push({
			f1 : 'b',
			f2 : 'd'
		});
		array.push({
			f1 : 'a',
			f2 : 'e'
		});
		var list = new com.doubeye.RecordList();
		list.addAllData(array);
		var data = list.find({
			f1 : 'a',
			f2 : 'b'
		});
		//第0行
		//alert(com.doubeye.Ext.encode(data));
		//第3行
		data = list.find({
			f1 : 'a',
			f2 : 'b'
		}, 1);
		//注意：测试用的所有的行号都是从0开始的，由于deleteByCondition函数会调用findAll方法，因此不再单独测试findAll
		//同理，不带参数版的addRecord也被addData调用，不再单独测试
		//alert(com.doubeye.Ext.encode(data));
		//删除第1行
		//list.deleteRecord(1);
		//删除s所有f1=a的行，即0,2,3,4,5
		//list.deleteByCondition({f1 : 'a'});
		//从第4行开始删除s所有f1=a的行，即4,5
		//list.deleteByCondition({f1:'a'} , 4);
		//删除f1=a,f2=b的所有行，即0,3行
		//list.deleteByCondition({f1 : 'a', f2 : 'b'});
		//从第1行开始删除s所有f1=a的行，即3
		//list.deleteByCondition({f1 : 'a', f2 : 'b'}, 1);
		//list.addRecord();
		//在第3行后面添加一行
		//list.insertAfter(null, 3);
		//在第3行前面添加一行
		list.insertBefore(null, 3);
		alert(com.doubeye.Ext.encode(list.records));		
	}).appendTo('#container').trigger('click');
};