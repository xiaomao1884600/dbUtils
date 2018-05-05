/**
 * 流式布局
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
 * items : Array<Object> 组成该布局的所有组件的描述，具体属性如下：
 *  editorClassName : 组件的类名
 *  config (Object) 构造函数属性，具体内容根据类名具体参考
 *  value {Mix} 组件的值
 *  name {String} 组件的名称，该名称将会在getValue()函数被调用时最为值的名称是
 *  description {String} 组件的描述名称，会被生成该组件之前的Label使用
 * 
 * @version 1.0.1
 * @history
 *  1.0.1
 *   + 使用com.doubeye.Record保存面布局器上的值
 */
com.doubeye.FlowLayout = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.FlowLayout',
	needLabel : true,
	labelSeperator : '：',
	collapsable : false,
	__itemPanel : null,
	__items : null,
	__record : null,
	init : function() {
		com.doubeye.FlowLayout.superclass.init.call(this);
		this.__items = [];
		this.__record = new com.doubeye.Record({});
		return this;
	},
	render : function() {
		com.doubeye.FlowLayout.superclass.render.call(this);
		this.__renderItemPanel();
		var size = this.config.size ?  this.config.size : {width : null, height : null};
		this.rootComponent.css('width', size.width ? size.width : '100%');
		if (size.height && height.indexOf('px') >= 0) {
			this.rootComponent.css('height', size.height);
		} else if (size.height && height.indexOf('%') >= 0) {
			$(window).resize( function() {
				//jQuery(rootComponent).height(jQuery(window).height() - jQuery('#functionList').outerHeight() - 1  - jQuery('#foot').outerHeight() - 1 - jQuery('#buttons').outerHeight() - 1 - parseInt(jQuery('#container').css('padding-top'), 10));
			}).resize();
		}
		var border = this.config.border;
		this.addBunchItemsByConfig(this.items);
		return this;
	},
	__renderItemPanel : function(){
		this.__itemPanel = jQuery('<div>').appendTo(this.rootComponent);
	},
	addBunchItemsByConfig : function(itemConfigs) {
		if (com.doubeye.Ext.isArray(itemConfigs)) {
			for (var i = 0; i < itemConfigs.length; i ++) {
				this.addItemByConfig(itemConfigs[i]);
			}
		}
	},
	addItemByConfig : function(itemConfig) {
		var className = itemConfig.editorClassName;
		if (className) {//如果没有组件的名称，则不进行改组件的生成
			var config = com.doubeye.Ext.apply(itemConfig, {
				parent : this.__itemPanel
			});
			var cmp = com.doubeye.Utils.getClassInstance(className, config);
			if (!cmp.noPositionInLayout && this.needLabel) {
				this.renderItemLabel(this.getLabelText(itemConfig), this.__itemPanel);
			}
			cmp.init().render();	
			this.__items.push(cmp);
			//加入record
			this.__record.setDataComponent(itemConfig.name, cmp);	
		}
	},
	addItem : function(component, label) {
		if (!component.noPositionInLayout && this.needLabel) {
			this.renderItemLabel(this.getLabelText(label), this.__itemPanel);
		}
		component.rootComponent.appendTo(this.__itemPanel);
		this.__items.push(component);
		//加入record
		this.__record.setDataComponent(component.name, cmp);
	},
	clear : function(){
		this.__items = [];
		this.__itemPanel.empty();
	},
	getLabelText : function(itemConfig) {
		var label = '';
		if (!itemConfig) {
			return '';
		} else if (com.doubeye.Ext.isString(itemConfig)) {
			label = itemConfig;			
		} else {
			label = itemConfig.label ? itemConfig.label : itemConfig.description;
			if (!label) {
				label = itemConfig.name;
			}
		}
		if (label && this.labelSeperator) {
			label = label + this.labelSeperator;
		}
		return label;
	},
	renderItemLabel : function(text, labelParent) {
		jQuery('<label>', {
			text : text
		}).appendTo(labelParent);
	},
	//加入record主要改了这个方法
	getValue : function(){
		/* 加入record之前的实现
		var value = {}, item;
		for (var i = 0; i < this.__items.length; i ++) {
			item = this.__items[i];
			value[item.name] = item.getValue();
		}
		return value;
		*/
		return this.__record.getValue();
	},
	getModifiedValue : function() {
		return this.__record.getModifiedValueWithOriginalKeyValue();
	},
	getComponentByName : function(name) {
		return com.doubeye.Utils.array.getObjectFromArray({name : name}, this.__items);
	},
	//加入record主要改了这个方法
	setValue : function(value) {
		/*
		var valueFields = com.doubeye.Utils.objectRefactor.getFields(value), fieldName, cmp;
		for (var i = 0; i < valueFields.length; i ++) {
			fieldName = valueFields[i];
			cmp = this.getComponentByName(fieldName);
			if (cmp) {
				cmp.setValue(value[fieldName]);
			}
		}
		*/
		this.__record.setValue(value);
	}, 
	setValueById : function(id, value) {
		
	},
	clearValue : function(){
		for (var i = 0; i < this.__items.length; i ++) {
			this.__items[i].clearValue();
		}
	},
	/**
	 * 检查布局器上的组件是否满足验证js端验证条件，如果所有组件都满足，则返回true，否则返回错误数组
	 * @return  如果所有组件都满足，则返回true，否则返回错误数组
	 * 数组结构如下Array<String>
	 */
	checkValidity : function() {
		var errorMessages = [], valid, cmp;
		for (var i = 0; i < this.__items.length; i ++) {
			cmp = this.__items[i];
			valid = cmp.isValid(cmp.getValue());
			if (valid != '') {
				errorMessages.push(valid);
			}
		}
		if (errorMessages.length == 0) {
			return true;
		} else {
			return errorMessages;
		} 
	}
});