/**
 * 全文检索关键词查询页 
 * config
 *  keyword {String} 要搜索的关键字
 *  result {Array<Object>} 搜做结果
 * @version 1.0.2
 * @history
 * 1.0.1:
 *  ! 如果文件类型不被支持，例如ppt文件，则改为下载 
 * 1.0.2
 *  ! 根据窗口大小调整PdfViewer的大小，同时弹出窗口不再能够拖动和改变大小
 */
com.doubeye.SearchResult = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.SearchResult',
	searchEl : null,
	keywordSearch : null,
	keyword : null,
	resultEl : null,
	ieOpenableExtendName : ['pdf', 'swf', 'doc', 'docx', 'xls', 'xlsx', 'txt', 'csv'],
	openableExtendName : ['pdf', 'swf'],
	init : function() {
		com.doubeye.SearchResult.superclass.init.call(this);
		return this;		
	},
	render : function() {
		com.doubeye.SearchResult.superclass.render.call(this);
		this.searchEl = jQuery('<div>').appendTo(this.rootComponent); 
		this.keywordSearch = new com.doubeye.KeywordSearch({
			parent : this.searchEl,
			resultComponent : this,
			keyword : this.keyword
		}).init().render();
		this.resultEl = jQuery('<div>').appendTo(this.rootComponent);
		if (this.result) {
			this.showResult(this.result);
			delete this.result;
		}
		return this;
	},
	clearResult : function() {
		this.resultEl.empty();
	},
	showResult : function(results) {
		var thiz = this;
		if (!com.doubeye.Ext.isArray(results)) {
			return;
		}
		var allOpenable = jQuery.browser.msie ? this.ieOpenableExtendName : this.openableExtendName;
		if (results.length == 0) {
			this.resultEl.html("没有找到任何匹配的结果，请更换关键词后重试");
		} else {
			for (var i = 0; i < results.length; i ++) {
				var result = results[i];
				var storedFileName = result.storedFileName;
				var extendName = com.doubeye.Utils.String.getStringAfterLastDelimiter(storedFileName, '.');
				//TODO 在服务端进行设置 
				var url = storedFileName.replace('D:\\textSearch\\success\\', 'http://127.0.0.1:8080/doc/');
				var openable = com.doubeye.Utils.isValueInObjectArray(extendName, allOpenable);
				var div = jQuery('<div>').appendTo(this.resultEl);
				var h = jQuery('<h3>').appendTo(div);
				var a = jQuery('<a>', {
					text : storedFileName,
					href : openable ? 'javascript:void(0);' : url
				}).appendTo(h);
				a.data('fileUrl', url);
				a.data('extendName', result.extendFileName);
				if (openable) {
					a.click(function(){
						var obj = jQuery(this);
						var fileUrl = obj.data('fileUrl');
						var extendName = obj.data('extendName');
						thiz.openDoc(fileUrl, extendName);
					});
				}
			}
		}
	},
	openDoc : function(url, extendName) {
		var lowerExtendName = extendName.toLowerCase();
		if (lowerExtendName == "pdf") {
			var width = screen.availWidth, height = screen.availHeight;
			var dialog = jQuery('<div>').appendTo(this.rootComponent).dialog({
				width : width,
				height : height,
				resizable : false,
				draggable : false
			});
			new com.doubeye.PdfViewer({
				parent : dialog,
				docUrl : url,
				searchText : this.keywordSearch.inputEl.val()
			}).init().render();
		} else if (lowerExtendName == 'doc' || lowerExtendName == 'docx' || lowerExtendName == 'xls' || lowerExtendName == 'xlsx' || lowerExtendName == 'txt' || lowerExtendName == 'csv') {
			var page = com.doubeye.Utils.toFullPath('/objects/IEOfficeDocViewer.html');
			window.open (page + '?docUrl=' + url + '&searchText=' + this.keywordSearch.inputEl.val(), 'newwindow', 'toolbar=yes,menubar=yes,scrollbars=yes, resizable=no,location=no, status=no'); 
		} else {
			window.open (url, 'newwindow', 'toolbar=yes,menubar=yes,scrollbars=yes, resizable=no,location=no, status=no');
		}
	}
});