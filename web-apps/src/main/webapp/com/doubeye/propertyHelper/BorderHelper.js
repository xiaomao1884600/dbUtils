/**
 * 组件border属性设置器
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * @css ../css/dropSite.css
 * 
 * 
 * 构造函数
 * component {com.doubeye.Component} 设置改变属性时影响的组件
 * onChange {function(value)} 组件值改变事件
 */
com.doubeye.BorderHelper = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.BorderHelper',
	cssPropertyName : 'border',
	/**
	 * Border Style 可视化下拉列表
	 */
	styleEl : null,
	/**
	 * 颜色选择文本框 
	 */
	colorEl : null,
	/**
	 * 相对宽度raidio 
	 */
	radioRelativeEl : null,
	/**
	 * 像素宽度raidio 
	 */
	radioPxEl : null,
	/**
	 * 相对宽度下拉列表 
	 */
	relativeWidthSelectEl : null,
	/**
	 * 绝对款对文本框 
	 */
	pxInputEl : null,
	/**
	 * Border 的轮廓样式 
	 */
	cssEl : null,
	/**
	 * Border宽度的单位
	 */
	absoluteWithUnitEl : null,
	init : function() {
		com.doubeye.BorderHelper.superclass.init.call(this);
		var config = this.config;
		this.component = config.component ? config.component : this;
		return this;
	},
	render : function() {
		com.doubeye.BorderHelper.superclass.render.call(this);
		this.renderColorPicker();
		this.renderBorderWidthPanel();
		this.renderBorderStylePanel();
		this.renderStylePanel();
		this.setEvent();
		this.fromValue(this.config.value);
		return this;
	},
	renderColorPicker : function() {
		var thiz = this;
		var rootComponent = this.rootComponent;
		var el = jQuery('<div>').appendTo(rootComponent);
		jQuery('<label>', {
			text : '请选择所需的颜色：',
			'class' : 'com-doubeye-default-float-left'
		}).appendTo(el);
		this.colorEl = jQuery('<input>', {
			'class' : 'com-doubeye-default-float-left'
		}).appendTo(el);
		this.colorPikerEl = jQuery('<div>', {
			'class' : 'com-doubeye-default-float-left',
			style : 'background: url(../../lib/jQuery/colorPicker/images/select2.png) center;'
		}).appendTo(el).width(this.colorEl.height()).height(this.colorEl.height());
		var color = this.colorPikerEl.val();
		this.colorPikerEl.ColorPicker({
			color : color ? color : '#ffffff',
			onShow : function(colpkr) {
				$(colpkr).fadeIn(500);
				return false;
			},
			onHide : function(colpkr) {
				$(colpkr).fadeOut(500);
				return false;
			},
			onChange : function(hsb, hex, rgb) {
				thiz.colorPikerEl.css('backgroundColor', '#' + hex);
				thiz.colorEl.val('#' + hex).trigger('change');
			},
			onSubmit: function(hsb, hex, rgb, el) {
				$(el).val(hex);
				thiz.colorPikerEl.css('backgroundColor', '#' + hex);
				$(el).ColorPickerHide();
				thiz.colorEl.val('#' + hex);
			}
		});
		jQuery('<div>', {
			'class' : 'com-doubeye-default-clear-float'
		}).appendTo(el);
	},
	renderBorderWidthPanel : function() {
		var thiz = this;
		var rootComponent = this.rootComponent;
		var el = jQuery('<div>').appendTo(rootComponent);
		jQuery('<label>', {
			text : '请选择边框的宽度：'
		}).appendTo(el);
		var relative = jQuery('<div>').appendTo(el);
		this.radioRelativeEl = jQuery('<input>', {
			type : 'radio',
			name : 'widthType'
		}).appendTo(relative);
		jQuery('<label>', {
			text : '相对值：'
		}).appendTo(relative);
		this.relativeWidthSelectEl = jQuery('<select>').appendTo(relative);
		for (var i = 0; i < com.doubeye.BorderHelper.relateiveWidth.length; i ++) {
			jQuery('<option>', com.doubeye.BorderHelper.relateiveWidth[i]).appendTo(this.relativeWidthSelectEl);
		}
		
		relative = jQuery('<div>').appendTo(el);
		this.radioPxEl = jQuery('<input>', {
			type : 'radio',
			name : 'widthType'
		}).appendTo(relative);
		jQuery('<label>', {
			text : '像素：'
		}).appendTo(relative);
		this.pxInputEl = jQuery('<input>', {
			
		}).appendTo(relative);
		this.absoluteWithUnitEl = jQuery('<select>').appendTo(relative);
		var units;
		for (var i = 0; i < com.doubeye.BorderHelper.absoluteWidthUnits.length; i ++) {
			unit = com.doubeye.BorderHelper.absoluteWidthUnits[i];
			jQuery('<option>', unit).appendTo(this.absoluteWithUnitEl);
		}
	},
	renderBorderStylePanel : function() {
		var thiz = this;
		var el = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<label>', {
			text : '请选择边框的样式：',
			'class' : 'com-doubeye-default-float-left'
		}).appendTo(el);
		var panel = jQuery('<span>').appendTo(el);
		jQuery('<div>', {
			'class' : 'com-doubeye-default-clear-float'
		}).appendTo(el);
		this.styleEl = new com.doubeye.BorderSelector({
			parent : panel,
			change : function(value) {
				thiz.cssEl.val(thiz.getValue()).change();
				if (thiz.onChange) {
					thiz.onChange(value);
				}
			}
		});
		this.styleEl.init();
		this.styleEl.render();
	},
	renderStylePanel : function() {
		var thiz = this;
		var el = jQuery('<div>').appendTo(this.rootComponent);
		jQuery('<label>', {
			text : 'CSS：'
		}).appendTo(el);
		this.cssEl = jQuery('<input>', {
			style : 'width:80%'
		}).appendTo(el).change(function(){
			thiz.component.rootComponent.css(thiz.cssPropertyName, jQuery(this).val());
			thiz.fromValue(jQuery(this).val(), false);
		});
	},
	getValue : function() {
		var width;
		if (this.radioRelativeEl.attr('checked') == 'checked') {
			width = this.relativeWidthSelectEl.val();
		} else if (this.radioPxEl.attr('checked') == 'checked') {
			width = this.pxInputEl.val() + this.absoluteWithUnitEl.val();
		}
		return width + ' ' + this.styleEl.getValue() + ' ' + this.colorEl.val();
	},
	setEvent : function(){
		var thiz = this;
		this.colorEl.change(function() {
			thiz.cssEl.val(thiz.getValue()).change();
		}).keydown(function(event) {
			return com.doubeye.Utils.RegExprestions.hexString.test(String.fromCharCode(event.keyCode)) || event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39;
		});
		this.radioRelativeEl.change(function() {
			thiz.relativeOrAbsoluteWidth('relative');
			thiz.cssEl.val(thiz.getValue()).change();
		});
		this.relativeWidthSelectEl.change(function(){
			thiz.cssEl.val(thiz.getValue()).change();
		});
		this.radioPxEl.change(function() {
			thiz.relativeOrAbsoluteWidth('absolute');
			thiz.cssEl.val(thiz.getValue()).change();
		});
		this.pxInputEl.change(function() {
			thiz.cssEl.val(thiz.getValue()).change();
		});
		this.absoluteWithUnitEl.change(function() {
			thiz.cssEl.val(thiz.getValue()).change();
		});
		this.radioRelativeEl.attr('checked', 'checked').change();
	},
	fromValue : function(value, triggerEvent) {
		if (triggerEvent == undefined) {
			triggerEvent = true;
		}
		var thiz = this;
		if (!value || value == '') {
			value = 'none';
		} else {
			value = value.trim();
		}
		//颜色
		if (value.indexOf('#') >= 0) {
			var color = value.substr(value.indexOf('#'), 7);
			this.colorEl.val(color);
			thiz.colorPikerEl.css('backgroundColor', color);
		}
		//宽度

		//相对宽度
		var findWidth = false;
		var width;
		for (var i = 0; i < com.doubeye.BorderHelper.relateiveWidth.length; i ++) {
			width = com.doubeye.BorderHelper.relateiveWidth[i].value;
			if (value.indexOf(width) >= 0) {
				this.relativeWidthSelectEl.val(width);
				this.radioRelativeEl.attr('checked', 'checked');
				this.relativeOrAbsoluteWidth('relative');
				findWidth = true;
				break;
			}
		}
		if (!findWidth) {
			var unit, unitIndex, lastBlankIndex = 0;
			for (var i = 0; i < com.doubeye.BorderHelper.absoluteWidthUnits.length; i ++) {
				unit = com.doubeye.BorderHelper.absoluteWidthUnits[i].value;
				unitIndex = value.indexOf(unit);
				if (unitIndex >= 0) {
					var firstBlankspaceIndex = value.indexOf(' ');
					if (unitIndex < firstBlankspaceIndex) {
						width = value.substring(0, firstBlankspaceIndex - 2).trim();
					} else {
						width = com.doubeye.Utils.String.getStringJustBeforeAndBetween(value, unit, ' ');
					}
					this.pxInputEl.val(width);
					this.relativeWidthSelectEl.val(unit);
					this.radioPxEl.attr('checked', 'checked');
					this.relativeOrAbsoluteWidth('absolute');
					break;
				}
			}
		}
		// 轮廓
		var border;
		for (var i = 0; i < this.styleEl.styles.length; i ++) {
			border = this.styleEl.styles[i].value;
			if (value.indexOf(border) >= 0) {
				this.styleEl.fromValue(border);
				break;
			}
		}
		if (triggerEvent === true) {
			thiz.cssEl.val(thiz.getValue()).change();	
		}
	},
	relativeOrAbsoluteWidth : function(value) {
		var selected = value == 'relative';
		this.relativeWidthSelectEl.attr('disabled', !selected);
		this.pxInputEl.attr('disabled', selected);
		this.absoluteWithUnitEl.attr('disabled', selected);
	},
	setCssPropertyName : function(name, value) {
		this.cssPropertyName = name;
		if (value != undefined) {
			this.fromValue(value);
		}
	}
});
com.doubeye.BorderHelper.relateiveWidth = [{value : 'medium', text : '普通'}, {value : 'thin', text : '较细'}, {value : 'thick', text : '较粗'}];
com.doubeye.BorderHelper.absoluteWidthUnits = [{value : 'px', text : 'px'}, {value : 'mm', text : 'mm'}, {value : 'cm', text : 'cm'}, {value : 'pt', text : 'pt'}, {value : 'pc', text : 'pc'}, {value : 'in', text : 'in'}];
