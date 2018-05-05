jQuery(document).ready(function(){
	var testStrings = function() {
		/*
		var source = 'abc def';
		var delimeter = ' ';
		//测试一个字节长度的delimeter
		alert('测试一个字节长度的delimeter');
		alert(com.doubeye.Utils.String.getStringBefore(source, delimeter) + ' 应该显示的值为abc');
		alert(com.doubeye.Utils.String.getStringAfter(source, delimeter) + ' 应该显示的值为def');
		//测试多个字节长度的delimeter
		alert('测试多个字节长度的delimeter');
		delimeter = 'c d';
		alert(com.doubeye.Utils.String.getStringBefore(source, delimeter) + ' 应该显示的值为ab');
		alert(com.doubeye.Utils.String.getStringAfter(source, delimeter) + ' 应该显示的值为ef');
		//测试getStringBetween，单字符
		alert('测试getStringBetween，单字符');
		var leftDelimeter = 'b', rightDelimeter = 'e';
		alert(com.doubeye.Utils.String.getStringBetween(source, leftDelimeter, rightDelimeter) + ' 应该显示的值为c d');
		//测试getStringBetween，过个字符
		alert('测试getStringBetween，多个字符');
		var leftDelimeter = 'bc', rightDelimeter = 'ef';
		alert(com.doubeye.Utils.String.getStringBetween(source, leftDelimeter, rightDelimeter) + ' 应该显示的值为 d');
		*/
		
		
		//测试getStringJustBeforeAndBetween
		var source = ' #00ff00 solid   3px  dsf';
		var secondDelimeter = 'px', firstDelimeter = ' ';
		alert('测试getStringJustBeforeAndBetween，secondDelimeter不为结尾');
		alert(com.doubeye.Utils.String.getStringJustBeforeAndBetween(source, secondDelimeter, firstDelimeter) + ' 应该显示的值为3');
		alert('测试getStringJustBeforeAndBetween，secondDelimeter为结尾');
		source = ' #00ff00 solid   3px;';
		alert(com.doubeye.Utils.String.getStringJustBeforeAndBetween(source, secondDelimeter, firstDelimeter) + ' 应该显示的值为3');
		alert('测试getStringJustBeforeAndBetween，在secondDelimeter之前不包含firstDelimeter');
		source = '3px; 风第三方';
		alert(com.doubeye.Utils.String.getStringJustBeforeAndBetween(source, secondDelimeter, firstDelimeter) + ' 应该显示的值为3');
	};
	
	jQuery('<input>', {
		type : 'button',
		value : 'testStrings'
	}).click(function(){
		testStrings();
	}).appendTo('#buttons');
});