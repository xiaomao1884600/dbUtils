/**
 * 元素css属性的border选择器
 * explorerCompatibility ： {
 * 	minIEVersion {integer} IE最低支持版本
 *  minFirefoxVersion {integer} firefox最低支持版本
 * }
 * change : function() 当选择的值改变时的回调函数
 * getValue : function() 获取选中值
 */
com.doubeye.BorderSelector = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.BorderSelector',
	/**
	 * 下拉列表组件 
	 */
	dropdownEl : null,
	/**
	 * 定义所有的style的变量
	 */
	styles : null,
	explorerCompatibility : null,
	init : function() {
		this.styles = [];
		com.doubeye.BorderSelector.superclass.init.call(this);
		com.doubeye.Ext.apply(this, this.config.explorerCompatibility, {
			explorerCompatibility : {
				minIEVersion : 0,
				minFirefoxVersion : 0
			}
		});
		this.change = (this.config.change && typeof(this.config.change == 'funtion')) ? this.config.change : function(){};

		var none = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_none.jpg',
			description : 'border-color与border-width将被忽略',
			value : 'none',
			text : 'none - 无轮廓 '
		};
		var hidden = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_hidden.jpg',
			description : 'IE7及以下不支持',
			value : 'hidden',
			text : 'hidden - 隐藏边框'
		};
		var dotted = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_dotted.jpg',
			description : 'IE6下显示为dashed效果',
			value : 'dotted',
			text : 'dotted - 点状轮廓'
		};
		var dashed = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_dashed.jpg',
			description : '',
			value : 'dashed',
			text : 'dashed - 虚线轮廓'
		};
		var solid = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_solid.jpg',
			description : '',
			value : 'solid',
			text : 'solid - 实线轮廓'
		};
		var double = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_double.jpg',
			description : '两条单线与其间隔的和等于指定的border-width值',
			value : 'double',
			text : 'double - 双线轮廓'
		};
		var groove = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_groove.jpg',
			description : '',
			value : 'groove',
			text : 'groove - 3D凹槽轮廓'
		};
		var ridge = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_ridge.jpg',
			description : '',
			value : 'ridge',
			text : 'ridge - 3D凸槽轮廓'
		};
		var inset = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_inset.jpg',
			description : '',
			value : 'inset',
			text : 'inset - 3D凹边轮廓'
		};
		var outset = {
			image : '../../com/doubeye/utils/Component/BorderSelector/images/borderStyle_outset.jpg',
			description : '',
			value : 'outset',
			text : 'outset - 3D凸槽轮廓'
		};
		this.styles.push(none);
		var explorerCompatibility = this.explorerCompatibility;
		if (explorerCompatibility.minIEVersion > 7) {
			this.styles.push(hidden);
		}
		if (explorerCompatibility.minFirefoxVersion > 6) {
			this.styles.push(dotted);
		}
		this.styles.push(dashed, solid, double, groove, ridge, inset, outset);
	},
	render : function() {
		com.doubeye.BorderSelector.superclass.render.call(this);
		var thiz = this;
		this.dropdownEl = this.rootComponent.msDropDown({
			byJson : {
				data : this.styles,
				name : 'borderStyle'
			},
			on : {
				change : function(data, ui) {
					thiz.change(data.value);
				}
			}
		}).data("dd");
	},
	getValue : function() {
		return this.dropdownEl.getData().data.value;
	},
	fromValue : function(value) {
		this.dropdownEl.setIndexByValue(value);
	}
}); 