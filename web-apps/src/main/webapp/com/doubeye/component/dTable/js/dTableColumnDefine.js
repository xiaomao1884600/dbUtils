/**
 * 用来定义dTable使用的标题信息（列定义信息）
 * @author doubeye
 * @version 1.0.3
 * config 
 * 	label {String} 列的显示名
 *  dataId {String} 数据中的属性名
 *  width {int/percent/'*'} 宽度，可以是像素，百分比以及默认
 *  minWidth {int} 最小宽度
 *  maxWidth {int} 最大宽度
 *  childrenObj {com.doubeye.dTableColumnDefine} 子列（复合表头时使用） 
 *  renderValue {function(rowObj, td, value, record, resultSet, rowIndex)} 绘制方法，参数依次为:com.doubeye.dTableRow对象， 单元格的td对象,单元格的值，改行的对象，结果集和行数
 *  component {com.doubeye.Component} 用来显示字段值的组件名称，如果指定了renderValue，则此参数将被忽略，及此参数的优先级低于renderValue
 *  componentConfig {Object} 用来配置component的参数
 *  componentRender {Object} 用来指示table使用com.doubeye.CellComponentRender类进行组件绘制，方便处理以下情况：对于同一列，不同的行的编辑组件不同，目前的参数如下：
 *   configDataId {String} 保存组件配置的列名
 *  resizable 是否允许通过列头调整列的大小，默认为true
 *  disableOrder 禁用该列上的排序，默认为false，即允许排序
 *  disableCondition 禁止在该列上设置条件，默认为false，允许设置条件
 *  keyColumn {boolean} 标识该字段是否为数据的唯一标识，默认为false
 *  hidden {boolean} 表示该列是否隐藏，默认为false，即不隐藏
 *  contentHorizontalAlign {Enumeration<com.doubeye.constant.CSS_ALIGN.HORIZONTAL_ALIGN>} 列的内容的对齐方式，默认为com.doubeye.constant.CSS_ALIGN.HORIZONTAL_ALIGN.LEFT
 *  contentHVerticalAlign {Enumeration<com.doubeye.constant.CSS_ALIGN.VERTICAL_ALIGN>} 列的内容的对齐方式，默认为com.doubeye.constant.CSS_ALIGN.VERTICAL_ALIGN.MIDDLE
 *  
 * @history
 *  1.0.1
 *   + keyColumn {boolean} 标识该字段是否为数据的唯一标识，默认为false
 *  1.0.2
 *   + hidden {boolean} 参数，表示该列是否隐藏
 *  1.0.3
 *   + componentRender 属性，方便处理以下情况：对于同一列，不同的行的编辑组件不同
 *   + contentHorizontalAlign
 *   + contentHorizontalAlign
 */
com.doubeye.dTableColumnDefine = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableColumnDefine',
	resizeEl : null,
	/**
	 * 拖动时的起始位置 
	 */
	__initX : 0,
	__initWidth : 0,
	__initLeft : 0,
	keyColumn : false,
	hidden : false,
	contentHorizontalAlign : com.doubeye.constant.CSS_ALIGN.HORIZONTAL_ALIGN.LEFT,
	contentVerticalAlign : com.doubeye.constant.CSS_ALIGN.VERTICAL_ALIGN.MIDDLE,
	init : function() {
		com.doubeye.dTableColumnDefine.superclass.init.call(this);
		this.resizable = (this.resizable === false ? false : true);
		var config = this.config;
		this.rootElementTypeName = '<th>';
		return this;
	},
	render : function() {
		if (this.parentEl[0].tagName.toLowerCase() != 'tr') {
			throw 'com.doubeye.dTableColumnDefine 的父组件必须是<tr>';
		}
		com.doubeye.dTableColumnDefine.superclass.render.call(this);
		this.rootComponent.addClass('th').attr('colSpan', this.colSpan).attr('rowSpan', this.rowSpan).attr('id', this.id).attr('dataId', this.dataId).attr('width', this.width);
		if (this.hidden) {
			this.rootComponent.hide();
		}
		jQuery('<span>', {
			'class' : 'cellValue',
			html : this.label
		}).appendTo(this.rootComponent);
		this.__renderDraggableEl();
	},
	__renderDraggableEl : function(){
		var thiz = this;
		if (this.resizable) {
			this.rootComponent.addClass('resizable-th');
			this.resizeEl = jQuery('<div>', {
				//html : '||',
				//'class' : 'resizeElement',
				style : 'float:right;width:5px;'
			}).appendTo(this.rootComponent).dblclick(function(){
				thiz.autoAdjustWidth();
			});
		}
	},
	setWidth : function(width) {
		if (width < this.minWidth) {
			width = this.minWidth;
		}
		if (width > this.maxWidth) {
			width = this.maxWidth;
		}
		this.rootComponent.width(width);
	},
	autoAdjustWidth : function() {
		//如果是符合列头，则调用child的autoAdjustWidth，否则进行本列的自动调整
		if (this.childrenObj) {
			for (var i = 0; i < this.childrenObj.length; i ++) {
				this.childrenObj[i].autoAdjustWidth();
			}
		} else {
			var maxWidth = 0, index, width;
			var table = this.rootComponent.parents('table');
			// 寻找table下所有的td和th，找到最长的行
			var ths = table.find('th[dataId="' + this.dataId+ '"]'), cell;
			for (var i = 0; i < ths.length; i ++) {
				cell = jQuery(ths[i]);
				width = com.doubeye.Utils.getStringWidthPixel(cell, cell.html()) + cell.outerWidth() - cell.innerWidth();
				maxWidth = Math.max(maxWidth, width);
			}
			var tds = table.find('td[dataId="' + this.dataId+ '"]'), cell;
			for (var i = 0; i < tds.length; i ++) {
				cell = jQuery(tds[i]);
				width = com.doubeye.Utils.getStringWidthPixel(cell, cell.html()) + cell.outerWidth() - cell.innerWidth();
				maxWidth = Math.max(maxWidth, width);
			}
			this.rootComponent.width(maxWidth);
		}
	}
});
