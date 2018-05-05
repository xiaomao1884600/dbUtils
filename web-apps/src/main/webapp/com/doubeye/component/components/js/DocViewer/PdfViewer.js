/**
 *
 * Ddf文档查看器
 * @author doubeye
 * @version 1.0.0
 * config
 * docUrl {URL} 文档的url
 * searchText {String} 文档打开后自动查找的字符串
 * @history
 */
com.doubeye.PdfViewer = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.PdfViewer',
	holderId : null,
	init : function() {
		com.doubeye.PdfViewer.superclass.init.call(this);
		this.holderId = this.rootId + 'holder';
		return this;
	},
	render : function() {
		com.doubeye.PdfViewer.superclass.render.call(this);
		this.renderHolder();
		this.createViewer();
		return this;
	},
	renderHolder : function() {
		var width = this.rootComponent.innerWidth();
		var parentObj = this.rootComponent.parent();
		var height = parentObj.height();
		jQuery('<div>', {
			id : this.holderId,
			style : 'width' + width + 'px;height:' + height + 'px;display:block;'
		}).appendTo(this.rootComponent);
	},
	createViewer : function() {
		var success = new PDFObject({
			url : this.docUrl
			/*,
			pdfOpenParams : {
				view : 'FitH',
				pagemode : 'thumbs',
				scrollbars : '1',
				search: 'XML',
				toolbar : '1',
				statusbar : '1',
				messages : '1',
			}*/
		}).embed(this.holderId);
	}
});
