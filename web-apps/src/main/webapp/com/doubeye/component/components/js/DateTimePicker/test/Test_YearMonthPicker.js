jQuery(document).ready(function() {
	test_YearMonthPicker();
	test_YearMonthPicker_showtime();
});

var c;
var test_YearMonthPicker = function(){
	jQuery('<input>', {
		value : 'test_YearMonthPicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.YearMonthPicker({
		//value : 3,
		parent : '#container'
	}).init().render();
	//c.setValue(3);
	//c.setValue(6);
	//c.setValue('dsf');
	}).appendTo('#container').click();
};

var test_YearMonthPicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_YearMonthPicker_showtime',
		type : 'button'
	}).click(function(){
		//c.setValue('2012-03-04 3');
		//c.setValue('2012-03-04 3:23');
		c.setValue('2012-03-04 13:23:32');
		alert(c.getValue());
	}).appendTo('#container');
};