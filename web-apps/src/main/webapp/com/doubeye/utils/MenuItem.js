/*
 * 对jQuery.menu()进行封装的弹出菜单
 * config
 * text {String} 菜单的名称
 * name {String} 菜单的名称id
 * hidden {boolean} 是否隐藏
 * disable {boolean} 是否禁用
 * action {function(event)} 点击事件的回调函数
 * popMenu {com.doubeye.PopMenu} 包含此菜单项的PopMenu
 */
com.doubeye.MenuItem = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.PopMenu',
	disable : false,
	init : function() {
		com.doubeye.MenuItem.superclass.init.call(this);
		this.rootElementTypeName = '<li>';
		this.rootElementCssClass = 'ui-menu-item';
		return this;
	},
	render : function() {
		var thiz = this;
		com.doubeye.MenuItem.superclass.render.call(this);
		jQuery('<a>', {
			href : '#',
			html : this.text
		}).appendTo(this.rootComponent).click(function(event){
			if (thiz.disable) {
				return;
			}
			thiz.action(event);
			if (thiz.popMenu) {
				thiz.popMenu.hide();
			}
		});
		if (this.hidden) {
			this.rootComponent.hide();
		}
		if (this.disable) {
			this.disable();
		}
		return this;
	},
	setDisable : function(){
		this.disable = true;
		this.rootComponent.addClass('ui-state-disabled');
	},
	setEnable : function() {
		this.disable = false;
		this.rootComponent.removeClass('ui-state-disabled');
	},
	show : function() {
		this.rootComponent.show();
	},
	hide : function() {
		this.rootComponent.hide();
	}
});