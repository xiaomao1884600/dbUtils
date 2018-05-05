jQuery(document).ready(function() {
	test_MonthPicker();
	test_MonthPicker_showtime();
});

var c;
var test_MonthPicker = function(){
	jQuery('<input>', {
		value : 'test_MonthPicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.MonthPicker({
		parent : '#container'
	}).init().render();
	}).appendTo('#container').click();
};

var test_MonthPicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_MonthPicker_showtime',
		type : 'button'
	}).click(function(){
		alert(c.getValue());
	}).appendTo('#container');
};