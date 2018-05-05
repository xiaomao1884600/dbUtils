jQuery(document).ready(function() {
	test_YearQuarterPicker();
	test_YearQuarterPicker_showtime();
});

var c;
var test_YearQuarterPicker = function(){
	jQuery('<input>', {
		value : 'test_YearQuarterPicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.YearQuarterPicker({
		parent : '#container'
	}).init().render();
	//c.setValue(3);
	//c.setValue(6);
	//c.setValue('dsf');
	}).appendTo('#container').click();
};

var test_YearQuarterPicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_YearQuarterPicker_showtime',
		type : 'button'
	}).click(function(){
		//c.setValue('2012-03-04 3');
		//c.setValue('2012-03-04 3:23');
		//c.setValue('2012-03-04 13:23:32');
		//c.setValue('2015~03');
		c.setValue('2015~2');
		alert(c.getValue());
	}).appendTo('#container');
};