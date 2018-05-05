/**
 * 列扩展属性定义面板 
 * 
 * config
 * additionalProperties {Array{Object}} 扩展属性数组
 * @version 1.0.0
 */
com.doubeye.ColumnAdditionalProperty = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ColumnAdditionalProperty',
	rootElementTypeName : '<table>',
	additionalProperties : null,
	init : function() {
		this.additionalProperties = [];
		com.doubeye.ColumnAdditionalProperty.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ColumnAdditionalProperty.superclass.render.call(this);
		this.__renderProperties();
		return this;
	},
	__renderProperties : function() {
		var property;
		for (var i = 0; i < this.additionalProperties.length; i ++){
			property = this.additionalProperties[i];
			this.__renderProperty(property);
		}
	},
	__renderProperty : function(property) {
		var tr = jQuery('<tr>').appendTo(this.rootComponent);
		tr.data('property', property);
		var componentName = property.editorClassName;
		var desc = property.description;
		var th = jQuery('<th>', {
			html : desc + '：'
		}).appendTo(tr);
		var td = jQuery('<td>').appendTo(tr);
		var config = com.doubeye.Ext.apply({
			parent : td
		}, com.doubeye.Ext.decode(property.editorConfig));
		var cmp = com.doubeye.Utils.getClassInstance(componentName, config).init().render();
		property.valueEl = cmp;
	},
	getValue : function() {
		var result = {}, property;
		for (var i = 0; i < this.additionalProperties.length; i ++){
			property = this.additionalProperties[i];
			result[property.name] = property.valueEl.getValue();
		}
		return result;
	},
	setValue : function(value) {
		var propertyNames = com.doubeye.Utils.objectRefactor.getFields(value), propertyName, property;
		for (var i = 0; i < propertyNames.length; i ++) {
			propertyName = propertyNames[i];
			var property = com.doubeye.Utils.array.getObjectFromArray({name : propertyName}, this.additionalProperties);
			if (property && property.valueEl && com.doubeye.Ext.isFunction(property.valueEl.setValue)) {
				property.valueEl.setValue(value[propertyName]);
			}
		}
	}
});
