/**
 * 列式布局
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * @css ../css/dropSite.css
 * 
 * 
 * 构造函数
 * border {Object} 对流式布局容器的border的定义
 * 	color {Color} 定义border的颜色
 * 	line {Line} 定义border的形状
 *  size {Size} 定义线的粗细
 * 		width {integer/percentage} 宽度
 * 		height {integer/percentage} 高度
 * size {Object} 对布局器容器的大小的定义
 * needLabel {boolean} 是否为每个组件生成label，label的值会根据组件的以下属性确定：label>description>name，默认为true
 * labelSeperator {String} 组件的label和组件之间的分隔符，默认为"："
 * margin {Object} 对布局器margin的定义
 * padding {Object} 对布局器padding 的定义
 * columnCount {int} 每行包含列的个数，默认为1
 * items : Array<Object> 组成该布局的所有组件的描述，具体属性如下：
 *  editorClassName : 组件的类名
 *  config (Object) 构造函数属性，具体内容根据类名具体参考
 *  value {Mix} 组件的值
 *  name {String} 组件的名称，该名称将会在getValue()函数被调用时最为值的名称是
 *  description {String} 组件的描述名称，会被生成该组件之前的Label使用
 * @history 
 *  1.0.1
 *   + 对componnet加入一个参数：wholeRow {boolean} 指明在列式布局下自己单独占用一行，此时该组件将被绘制到布局器的最下面，默认为false
 */
com.doubeye.ColumnLayout = com.doubeye.Ext.extend(com.doubeye.FlowLayout, {
	className : 'com.doubeye.ColumnLayout',
	needLabel : true,
	labelSeperator : '：',
	collapsable : false,
	__itemPanel : null,
	__footPanel : null,
	__items : null,
	__itemCountInLine : 0,
	__hiddenEl : null,
	columnCount : 1,
	__currentRowEl : null, 
	init : function() {
		com.doubeye.ColumnLayout.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ColumnLayout.superclass.render.call(this);
		return this;
	},
	__renderItemPanel : function(){
		this.__itemPanel = jQuery('<table>').appendTo(this.rootComponent);
		this.__footPanel = jQuery('<tfoot>').appendTo(this.__itemPanel);
		this.__addHiddenRow();
		this.__addRow();
	},
	addBunchItemsByConfig : function(itemConfigs) {
		if (com.doubeye.Ext.isArray(itemConfigs)) {
			for (var i = 0; i < itemConfigs.length; i ++) {
				this.addItemByConfig(itemConfigs[i]);
			}
		}
	},
	__addHiddenRow : function(){
		var tr = this.__currentRowEl = jQuery('<tr>').appendTo(this.__itemPanel).hide();
		this.__hiddenEl = jQuery('<td>').appendTo(tr);
		var colSpan = this.needLabel ? this.columnCount * 2 : this.columnCount;
		this.__hiddenEl.attr('colSpan', colSpan);
	},
	__addRow : function() {
		this.__itemCountInLine = 0;
		this.__currentRowEl = jQuery('<tr>').appendTo(this.__itemPanel); 
		return this.__currentRowEl;
	},
	__addWholeRow : function(){
		var row = jQuery('<tr>').appendTo(this.__footPanel); 
		return row;
	},
	addItemByConfig : function(itemConfig) {
		var className = itemConfig.editorClassName;
		if (className) {//如果没有组件的名称，则不进行改组件的生成
			
			var cmpTd = jQuery('<td>');
			var config = com.doubeye.Ext.apply(itemConfig, {
				parent : cmpTd
			});
			var cmp = com.doubeye.Utils.getClassInstance(className, config);
			//确定组件绘制到哪个row中的变量，正常情况下使用默认的当前行
			var rowToUse = this.__currentRowEl;
			if (cmp.noPositionInLayout) {
				rowToUse = this.__hiddenEl;
			} else if (cmp.wholeRow) {
				rowToUse = this.__addWholeRow();
			}
			if (!cmp.noPositionInLayout && this.needLabel) {
				this.renderItemLabel(this.getLabelText(itemConfig), rowToUse);
			}
			cmpTd.appendTo(rowToUse);
			if (cmp.wholeRow) {
				var colSpan = this.needLabel ? this.columnCount * 2 - 1 : this.columnCount - 1;
				cmpTd.attr('colSpan', colSpan);
			}
			cmp.init().render();
			this.__items.push(cmp);
			if (rowToUse == this.__currentRowEl)	{
				this.__itemCountInLine ++;
			}
			if (this.__itemCountInLine == this.columnCount) {
				this.__addRow();
			}
			this.__record.setDataComponent(itemConfig.name, cmp);
		}
	},
	addItem : function(component, label) {
		var td = jQuery('<td>');
		var rowToUse = this.__currentRowEl;
		if (component.noPositionInLayout) {
			rowToUse = this.__hiddenEl;
		} else if (component.wholeRow) {
			rowToUse = this.__addWholeRow();
		}
		if (!component.noPositionInLayout && this.needLabel) {
			this.renderItemLabel(this.getLabelText(label), rowToUse);
		}
		td.appendTo(rowToUse);
		if (component.wholeRow) {
			var colSpan = this.needLabel ? this.columnCount * 2 - 1 : this.columnCount - 1;
			td.attr('colSpan', colSpan);
		}
		component.rootComponent.appendTo(td);
		this.__items.push(component);
		if (rowToUse == this.__currentRowEl)	{
			this.__itemCountInLine ++;
		}
		if (this.__itemCountInLine == this.columnCount) {
			this.__addRow();
		}
		this.__record.setDataComponent(component.name, component);
	},
	clear : function(){
		this.__items = [];
		this.__itemPanel.empty();
		this.__addHiddenRow();
		this.__addRow();
	},
	renderItemLabel : function(text, labelParent) {
		var td = jQuery('<td>').appendTo(labelParent);
		jQuery('<label>', {
			text : text
		}).appendTo(td);
	}
});