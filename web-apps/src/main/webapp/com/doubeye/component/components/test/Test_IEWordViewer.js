jQuery(document).ready(function() {
		var config = com.doubeye.Ext.apply({
			parent : 'container',
			docUrl : 'http://127.0.0.1:8080/doc/first.docx',
			searchText : 'XML'
		});
		var cmp = new com.doubeye.IEWordViewer(config).init().render();
});

