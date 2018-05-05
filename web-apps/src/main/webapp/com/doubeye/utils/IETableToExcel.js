/** 功能：HTML中Table对象转换为Excel通用对象.
 * 
 * 注意：仅支持IE，并且客户端必须安装Excel
 * 参数：tableID HTML中Table对象的ID属性值
 * 说明：
 * 能适应复杂的HTML中Table对象的自动转换，能够自动根据行列扩展信息
 * 合并Excel中的单元格，客户端需要安装有Excel
 * 详细的属性、方法引用说明参见：Excel的Microsoft Excel Visual Basic参考
 * 示范：
 * var tb = new IETableToExcel('demoTable');
 * tb.setFontStyle("Courier New");
 * tb.setFontSize(10); //推荐取值10
 * tb.setFontColor(6); //一般情况不用设置
 * tb.setBackGround(4); //一般情况不用设置
 * tb.setTableBorder(2); //推荐取值2
 * tb.setColumnWidth(10); //推荐取值10
 * tb.isLineWrap(false);
 * tb.isAutoFit(true);
 * tb.getExcelFile();
 * 
 * config
 *  renderStyle {boolean} 是否渲染表格的字体、边框样式等样式，默认为false，用来提高导出速度
 *  table {id|jQueryObject<table>} 可以id，也可以是jQuery的table对象
 *  valueSelector {jQuerySelector} 默认为空，即将整个cell输入，如果包含该值，则仅输入cell下符合条件的element中的内容
 * 
 * 如果设置了单元格自适应，则设置单元格宽度无效
 * 版本：1.1
 * 版本历史 ：
 *  1.1 
 *  ! 构造函数采用config模式
 *  + 增加参数renderStyle {boolean} 是否渲染表格的字体、边框样式等样式，默认为false，用来提高导出速度
 *  ! tableID 参数改为table参数，可以id，也可以是jQuery的table对象
 *  ! 加入valueSelector 默认为空，即将整个cell输入，如果包含该值，则仅输入cell下符合条件的element中的内容
 */
com.doubeye.IETableToExcel = function(config) {
	this.tableBorder = -1; //边框类型，-1没有边框 可取1/2/3/4
	this.backGround = 0; //背景颜色：白色 可取调色板中的颜色编号 1/2/3/4....
	this.fontColor = 1; //字体颜色：黑色
	this.fontSize = 10; //字体大小
	this.fontStyle = "宋体"; //字体类型
	this.rowHeight = 20; //行高
	this.columnWidth = -1; //列宽
	this.lineWrap = true; //是否自动换行
	this.textAlign = -4108; //内容对齐方式 默认为居中
	this.autoFit = true; //是否自适应宽度
	//this.tableID = tableID;
	this.renderStyle = false;
	com.doubeye.Ext.apply(this, config);
};

com.doubeye.IETableToExcel.prototype.setTableBorder = function (excelBorder) {
	this.tableBorder = excelBorder ;
};
com.doubeye.IETableToExcel.prototype.setBackGround = function (excelColor) {
	this.backGround = excelColor;
};
com.doubeye.IETableToExcel.prototype.setFontColor = function (excelColor) {
	this.fontColor = excelColor;
};
com.doubeye.IETableToExcel.prototype.setFontSize = function (excelFontSize) {
	this.fontSize = excelFontSize;
};
com.doubeye.IETableToExcel.prototype.setFontStyle = function (excelFont) {
	this.fontStyle = excelFont;
};
com.doubeye.IETableToExcel.prototype.setRowHeight = function (excelRowHeight) {
	this.rowHeight = excelRowHeight;
};
com.doubeye.IETableToExcel.prototype.setColumnWidth = function (excelColumnWidth) {
	this.columnWidth = excelColumnWidth;
};
com.doubeye.IETableToExcel.prototype.isLineWrap = function (lineWrap) {
	if (lineWrap == false || lineWrap == true) {
		this.lineWrap = lineWrap;
	}
};
com.doubeye.IETableToExcel.prototype.setTextAlign = function (textAlign) {
	this.textAlign = textAlign;
};
com.doubeye.IETableToExcel.prototype.isAutoFit = function(autoFit) {
	if(autoFit == true || autoFit == false)
		this.autoFit = autoFit ;
};
//文件转换主函数
/**
 * @params title 标题
 * @params additionObj 额外的信息，类容为{left:'',right:''}
 */
