jQuery(document).ready(function() {
	test_QuarterPicker();
	test_QuarterPicker_showtime();
});

var c;
var test_QuarterPicker = function(){
	jQuery('<input>', {
		value : 'test_QuarterPicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.QuarterPicker({
		//value : 3,
		parent : '#container'
	}).init().render();
	//c.setValue(3);
	//c.setValue(6);
	c.setValue('dsf');
	}).appendTo('#container').click();
};

var test_QuarterPicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_QuarterPicker_showtime',
		type : 'button'
	}).click(function(){
		alert(c.getValue());
	}).appendTo('#container');
};