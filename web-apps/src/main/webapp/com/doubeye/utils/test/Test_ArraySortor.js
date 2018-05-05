jQuery(document).ready(function() {
	test_arraysortor();
});

var test_arraysortor_needchange = function() {
	jQuery('<input>', {
		value : 'test_arraysortor_needchange',
		type : 'button'
	}).click(function() {
		//测试两个空对象，此时不论是升序还是降序，都不应该需要交换
		
		var obj1 = null;
		var obj2 = undefined;
		var configs = [{dataId : 'a1', order : 'DESC'}, {dataId : 'a2', order : 'ASC'}];
		//false
		//alert(com.doubeye.ArraySortor.isNeedChange(obj1, obj2, configs, true));
		
		//测试一个普通对象和一个空对象，此时应该根据排序规则和isNullSmallest值有所变化
		var obj1 = {a1 : 1, a2 : 2};
		var obj2 = undefined;
		var configs = [{dataId : 'a1', order : 'DESC'}];
		//false
		//alert(com.doubeye.ArraySortor.isNeedChange(obj1, obj2, configs, true));
		//true
		//alert(com.doubeye.ArraySortor.isNeedChange(obj1, obj2, configs, false));
		
		//测试一个普通对象和一个空对象，此时应该根据排序规则和isNullSmallest值有所变化
		var obj1 = {a1 : 1, a2 : 2};
		var obj2 = undefined;
		var configs = [{dataId : 'a1', order : 'ASC'}];
		//false
		//alert(com.doubeye.ArraySortor.isNeedChange(obj1, obj2, configs, true));
		//true
		//alert(com.doubeye.ArraySortor.isNeedChange(obj1, obj2, configs, false));
		// TODO 希望在有时间的情况下好好测试这个类中的各个方法
	}).appendTo('#container').trigger('click');
};
test_arraysortor = function() {
		jQuery('<input>', {
		value : 'test_arraysortor',
		type : 'button'
	}).click(function() {
		var configs = [{dataId : 'ROWNUM', order : 'ASC'}, {dataId : 'TITLE', order : 'ASC'}];
		var data = [{ROWNUM : 2, TITLE : '第一条记录', DEPARTMENT_DESC : '一处', A1 : 'jfoaidjfioahgodfhoaf', aa1 : '第一条记录aa1', aa2 : '第一条记录aa2', aa3 : '第一条记录aa3', ZBCY : '第一条主报处室', b11 : '1b11', b12 : '1b12', b2 : '1b2', LDPS : '的附件傲娇地哦啊哈佛好的 '},
		{ROWNUM : 2, TITLE : '第二条记录', DEPARTMENT_DESC : '二处', A1 : 'djfioahjiofhgaiojfo', aa1 : '第二条记录aa1', aa2 : '第二条记录aa2', aa3 : '第二条记录aa3', ZBCY : '第二条主报处室', b11 : '2b11', b12 : '2b12', b2 : '2b2', LDPS : '的卷发哦哈奇偶发觉偶就'},];
		com.doubeye.ArraySortor.doSort(data, configs, true);
		alert(com.doubeye.Ext.encode(data));	
	}).appendTo('#container').trigger('click');
};
