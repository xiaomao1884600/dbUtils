/**
 * 面板组件
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * @css ../css/dropSite.css
 * 
 * title {String} 标题
 * collapsable ： {boolean} 是否可以折叠，默认为false
 * collapsed : {boolean} 是否显示为折叠状态，默认为false
 * html : {String} 用来填充panel内容的html代码，注意：如果使用html参数，则会覆盖布局器的自有布局
 * layout ： {com.doubeye.FlowLayout或其子类的名称} 布局器,
 * layoutConfig : {Object} 用来配置布局器的参数，具体参数参见布局器说明
 * items : Array<Object> 组成该布局的所有组件的描述，具体属性如下：
  *  editorClassName : 组件的类名
  *  config (Object) 构造函数属性，具体内容根据类名具体参考
  *  value {Mix} 组件的值
  *  name {String} 组件的名称，该名称将会在getValue()函数被调用时最为值的名称是
  *  description {String} 组件的描述名称，会被生成该组件之前的Label使用
 */
com.doubeye.Panel = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.Panel',
	layout : 'com.doubeye.FlowLayout',
	collapsable : false,
	collapsed : false,
	__titlePanel : null,
	__toolsPabel : null,
	__itemPanel : null,
	__layout : null,
	__collapseEl : null,
	init : function() {
		com.doubeye.Panel.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.Panel.superclass.render.call(this);
		this.__renderTitlePanel();
		this.__renderItemPanel();
		this.__createLayout();
		return this;
	},
	__renderTitlePanel : function() {
		this.__titlePanel = jQuery('<div>', {
			'class' : 'ui-widget-header',
			style : 'width:100%;position:relative;',
			html : this.title
		}).appendTo(this.rootComponent).hide();
		this.__renderToolsPanel();
		if (this.collapsable) {
			this.addCollapseSupport();
		}
		if (this.title || this.collapsable) {
			this.__titlePanel.show();
		}
	},
	__renderToolsPanel : function() {
		this.__toolsPanel = jQuery('<div>', {
			'class' : this.classThemePrefix + 'tool ' 
		}).appendTo(this.__titlePanel);
	},
	addCollapseSupport : function(){
		var thiz = this;
		this.__collapseEl = jQuery('<span>', {
			'class' : this.classThemePrefix + 'collapse ' + this.classThemePrefix + 'notCollapsed '
		}).appendTo(this.__toolsPanel).click(function(){
			if (thiz.collapsed) {
				thiz.uncollapse();
			} else {
				thiz.collapse();
			}
		});
	},
	__renderItemPanel : function(){
		this.__itemPanel = jQuery('<div>').appendTo(this.rootComponent);
	},
	__createLayout : function(){
		var config = {
			parent : this.__itemPanel,
			html : this.html
		};
		config = com.doubeye.Ext.apply(config, this.layoutConfig);
		if (!this.layout) {
			this.layout = 'com.doubeye.FlowLayout';
		}
		this.__layout = com.doubeye.Utils.getClassInstance(this.layout, config).init().render();
		if (this.html) {
			this.__layout.rootComponent.html(this.html);
		} else if (com.doubeye.Ext.isArray(this.items)) {
			this.__layout.addBunchItemsByConfig(this.items);
		}
		if (this.collapsed) {
			this.collapse();
		}
	},
	addBunchItemsByConfig : function(itemConfigs) {
		this.__layout.addBunchItemsByConfig(itemConfigs);
	},
	addItemByConfig : function(itemConfig) {
		this.__layout.addItemByConfig(itemConfig);
	},
	addItem : function(component, label) {
		this.__layout.addItem(component, label);
	},
	clear : function() {
		this.__layout.clear();
	},
	collapse : function() {
		this.collapsed = true;
		this.__layout.rootComponent.slideUp();
		this.__collapseEl.removeClass(this.classThemePrefix + 'notCollapsed').addClass(this.classThemePrefix + 'collapsed');
	},
	uncollapse : function() {
		this.collapsed = false;
		this.__layout.rootComponent.slideDown();
		this.__collapseEl.removeClass(this.classThemePrefix + 'collapsed').addClass(this.classThemePrefix + 'notCollapsed');
	},
	getValue : function() {
		return this.__layout.getValue();
	},
	getModifiedValue : function() {
		return this.__layout.getModifiedValue();
	},
	clearValue : function() {
		this.__layout.clearValue();
	}, 
	setValue : function(value) {
		this.__layout.setValue(value);
	},
	getComponent : function(id) {
		return this.__layout.getComponentByName(id);
	},
	/**
	 * 检查panel上的组件是否满足验证js端验证条件，如果所有组件都满足，则返回true，否则返回错误数组
	 * @return  如果所有组件都满足，则返回true，否则返回错误数组
	 * 数组结构如下{label<String>, message<String>} 第一个参数为组件的lable，第二个参数为错误信息
	 */
	checkValidity : function() {
		return this.__layout.checkValidity();
	}
});