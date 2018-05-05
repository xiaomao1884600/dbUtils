jQuery(document).ready(function() {
	test_YearPicker();
	test_YearPicker_showtime();
});

var c;
var test_YearPicker = function(){
	jQuery('<input>', {
		value : 'test_YearPicker',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.YearPicker({
		parent : '#container',
		value : '2055a'
	}).init().render();
	}).appendTo('#container').click();
};

var test_YearPicker_showtime = function(){
	jQuery('<input>', {
		value : 'test_YearPicker_showtime',
		type : 'button'
	}).click(function(){
		alert(c.getValue());
	}).appendTo('#container');
};