com.doubeye.IETableToExcel.prototype.getExcelFile = function (title, additionObj) {
	var jXls, myWorkbook, myWorksheet, myHTMLTableCell, myExcelCell, myExcelCell2;
	var myCellColSpan, myCellRowSpan;
	var additionRowCount = 0;
	var tableObj;
	if (title) {
		additionRowCount ++;
	}
	if (additionObj) {
		additionRowCount ++;
	}

	var readRow = 0, readCol = 0;
	var totalRow = 0, totalCol = 0;
	var tabNum = 0;

	//搜索需要转换的Table对象，获取对应行、列数
	if (!this.table.jquery) {
		if (!this.table.substr(0,1) == '#') {
			this.table = '#' + this.table;
			this.table = jQuery(this.table);
		}	
	}
	if (this.table.length == 0) {
		return;
	}
	tableObj = this.table[0];
	try {
		jXls = new ActiveXObject('Excel.Application');
	} catch (e) {
		alert("无法启动Excel!\n\n如果您确信您的电脑中已经安装了Excel，"+"那么请调整IE的安全级别。\n\n具体操作：\n\n"+"工具 → Internet选项 → 安全 → 自定义级别 → 对没有标记为安全的ActiveX进行初始化和脚本运行 → 启用");
		return false;
	}
	jXls.Visible = true;
	myWorkbook = jXls.Workbooks.Add();
	jXls.DisplayAlerts = false;
	myWorkbook.Worksheets(3).Delete();
	myWorkbook.Worksheets(2).Delete();
	jXls.DisplayAlerts = true;
	myWorksheet = myWorkbook.ActiveSheet;


	//设置行高、列宽
	if(this.columnWidth != -1)
		myWorksheet.Columns.ColumnWidth = this.columnWidth;
	else
		myWorksheet.Columns.ColumnWidth = 7;
	if(this.rowHeight != -1)
		myWorksheet.Rows.RowHeight = this.rowHeight ;

	
	tableObj = this.table[0];
	totalRow = tableObj.rows.length;
	for (i = 0; i < tableObj.rows[0].cells.length; i++) {
		myHTMLTableCell = tableObj.rows(0).cells(i);
		myCellColSpan = myHTMLTableCell.colSpan;
		totalCol = totalCol + myCellColSpan;
	}
	//开始构件模拟表格
	var excelTable = new Array();
	for (i = 0; i <= totalRow; i++) {
		excelTable[i] = new Array();
		for (t = 0; t <= totalCol; t++) {
			excelTable[i][t] = false;
		}
	}
	//开始转换表格
	var maxColCount = 0;
	for (z = 0; z < tableObj.rows.length; z++) {
		readRow = z + 1;
		readCol = 0;
		for (c = 0; c < tableObj.rows(z).cells.length; c++) {
			myHTMLTableCell = tableObj.rows(z).cells(c);
			myCellColSpan = myHTMLTableCell.colSpan;
			myCellRowSpan = myHTMLTableCell.rowSpan;
			if (this.valueSelector) {
				if (jQuery(myHTMLTableCell).find(this.valueSelector).length == 0) {
					continue;
				} else {
					myHTMLTableCell = jQuery(myHTMLTableCell).find(this.valueSelector)[0];
				}
			}
			for (y = 1; y <= totalCol; y++) {
				if (excelTable[readRow][y] == false) {
					readCol = y;
					break;
				}
			}
	
			if (myCellColSpan * myCellRowSpan > 1) {
				myExcelCell = myWorksheet.Cells(readRow + additionRowCount, readCol);
				myExcelCell2 = myWorksheet.Cells(readRow + additionRowCount + myCellRowSpan - 1, readCol + myCellColSpan - 1);
				myWorksheet.Range(myExcelCell, myExcelCell2).Merge();
				if (this.renderStyle) {
					myExcelCell.HorizontalAlignment = this.textAlign;
					myExcelCell.Font.Size = this.fontSize;
					myExcelCell.Font.Name = this.fontStyle;
					myExcelCell.wrapText = this.lineWrap;
					myExcelCell.Interior.ColorIndex = this.backGround;
					myExcelCell.Font.ColorIndex = this.fontColor;
					if(this.tableBorder != -1){
					 	myWorksheet.Range(myExcelCell, myExcelCell2).Borders(1).Weight = this.tableBorder ;
					 	myWorksheet.Range(myExcelCell, myExcelCell2).Borders(2).Weight = this.tableBorder ;
					 	myWorksheet.Range(myExcelCell, myExcelCell2).Borders(3).Weight = this.tableBorder ;
					 	myWorksheet.Range(myExcelCell, myExcelCell2).Borders(4).Weight = this.tableBorder ;
					}
				}
				myExcelCell.Value = myHTMLTableCell.innerText;
				for (row = readRow; row <= myCellRowSpan + readRow - 1; row++) {
					for (col = readCol; col <= myCellColSpan + readCol - 1; col++) {
						excelTable[row][col] = true;
					}
				}
				readCol = readCol + myCellColSpan;
			} else {
				myExcelCell = myWorksheet.Cells(readRow + additionRowCount, readCol);
				myExcelCell.Value = myHTMLTableCell.innerText;
				if (this.renderStyle) {
					myExcelCell.HorizontalAlignment = this.textAlign;
					myExcelCell.Font.Size = this.fontSize;
					myExcelCell.Font.Name = this.fontStyle;
					myExcelCell.wrapText = this.lineWrap;
					myExcelCell.Interior.ColorIndex = this.backGround;
					myExcelCell.Font.ColorIndex = this.fontColor;
					if(this.tableBorder != -1){
						myExcelCell.Borders(1).Weight = this.tableBorder ;
						myExcelCell.Borders(2).Weight = this.tableBorder ;
						myExcelCell.Borders(3).Weight = this.tableBorder ;
						myExcelCell.Borders(4).Weight = this.tableBorder ;
					}
				}
				excelTable[readRow][readCol] = true;
				readCol = readCol + 1;
			}
		}
		maxColCount = maxColCount > readCol - 1 ? maxColCount : readCol - 1;
	}
	if (title){
		myExcelCell = myWorksheet.Cells(1, 1);
		myExcelCell2 = myWorksheet.Cells(1, maxColCount);
		myWorksheet.Range(myExcelCell, myExcelCell2).Merge();
		myExcelCell.Value = title;
		myExcelCell.HorizontalAlignment = this.textAlign;
	}
	if (additionObj){
		myExcelCell = myWorksheet.Cells(2, 1);
		myExcelCell.Value = additionObj.left;
		myExcelCell = myWorksheet.Cells(2, maxColCount);
		myExcelCell.Value = additionObj.right;
		myExcelCell.HorizontalAlignment = this.textAlign;
	}
	if(this.autoFit == true)
		myWorksheet.Columns.AutoFit;
	jXls.UserControl = true;
	jXls = null;
	myWorkbook = null;
	myWorksheet = null;
}; 