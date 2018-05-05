jQuery(document).ready(function() {
	Test_SwfDocViewer();
});

Test_SwfDocViewer = function() {
	jQuery('<input>', {
		value : 'Test_SwfDocViewer',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			docUrl : '轻松搞定XML.swf',
			searchText : '你'
		});
		var cmp = new com.doubeye.SwfDocViewer(config).init().render();
	}).appendTo('#container').trigger('click');
};