jQuery(document).ready(function() {
	//Test_PdfDocViewer();
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			docUrl : '轻松搞定XML.pdf',
			searchText : 'XML'
		});
		var cmp = new com.doubeye.PdfViewer(config).init().render();
});

Test_PdfDocViewer = function() {
	jQuery('<input>', {
		value : 'Test_PdfDocViewer',
		type : 'button'
	}).click(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			docUrl : '轻松搞定XML.swf',
			searchText : '你'
		});
		var cmp = new com.doubeye.PdfViewer(config).init().render();
	}).appendTo('#container').trigger('click');
};