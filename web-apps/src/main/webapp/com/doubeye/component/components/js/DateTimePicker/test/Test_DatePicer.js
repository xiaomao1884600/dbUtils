jQuery(document).ready(function() {
	test_DatePicer();
	test_DatePicer_showtime();
});

var c;
var test_DatePicer = function(){
	jQuery('<input>', {
		value : 'test_DatePicer',
		type : 'button'
	}).click(function(){
	c = new com.doubeye.DatePicker({
		parent : '#container'
	}).init().render();
	}).appendTo('#container').click();
};

var test_DatePicer_showtime = function(){
	jQuery('<input>', {
		value : 'test_DatePicer_showtime',
		type : 'button'
	}).click(function(){
		alert(c.getValue());
	}).appendTo('#container');
};