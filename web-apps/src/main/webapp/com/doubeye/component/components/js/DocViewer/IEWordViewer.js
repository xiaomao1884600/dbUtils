/**
 *
 * Ddf文档查看器
 * @author doubeye
 * @version 1.0.0
 * config
 * docUrl {URL} 文档的url
 * searchText {String} 文档打开后自动查找的字符串
 */
com.doubeye.IEWordViewer = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.IEWordViewer',
	holderId : null,
	init : function() {
		com.doubeye.IEWordViewer.superclass.init.call(this);
		this.holderId = this.rootId + 'holder';
		return this;
	},
	render : function() {
		if (jQuery.browser.msie) {
			com.doubeye.IEWordViewer.superclass.render.call(this);
			this.createViewer();
		} else {
			this.renderRootComponent();
			this.rootComponent.html('<H3>com.doubeye.IEWordViewer仅支持IE浏览器</H3>');
			window.location.href = this.docUrl;
		}
		return this;
	},
	createViewer : function() {
		/*
		var obj = '<object id="FramerControl1" width="100%" height="100%"  codebase="dsoframer.ocx" classid="clsid:00460182-9E5E-11d5-B7C8-B8269041DD57" >' + 
		'<param name="_ExtentX" value="16960" />' + '<param name="_ExtentY" value="13600" />' + 
		'<param name="BorderColor" value="-2147483632" />' + 
		'<param name="BackColor" value="-2147483643" />' + 
		'<param name="ForeColor" value="-2147483640" />' + 
		'<param name="TitlebarColor" value="-2147483635" />' + 
		'<param name="TitlebarTextColor" value="-2147483634" />' + 
		'<param name="BorderStyle" value="1" />' + 
		'<param name="Titlebar" value="0" />' + 
		'<param name="Toolbars" value="0" />' + 
		'<param name="Menubar" value="0" />' + 
		'<param name="IsNoCopy" value="-1" />' +
		'</object>';
		jQuery(obj).appendTo(this.rootComponent);
		var eventDiv = '<div style="display:none;">'
					 '<script type="text/javascript" language="jscript" for="FramerControl1" event="OnDocumentOpened(str,obj)">  ' +
                     '		alert("");' +
                     '       var selection = obj.Application.Selection;' +
                     '        selection.HomeKey(6); ' +
					'						     selection.Find.ClearFormatting();' +
					'						     selection.Find.Replacement.ClearFormatting();' +
					'						     selection.Find.Text = "女孩子";' +
					'						        while (selection.Find.Execute()){' +
					'						         selection.Range.HighlightColorIndex = 7;' +
					'						     }' +
                     '   </script> ' +
			'</div>';
		var opened = function(str, obj) {
			alert(str);
		};
		var eventDiv = '<div style="display:none;">' +
		 '<script type="text/javascript" language="jscript" for="FramerControl1" event="OnDocumentOpened(str,obj)">  ' +
		 'alert("");' + 
		 'opened(str, obj);' +
		 '</script> ' +
		'</div>';
		jQuery(eventDiv).appendTo(this.rootComponent);
		
		
		jQuery('#FramerControl1').bind('DocumentOpened', {}, function(str, obj) {
			alert('');
			var selection = obj.Application.Selection;
			selection.HomeKey(6);
			// 设置文档中的光标到文件首。
			selection.Find.ClearFormatting();
			selection.Find.Replacement.ClearFormatting();
			selection.Find.Text = '女孩子';
			while (selection.Find.Execute()) {
				selection.Range.HighlightColorIndex = 7;// 高亮时的背景色
			}
		});
		*/
		//document.all.FramerControl1.open('http://127.0.0.1:8080/redeemer/main/testCenter/ra.docx', true, "Word.Document");
		//return;
		//this.docUrl = com.doubeye.Utils.getQueryString('docUrl');
		//alert(this.docUrl);

		this.openApplicationName();
		
	},
	openApplicationName : function() {
		var suffix = com.doubeye.Utils.String.getStringAfterLastDelimiter(this.docUrl, '.');
		var appName = "";
		var loweredSuffix = suffix.toLowerCase();
		var url = this.docUrl;
		if (this.keyword) {
			url = url + '&searchText=' + this.searchText
		}
		if (loweredSuffix == 'doc' || loweredSuffix == 'docx' || loweredSuffix == 'txt') {
			appName = "Word.Document";
			document.all.FramerControl1.open(url, true, appName);
		} else if (loweredSuffix == 'xls' || loweredSuffix == 'xlsx' || loweredSuffix == 'csv') {
			appName = "Excel.Workbook";
			document.all.FramerControl1.open(url, appName);
		}
		/* ppt 没有找到更好的打开方式
		else if (loweredSuffix == 'ppt' || loweredSuffix == 'pptx') {
			appName = "PowerPoint.Show";
			document.all.FramerControl1.open(url, appName);
		}
		*/
	}
});
