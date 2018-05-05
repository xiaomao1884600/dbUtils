ispgetContextPath = function() {
	var contentPath = '';
	var link = document.getElementsByTagName('script');
	for (var q = 0; q < link.length; q++) {
		var h = !!document.querySelector ? link[q].src : link[q].getAttribute("src", 4), i;
		if (h && (i = h.indexOf('resources/ispire/js')) >= 0) {
			var j = h.indexOf('://');
			contentPath = j < 0 ? h.substring(0, i - 1) : h.substring(h.indexOf('/', j + 3), i - 1);
			return contentPath;
		}
	};
	return contentPath;
};
importJS = function(url) {
	document.write('<script type="text/javascript" src="' + ispgetContextPath() +
	'/resources/ispire/js/' +
	url +
	'"></script>');
};
importJSFromURL = function(url) {
	document.write('<script type="text/javascript" src="' + url + '"></script>');
};


/**
 * 框架类js
 */
//jQuery
var loaderPrefix = com.doubeye.GlobalParameters.loaderPrefix ? com.doubeye.GlobalParameters.loaderPrefix : '';
importJSFromURL(loaderPrefix + '../lib/jQuery/jquery-1.9.1.min.js');
importJSFromURL(loaderPrefix + '../lib/jQuery/jquery-migrate-1.1.1.js');

//jQueryUI
importJSFromURL(loaderPrefix + '../lib/jQuery/jQueryUI/js/jquery-ui-1.10.2.custom.min.js');

//jQueryLayout
importJSFromURL(loaderPrefix + '../lib/jQuery/jQueryLayout/jquery.layout_1.3.0.min.js');

//zTree
importJSFromURL(loaderPrefix + '../lib/jQuery/zTree/js/jquery.ztree.all-3.5.js');

//colorPicer
importJSFromURL(loaderPrefix + '../lib/jQuery/colorPicker/js/colorpicker.js');

//imaegDropdown
importJSFromURL(loaderPrefix + '../lib/jQuery/msdropdown/js/jquery.dd.min.js');
//prompt
importJSFromURL(loaderPrefix + '../lib/jQuery/prompt/js/jquery.prompt.js');
//FlexPaper
importJSFromURL(loaderPrefix + '../lib/FlexPaper/flexpaper_handlers.js');
importJSFromURL(loaderPrefix + '../lib/FlexPaper/flexpaper.js');
//PDFObject
importJSFromURL(loaderPrefix + '../lib/PDFObject/pdfobject.js');

//uploadifive
//importJSFromURL(loaderPrefix + '../lib/jQuery/uploadifive/jquery.uploadifive.min.js');
//uploadify
//importJSFromURL(loaderPrefix + '../lib/jQuery/uploadify/jquery.uploadify.min.js');
importJSFromURL(loaderPrefix + '../lib/jQuery/uploadify/jquery.uploadify-3.1.js');
importJSFromURL(loaderPrefix + '../lib/jQuery/uploadify/swfobject.js');


//ExtJs
/** 目前项目没有使用ExtJs的组件和代码，如果需要可以打开这里的注释， 如果版本不合适，可以修改成合适的版本
importJSFromURL(loaderPrefix + '../lib/ExtJs3.4.0/adapter/ext/ext-base.js');
importJSFromURL(loaderPrefix + '../lib/ExtJs3.4.0/ext-all.js');
importJSFromURL(loaderPrefix + '../lib/ExtJs3.4.0/ext-lang-zh_CN.js');
**/

//取代Ext的工具类
importJSFromURL(loaderPrefix + '../com/doubeye/utils/Ext.js');
//jquery功能扩展
importJSFromURL(loaderPrefix + '../com/doubeye/utils/jQuery.fn.extend.js');
//工具
importJSFromURL(loaderPrefix + '../com/doubeye/utils/Utils.js');
//正则表达式
importJSFromURL(loaderPrefix + '../com/doubeye/utils/RegExpretions.js');
//常量定义
importJSFromURL(loaderPrefix + '../com/doubeye/utils/constant.js');
//对象数组排序器
importJSFromURL(loaderPrefix + '../com/doubeye/utils/ArraySortor.js');
//通用提示信息
importJSFromURL(loaderPrefix + '../com/doubeye/utils/message.js');
//ajax
importJSFromURL(loaderPrefix + '../com/doubeye/utils/Ajax.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/RedirectedAjax.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/AjaxManager.js');
//IETableToExcel
importJSFromURL(loaderPrefix + '../com/doubeye/utils/IETableToExcel.js');
//浏览器兼容性
importJSFromURL(loaderPrefix + '../com/doubeye/utils/ExplorerCompatibility.js');

//组件基类
importJSFromURL(loaderPrefix + '../com/doubeye/component/js/Component.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/js/DataSensor.js');

