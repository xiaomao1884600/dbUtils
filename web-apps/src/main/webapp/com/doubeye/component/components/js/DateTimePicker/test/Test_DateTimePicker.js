jQuery(document).ready(function() {
	test_DateTimePicker();
	test_DateTimePicker_showtime();
});

var c;
var test_DateTimePicker = function(){
	jQuery('<input>', {
		value : 'test_DateTimePicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.DateTimePicker({
		parent : '#container'
	}).init().render();
	}).appendTo('#container').click();
};

var test_DateTimePicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_DateTimePicker_showtime',
		type : 'button'
	}).click(function(){
		//c.setValue('aa');
		//c.setValue('2007');
		//c.setValue('2008-09');
		//c.setValue('2009-04-24');
		//c.setValue('2010-03-12 12');
		//c.setValue('2011-12-30 13:23');
		c.setValue('2012-4-12 16:33:43');
		alert(c.getValue());
	}).appendTo('#container');
};