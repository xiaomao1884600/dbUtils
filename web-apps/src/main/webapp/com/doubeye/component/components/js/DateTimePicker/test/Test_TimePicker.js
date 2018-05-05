jQuery(document).ready(function() {
	test_TimePicker();
	test_TimePicker_showtime();
});

var c;
var test_TimePicker = function(){
	jQuery('<input>', {
		value : 'test_TimePicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.TimePicker({
		parent : '#container'
	}).init().render();
	}).appendTo('#container').click();
};

var test_TimePicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_TimePicker_showtime',
		type : 'button'
	}).click(function(){
		//c.setValue('aa');
		c.setValue('01:23:34,');
		//c.setValue('21');
		//c.setValue('12:23');
		//c.setValue('15:43:23');
		alert(c.getValue());
	}).appendTo('#container');
};