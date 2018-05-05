/**
 * 列定义详细信息 
 * 
 * config
 * columnType {com.doubeye.databaseManager.ColumnType} 列类型 
 * columnTypeManager {com.doubeye.ColumnTypeMananger}
 * event afterAnyValueChanged(value) 当详细信息改变后触发的事件
 * @author doubeye
 * @version 1.0.0
 */
com.doubeye.ColumnDetailPanel = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ColumnDetailPanel',
	__componentEl : null,
	/**
	 * 类型名称 
	 */
	__typeNameEl : null,
	__universalNameEl : null,
	__ansiNameEl : null,
	__lengthRootEl : null,
	__lengthEl : null,
	__scaleRootEl : null,
	__scaleEl : null,
	__deprecatedEl : null,
	__additionalPropertiesEl : null,
	__additionalPropertiesPanel : null,
	init : function() {
		com.doubeye.ColumnDetailPanel.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ColumnDetailPanel.superclass.render.call(this);
		this.__renderEls();
		if (this.columnType) {
			this.setType(this.columnType);
		}
		var thiz = this;
		this.rootComponent.blur(function(){
			alert('sdfs');
		});
		return this;
	},
	/**
	 * 改变数据类型
 	 * @param {com.doubeye.ColumnType} type 新类型
	 */
	setType : function(type){
		this.clear();
		if (type) {
			var typeName = type.typeName, typeWithInfo = this.columnTypeManager.getTypeByName(typeName);
			this.columnType = type;
			this.__typeNameEl.html(typeWithInfo.typeName);
			this.__universalNameEl.html(typeWithInfo.universalDataTypeName);
			this.__ansiNameEl.html(typeWithInfo.ansiDataTypeName);
			if (typeWithInfo.needLength) {
				this.__lengthRootEl.show();
				this.__lengthEl.setValue(type.length);
			} else {
				this.__lengthRootEl.hide();
			}
			if (typeWithInfo.needScale) {
				this.__scaleRootEl.show();
				this.__scaleEl.setValue(type.scale);
			} else {
				this.__scaleRootEl.hide();
			}
			if (typeWithInfo.deprecated) {
				this.__deprecatedEl.show();
			} else {
				this.__deprecatedEl.hide();
			}
			this.__additionalPropertiesPanel = new com.doubeye.ColumnAdditionalProperty({
				parent : this.__additionalPropertiesEl,
				emptyParentEl : true,
				additionalProperties : typeWithInfo.additionalProperties
			}).init().render();
			this.__additionalPropertiesPanel.setValue(type.additionalProperties);
		}
	},
	__renderEls : function() {
		this.__renderTable();
		this.__renderTypeNameEl();
		this.__renderUniversalNameEl();
		this.__renderAnsiNameEl();
		this.__renderLengthEl();
		this.__renderScaleEl();
		this.__renderDeprecatedEl();
		this.__renderAdditionalPropertiesEl();
	},
	__renderTable : function() {
		this.__tableEl = jQuery('<table>').appendTo(this.rootComponent);
	},
	/**
	 * 类型名称 
	 */
	__renderTypeNameEl : function(){
		var tr = jQuery('<tr>').appendTo(this.__tableEl);
		var th = jQuery('<th>').appendTo(tr);
		jQuery('<label>', {
			html : '数据类型：'
		}).appendTo(th);
		th = jQuery('<th>').appendTo(tr);
		this.__typeNameEl = jQuery('<Label>').appendTo(th);
	},
	/**
	 * 异构数据库统一名称 
	 */
	__renderUniversalNameEl : function(){
		var tr = jQuery('<tr>').appendTo(this.__tableEl);
		var th = jQuery('<th>').appendTo(tr);
		jQuery('<label>', {
			html : '异构数据库统一名称：'
		}).appendTo(th);
		th = jQuery('<th>').appendTo(tr);
		this.__universalNameEl = jQuery('<Label>').appendTo(th);
	},
	/**
	 * ansi名称 
	 */
	__renderAnsiNameEl : function(){
		var tr = jQuery('<tr>').appendTo(this.__tableEl);
		var th = jQuery('<th>').appendTo(tr);
		jQuery('<label>', {
			html : 'ANSI名称：'
		}).appendTo(th);
		th = jQuery('<th>').appendTo(tr);
		this.__ansiNameEl = jQuery('<Label>').appendTo(th);
	},	
	/**
	 * 数据长度 
	 */
	__renderLengthEl : function() {
		var thiz = this;
		this.__lengthRootEl = jQuery('<tr>').appendTo(this.__tableEl).hide();
		var th = jQuery('<th>').appendTo(this.__lengthRootEl);
		jQuery('<label>', {
			html : '长度：'
		}).appendTo(th);
		var td = jQuery('<td>').appendTo(this.__lengthRootEl);
		var nuturalNumberValidator = {
			name : 'positiveInteger',
			reg : com.doubeye.Utils.RegExprestions.number.positiveInteger,
			getInvalidMessage : function() {
				return '数据长度必须是正整数'
			}
		};
		this.__lengthEl = new com.doubeye.TextEdit({
			parent : td,
			valueType : com.doubeye.constant.DATA.DATATYPE.NUMBER,
			onValueChange : function() {
				if (com.doubeye.Ext.isFunction(thiz.afterAnyValueChanged)) {
					thiz.afterAnyValueChanged();
				}
			},
			data : {
				validators : [nuturalNumberValidator],
				validateFunction : function(value){
					var columnType = thiz.columnType;
					if (columnType && parseInt(value, 10) >  columnType.maxLength) {
						return '指定的长度超过了允许范围：1-' + columnType.maxLength;
					}
				}
			}
		}).init().render();
	},
	/**
	 * 数据精度 
	 */
	__renderScaleEl : function() {
		var thiz = this;
		this.__scaleRootEl = jQuery('<tr>').appendTo(this.__tableEl).hide();
		var th = jQuery('<th>').appendTo(this.__scaleRootEl);
		jQuery('<label>', {
			html : '精度：'
		}).appendTo(th);
		var td = jQuery('<td>').appendTo(this.__scaleRootEl);
		var nuturalNumberValidator = {
			name : 'nuturalNumberValidator',
			reg : com.doubeye.Utils.RegExprestions.number.naturalNumber,
			getInvalidMessage : function() {
				return '数据精度必须是自然数';
			}
		};
		this.__scaleEl = new com.doubeye.TextEdit({
			parent : td,
			valueType : com.doubeye.constant.DATA.DATATYPE.NUMBER,
			onValueChange : function() {
				if (com.doubeye.Ext.isFunction(thiz.afterAnyValueChanged)) {
					thiz.afterAnyValueChanged();
				}
			},
			data : {
				validators : [nuturalNumberValidator],
				validateFunction : function(value){
					var columnType = thiz.columnType;
					if (columnType && parseInt(value, 10) > columnType.maxScale) {
						return '指定的精度超过了允许范围：0-' + columnType.maxScale;
					}
				}
			}
		}).init().render();
	},
	/**
	 * 是否不赞成使用 
	 */
	__renderDeprecatedEl : function() {
		this.__deprecatedEl = jQuery('<tr>').appendTo(this.__tableEl).hide();
		var th = jQuery('<th>', {
			colSpan : 2
		}).appendTo(this.__deprecatedEl);
		jQuery('<label>', {
			style : 'color:red;',
			html : '该数据类型已不赞成使用'
		}).appendTo(th);
	},
	__renderAdditionalPropertiesEl : function() {
		this.__additionalPropertiesEl = jQuery('<div>').appendTo(this.rootComponent);
	},
	/**
	 * 清除面板的信息 
	 */
	clear : function() {
		this.__typeNameEl.html('');
		this.__universalNameEl.html('');
		this.__ansiNameEl.html('');
		this.__lengthEl.setValue('');
		this.__scaleEl.setValue('');
	},
	/**
	 * 与getValue()的区别在于，不影响this.columnType的值，仅从面板上读取数据
	 */
	getValueFromPanel : function() {
		var typeName = this.columnType.typeName;
		var value = {typeName : typeName};
		var typeWithInfo = this.columnTypeManager.getTypeByName(typeName);
		value.javaClassName = typeWithInfo.javaClassName;
		var errMsg = this.getInvalidMessages();
		if (errMsg.length > 0){
			this.showErrorMessages(errMsg);
			return null;
		}
		if (typeWithInfo.needLength) {
			value.length = this.__lengthEl.getValue();
		}
		if (typeWithInfo.needScale) {
			value.scale = this.__scaleEl.getValue();
		}
		//扩展属性
		var additionalProperties = this.__additionalPropertiesPanel.getValue();
		value.additionalProperties = additionalProperties;
		return value;
	},
	getValue : function() {
		var columnType = this.columnType;
		if (columnType) {
			var typeWithInfo = this.columnTypeManager.getTypeByName(columnType.typeName);
			columnType.javaClassName = typeWithInfo.javaClassName;
			var errMsg = this.getInvalidMessages();
			if (errMsg.length > 0){
				this.showErrorMessages(errMsg);
				return null;
			}
			if (typeWithInfo.needLength) {
				columnType.length = this.__lengthEl.getValue();
			} else {
				delete columnType.length;
			}
			if (typeWithInfo.needScale) {
				columnType.scale = this.__scaleEl.getValue();
			} else {
				delete columnType.scale;
			}
			//扩展属性
			var additionalProperties = this.__additionalPropertiesPanel.getValue();
			columnType.additionalProperties = additionalProperties;
			return columnType;
		} else {
			return null;
		}
	},
	setLength : function(length) {
		this.__lengthEl.setValue(length);
	},
	setScale : function(scale) {
		this.__scaleEl.setValue(scale);
	},
	getInvalidMessages : function() {
		var errMessages = [], type = this.columnType;
		if (!this.columnType) {
			return errMessages;
		}
		if (type.needLength) {
			var lengthErr = this.__lengthEl.isValid(this.__lengthEl.getValue());
			if (lengthErr != '') {
				errMessages.push(lengthErr);
			}
		}
		if (type.needScale) {
			var scaleErr = this.__scaleEl.isValid(this.__scaleEl.getValue());
			if (scaleErr != '') {
				errMessages.push(scaleErr);
			}
		}
		return errMessages;
	},
	showErrorMessages : function(errMsg) {
		var messages = "";
		for (var i = 0; i < errMsg.length; i ++) {
			messages += errMsg[i] + "\r\n";
		}
		alert(messages);
	}
});