//Layout
importJSFromURL(loaderPrefix + '../com/doubeye/layout/FlowLayout.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/ColumnLayout.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/TableView.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/TableViewActionRender.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/DefaultTableViewEditorProvider.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/action/Action.js');
importJSFromURL(loaderPrefix + '../com/doubeye/layout/action/ActionCollection.js');



//对话框
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/NormalDialog.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/BubbleDialog.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/WaitingDialog.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/AdvenceErrorDialog.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/ModalDialog.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/dialog/ModalPanelDialog.js');
//搜索
importJSFromURL(loaderPrefix + '../com/doubeye/utils/SearchEngine/BaiduSearch.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/SearchEngine/ComponentSearch.js');
//email
importJSFromURL(loaderPrefix + '../com/doubeye/utils/EmailSender.js');
//PopMenu
importJSFromURL(loaderPrefix + '../com/doubeye/utils/MenuItem.js');
importJSFromURL(loaderPrefix + '../com/doubeye/utils/PopMenu.js');

/**
 * 锁定
 */
importJSFromURL(loaderPrefix + '../com/doubeye/schedule/LockUI.js');

importJSFromURL(loaderPrefix + '../com/doubeye/component/js/ui/MainFrame.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/js/ui/HorizotalAutoFixedImageRender.js');

importJSFromURL(loaderPrefix + '../com/doubeye/utils/Component/BorderSelector/js/BorderSelector.js');

//Record
importJSFromURL(loaderPrefix + '../com/doubeye/Record/Record.js');
importJSFromURL(loaderPrefix + '../com/doubeye/Record/RecordList.js');
//importJSFromURL(loaderPrefix + '../com/doubeye/Record/test/Test_RecordList.js');

//BorderHelper
importJSFromURL(loaderPrefix + '../com/doubeye/propertyHelper/BorderHelper.js');
importJSFromURL(loaderPrefix + '../com/doubeye/propertyHelper/DetailBorderHelper.js');

//dTable
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTableColumnDefine.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTableColumnDefines.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTableRow.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTableRows.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTable.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/dTableFactory.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/CellComponentRender.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableRowCheckboxDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableEditDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableTableSearchDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableOrder/js/dTableOrderDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableOrder/js/dTableOrder.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableOrder/js/dTableSingleColumnOrderManager.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableOrder/js/dTableMultiColumnOrderManager.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTablePage/js/dTablePageDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTablePage/js/dTablePagingTextRender.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableCondition/js/dTableColumnCondition.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableCondition/js/dTableConditionDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableExporter/js/dTableLocalExportDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/dTable/js/plugins/dTableExporter/js/dTableServerExportDecorator.js');

//各种组件
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Label.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/TextEdit.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/RadioButton.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Combo.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/ComboAddElementDecorator.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Password.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Panel.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Button.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Hidden.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DocViewer/SwfDocViewer.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DocViewer/PdfViewer.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DocViewer/IEWordViewer.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Checkbox.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/ToolBar.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/Image.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/ImageExtend.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/ImageExtendGroup.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/upload/FileUploaderFive.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/upload/FileUploader.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/upload/ImageUploader.js');




//时间日期选择器
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/DatePicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/YearPicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/MonthPicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/QuarterPicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/YearMonthPicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/YearQuarterPicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/TimePicker.js');
importJSFromURL(loaderPrefix + '../com/doubeye/component/components/js/DateTimePicker/js/DateTimePicker.js');


//测试使用，正式发布时去掉此行及相应的文件夹
importJSFromURL(loaderPrefix + 'testCenter/testJSLoader.js');

//SystemParameterManager
importJSFromURL(loaderPrefix + '../com/doubeye/projectFunction/SystemParametersEditor.js');


//Connection
//ConnectionTree
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/connection/js/ConnectionTree.js');

//ConeectionManager
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/connection/js/ConnectionManager.js');


//TableManager
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/js/TableManager.js');

importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/ColumnType.js');
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/ColumnTypeManager.js');

importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/ColumnDetailPanel.js');
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/ColumnAdditionalPropertyPanel.js');
importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/TableColumnManager.js');

importJSFromURL(loaderPrefix + '../com/doubeye/databaseManager/table/column/js/ColumnTypeCombo.js');


//TableViewerDesigner
importJSFromURL(loaderPrefix + '../com/doubeye/layout/Designer/js/TableViewerDesigner.js');


//全文检索
importJSFromURL(loaderPrefix + '../com/doubeye/projectFunction/fulltextRetrieval/KeywordSearch.js');
importJSFromURL(loaderPrefix + '../com/doubeye/projectFunction/fulltextRetrieval/SearchResult.js');


//goods
importJSFromURL(loaderPrefix + '../com/doubeye/projectFunction/goods/js/OpenSellingEntryAction.js');
importJSFromURL(loaderPrefix + '../com/doubeye/projectFunction/goods/js/ShowSellingEntriesAction.js');

