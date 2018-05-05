/**
 * 组件的基类
 * @author doubeye
 * @version 2.0.1
 * 构造函数
 * @param {Object} config
 * 	parent {Object} : 父容器,目前支持html的id, jQuery对象, Extjs对象，或者为'@@onlyDialog@@'，当
 *  parentId {String} : DEPRECATED 父容器Id,为兼容1.x组件设置，2.x组件请使用parent属性指定父容器
 *  componentId {String} : 内部element的id的前缀
 *  themePrefix {String} : 主题前缀
 *  rootElementTypeName {String} : 组件的根元素的类型，默认为<div>
 *  rootElementCssClass {String} : 组件的根元素的样式类，默认为空
 *  rootElementStyle {String} : 组件的根元素style,默认为空
 *  emptyParentEl {Boolean} : 是否清楚父容器，默认为不清除
 *  width {int} : 组件的宽度，单位为像素
 *  disabled {boolean} 是否为不可用状态，默认为false
 * event
 *  afterRendering : function() 当绘制完成后调用
 * @history 
 * 	2.0.1 + parent参数可以使用'@@onlyDialog@@',表示组件仅通过产出方式打开，不需要有parentEl, 同时增加openDialog(config)方法，用来将组件打开在dialog中，改方法中的config与jQueryUI中的dialog方法的参数一至
 */
com.doubeye.Component = function(config){
	this.config = config ? config : {};
	this.config.renderTo = config.parentId;
	//this.parent = config.parent;
	/**
	 * 容器的id
	 */
	this.id = this.config.componentId ? this.config.componentId : com.doubeye.Ext.id();
	/**
	 * 组件内元素的id前缀
	 */
	this.rootId = this.id + '_';
	/**
	 * 表格编辑器的根面板 
	 */
	this.rootComponent;
	/**
	 * 主题前缀
	 */
	this.themePrefix = config.themePrefix ? config.themePrefix : 'com-doubeye-default-';
	/**
	 * 类主题前缀 
	 */
	this.classThemePrefix = this.className.replace('com.doubeye.', 'com-doubeye-default-') + '-';
	/**
	 * 是否处于编辑模式
	 */
	this.isEditing = (config.isEditing || config.isEditing === false) ? config.isEditing : true;
	/**
	 * 组件的属性编辑器
	 */
	this.propertyEditor;

	//this. = config. ? config.rootElementCssClass + ' ' : ' ';
	/**
	 * 组件改变大小时需要通知的对象
	 */
	this.resizeNotifyComponents = [];
	com.doubeye.Ext.apply(this, config);
};

