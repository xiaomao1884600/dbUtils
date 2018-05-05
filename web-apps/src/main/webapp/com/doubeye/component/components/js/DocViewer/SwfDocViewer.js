/**
 *
 * Swf文档查看器
 * @author doubeye
 * @version 1.0.0
 * config
 * docUrl {URL} 文档的url
 * searchText {String} 文档打开后自动查找的字符串
 * @history
 * 1.0.0:
 */
com.doubeye.SwfDocViewer = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.SwfDocViewer',
	holderId : null,
	init : function() {
		com.doubeye.SwfDocViewer.superclass.init.call(this);
		this.holderId = this.rootId + 'holder';
		return this;
	},
	render : function() {
		com.doubeye.SwfDocViewer.superclass.render.call(this);
		this.renderHolder();
		this.createViewer();
		return this;
	},
	renderHolder : function() {
		jQuery('<a>', {
			id : this.holderId,
			style : 'width:820px;height:650px;display:block'
		}).appendTo(this.rootComponent);
	},
	createViewer : function() {
		jQuery('#' + this.holderId).FlexPaperViewer({
			config : {
				SwfFile : this.docUrl,
				ZoomTransition : 'easeOut',
				ZoomTime : 0.5,
				ZoomInterval : 0.1,
				FitPageOnLoad : true,
				FitWidthOnLoad : true,
				FullScreenAsMaxWindow : true,
				ProgressiveLoading : true,
				MinZoomSize : 0.2,
				MaxZoomSize : 5,
				SearchMatchAll : false,
				InitViewMode : 'Portrait',

				ViewModeToolsVisible : true,
				ZoomToolsVisible : true,
				NavToolsVisible : true,
				CursorToolsVisible : true,
				SearchToolsVisible : true,
				localeChain : 'zh_CN'
			}
		});
		if (this.searchText) {
			var thiz = this;
			jQuery('#' + this.holderId).bind('onDocumentLoaded',function(e){
				var viewer = $FlexPaper(thiz.holderId);
				viewer.searchText(thiz.searchText);
				//viewer.highlight(thiz.searchText);
	    	});
    	}
	}
});
