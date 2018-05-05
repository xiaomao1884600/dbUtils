/**
 * 详细border属性设置器，可以将上下左右四个border分别设置
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * @css ../css/dropSite.css
 * 
 * 
 * 构造函数
 * component {com.doubeye.Component} 设置改变属性时影响的组件
 */
com.doubeye.DetailBorderHelper = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.DetailBorderHelper',
	/**
	 * 控制当前BoderHelper对哪个border起作用的Radio 
	 */
	topRadioEl : null,
	leftRadioEl : null,
	rightRadioEl : null,
	bottomRadioEl : null,
	/**
	 * 定义当前的position 
	 */
	currentPosition : 'border-top',
	value : null,
	/**
	 * border设置助手 
	 */
	borderHelper : null,
	init : function() {
		this.value = {};
		com.doubeye.DetailBorderHelper.superclass.init.call(this);
		var config = this.config;
		this.component = config.component ? config.component : this;
	},
	render : function() {
		com.doubeye.DetailBorderHelper.superclass.render.call(this);
		this.renderTable();
		this.fromValue(this.value ? this.value : {});
		
		var thiz= this;
		jQuery('<input>', {
			type : 'button',
			value : 'test'
		}).appendTo(this.rootComponent).click(function(){
			alert(com.doubeye.Ext.encode(thiz.getValue()));
		});
	},
	renderTable : function(){
		var thiz = this;
		var table = jQuery('<table>', {
			'class' : this.classThemePrefix + 'table'
		}).appendTo(this.rootComponent);
		//top
		var tr = jQuery('<tr>').appendTo(table);
		var td = jQuery('<td>', {
			'class' : this.classThemePrefix + 'top_bottom_radio',
			colSpan : 3
		}).appendTo(tr);
		this.topRadioEl = jQuery('<input>', {
			type : 'radio',
			name : 'position'
		}).appendTo(td).change(function(){
			thiz.onBorderRadioChanged('border-top');
		});
		//left and right
		tr = jQuery('<tr>').appendTo(table);
		td = jQuery('<td>').appendTo(tr);
		this.leftRadioEl = jQuery('<input>', {
			type : 'radio',
			name : 'position'
		}).appendTo(td).change(function(){
			thiz.onBorderRadioChanged('border-left');
		});
		td = jQuery('<td>').appendTo(tr);
		this.borderHelper = new com.doubeye.BorderHelper({
			parent : td,
			onChange : function() {
				thiz.value[thiz.currentPosition] = thiz.borderHelper.getValue();
			}
		});
		this.borderHelper.init();
		this.borderHelper.render();
		td = jQuery('<td>').appendTo(tr);
		this.rightRadioEl = jQuery('<input>', {
			type : 'radio',
			name : 'position'
		}).appendTo(td).change(function(){
			thiz.onBorderRadioChanged('border-right');
		});
		//bottom
		tr = jQuery('<tr>').appendTo(table);
		td = jQuery('<td>', {
			'class' : this.classThemePrefix + 'top_bottom_radio',
			colSpan : 3
		}).appendTo(tr);
		this.rightRadioEl = jQuery('<input>', {
			type : 'radio',
			name : 'position'
		}).appendTo(td).change(function(){
			thiz.onBorderRadioChanged('border-bottom');
		});
	},
	getValue : function() {
		this.value[this.currentPosition] = this.borderHelper.getValue();
		return this.value;
	},
	onBorderRadioChanged : function(position) {
		this.value[this.currentPosition] = this.borderHelper.getValue();
		this.borderHelper.setCssPropertyName(position, this.value[position] ? this.value[position] : '');
		this.currentPosition = position;
	},
	fromValue : function(value) {
		if (com.doubeye.Ext.isString()) {
			value = com.doubeye.Ext.decode(value);
		}
		this.value = value;
		this.topRadioEl.attr('checked', 'checked');
		this.borderHelper.setCssPropertyName('border-top', this.value['border-top'] ? this.value['border-top'] : '');
		this.borderHelper.setCssPropertyName('border-right', this.value['border-right'] ? this.value['border-right'] : '');
		this.borderHelper.setCssPropertyName('border-bottom', this.value['border-bottom'] ? this.value['border-bottom'] : '');
		this.borderHelper.setCssPropertyName('border-left', this.value['border-left'] ? this.value['border-left'] : '');
	}
});