com.doubeye.Component.prototype = {
	author : 'doubeye',
	version : '1.0.0',
	description : '组件的基类',
	/**
	 * 组件在布局器中的位置无关，一般隐藏式组件将此值设置为true，该组件不在布局器中显示布局，也不会影响其他组件的位置
	 */
	noPositionInLayout : false,
	/**
	 * 所有子类都需要覆盖此属性 
	 */
	className : 'com.doubeye.Component',
	/**
	 * 组件的根元素类型，默认为div
	 */
	rootElementTypeName : '<div>',
	/**
	 * 组件在编辑状态下的4个停靠点
	 */
	topDropSite : null,
	leftDropSite : null,
	rightDropSite : null,
	bottomDropSite : null,
	/**
	 * 初始化面板(需要基类复写)
	 */
	init : function(){
		var thiz = this;
		if (!this.parent) {
			alert(this.className + ':' + com.doubeye.Ext.encode(this.config));
			throw '请在config里指定父容器,目前支持html的id, jQuery对象, Extjs对象, 或者@@onlyDialog@@';
		}
		if (typeof(this.parent)=="string") {
			if (this.parent == '@@onlyDialog@@') {
				this.parentEl = null;				
			} else if (this.parent.substr(0,1) == '#') {
				this.parentEl = jQuery(this.parent);
			} else {
				this.parentEl = jQuery('#' + this.parent);
			}
		} else if (this.parent.jquery) {
			this.parentEl = this.parent;
		} else if (this.parent.ctype.indexOf('Ext.') == 0) {
			this.parentEl = jQuery(this.parent.body ? this.parent.body.dom : (this.parent.container ? this.parent.container.dom : ''));
		}
		if (this.parentEl) {	
			this.parentEl.resize(function(e){
				thiz.resize(e);
			});
		}
		if (this.emptyParentEl && this.parent != '@@onlyDialog@@'){
			this.parentEl.empty();
		}
		com.doubeye.Ext.apply(this, this.config);
	},
	/**
	 * 设置组件的大小
	 * @param {Object} width 宽度
	 * @param {Object} height 高度
	 */
	setSize : function(width, height){
		if (width) {
			this.rootComponent.width(width);
		}
		if (height) {
			this.rootComponent.height(height);
		}
		if (width || height) {
			this.resize();
		}
	},
	/**
	 * 改变大小的事件(需要基类复写)
	 */
	resize : function(){
		jQuery.each(this.resizeNotifyComponents, function(){
			this.trigger('resize');
		});
	},
	/**
	 * 显示属性编辑器编辑(需要基类复写)
	 */
	showEditor : function(){
		
	},
	/**
	 * 绘制组件(需要基类复写)
	 */
	render : function(){
		this.renderRootComponent();
	},
	/**
	 * 加入编辑支持(需要基类复写)
	 */
	addEditSupport : function(){
		
	},
	/**
	 * 进入编辑模式(需要基类复写)
	 */
	editing : function(){
		this.parentEl.find('.DropSite').removeClass(this.themePrefix + 'hidden');
	},
	/**
	 * 进入预览模式(需要基类复写)
	 */
	preview : function(){
		this.parentEl.find('.DropSite').addClass(this.themePrefix + 'hidden');
	},
	/**
	 * 向数据库请求数据(需要基类复写)
	 */
	getData : function(){
		
	},
	/**
	 * 绘制根div和编辑状态下的dropSites
	 * @memberOf {TypeName} 
	 */
	renderRootComponent : function(addtionalConfig){
		var config = {
			id : this.id
		};
		if (this.rootElementCssClass) {
			config['class'] = this.rootElementCssClass;
		}
		if (this.rootElementStyle) {
			config.style = this.rootElementStyle;
		}
		config = com.doubeye.Ext.apply(config, addtionalConfig);
		//根元素
		this.rootComponent = jQuery(this.rootElementTypeName, config);
		if (this.parent != '@@onlyDialog@@') {
			this.rootComponent.appendTo(this.parentEl);
		}
		if (this.config.width) {
			this.rootComponent.css('width', this.config.width);
		}
		if (this.config.readOnly == true) {
			this.rootComponent.attr('readonly', 'true');
		}
	},
	/**
	 * 保存组件的设置参数(需要基类复写)
	 */
	getSaveConfig : function(){
		var saveConfig = {};		
		//容器的样式		
		var parentCssClass = '';
		var parent = this.parentEl.parent()[0];
		if ((parent && jQuery(parent).hasClass(com.doubeye.DropSite.leftVoidClass)) || this.parentEl.hasClass(com.doubeye.DropSite.leftVoidClass)){
			parentCssClass = jQuery(parent).attr('class');
		} else {
			parentCssClass = this.parentEl.attr('class');
		}
		saveConfig.parentCssClass = parentCssClass;
		saveConfig.themePrefix = this.themePrefix;
		return saveConfig;
	},
	openDialog : function(config){
		this.rootComponent.dialog(config);
		return this;
	},
	/**
	 * 关闭对话框 
 	 * @param {Boolean} destroy 是否销毁对话框
	 */
	closeDialog : function(destroy) {
		if (destroy) {
			this.rootComponent.dialog('destroy');
		} else {
			this.rootComponent.dialog('close');
		}
	},
	/**
	 * 隐藏组件 
	 */
	hide : function(){
		this.rootComponent.hide();
	},
	/**
	 * 显示组件 
	 */
	show : function(){
		this.rootComponent.show();
	},
	clearValue : function() {
		
	},
	disable : function() {
		this.rootComponent.attr('disabled', 'true');
	},
	enable : function() {
		this.rootComponent.removeAttr('disabled');
	},
	afterRendering : function() {
		if (this.disabled) {
			this.disable();
		}
	}
};