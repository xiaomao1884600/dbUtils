/*
 * 对jQuery.menu()进行封装的弹出菜单
 * config
 * menuItems {Array(com.doubeye.MenuItem)} 菜单项，菜单项不需要指定parent属性
 * left {int} 菜单出现的横坐标，如果不指定，则出现在鼠标指针位置
 * top {int} 菜单出现的纵坐标，如果不知道，则出现在鼠标指针位置
 * autoHide {boolean} 初始时为隐藏，默认为true，当需要显示时，请调用show()函数
 */
com.doubeye.PopMenu = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.PopMenu',
	__menuItems : null,
	autoHide : true,
	init : function() {
		this.menuItems = [];
		this.__menuItems = [];
		com.doubeye.PopMenu.superclass.init.call(this);
		this.rootElementTypeName = '<ul>';
		this.rootElementCssClass = this.classThemePrefix + 'root';
		return this;
	},
	render : function() {
		com.doubeye.PopMenu.superclass.render.call(this);
		this.rootComponent.menu();
		if (this.autoHide) {
			this.rootComponent.hide();
		}
		this.__renderMenuItems();
		return this;
	},
	__renderMenuItems : function() {
		var config, item;
		for (var i = 0; i < this.menuItems.length; i ++) {
			config = this.menuItems[i];
			this.addMenuItem(config);
		}
	},
	show : function(left, top) {
		if (isNaN(left)) {
			if (isNaN(this.left)) {
				left = 0;
			} else {
				left = this.left;
			}
		}
		if (isNaN(top)) {
			if (isNaN(this.top)) {
				top = 0;
			} else {
				top = this.top;
			}
		}
		this.rootComponent.css({top : top + 'px', left : left + 'px'}).show();
	},
	hide : function() {
		this.rootComponent.hide();
	},
	/**
	 * 增加一个菜单项
     * @param {com.doubeye.MenuItem} menuItem
	 */
	addMenuItem : function(menuItemConfig) {
		var config = com.doubeye.Ext.apply(menuItemConfig, {
			parent : this.rootComponent,
			popMenu : this
		});
		var item = new com.doubeye.MenuItem(config).init();
		item.render();
		this.__menuItems.push(item);
	},
	/**
	 * 隐藏指定的菜单
 	 * @param {String} menuName 菜单名
	 */
	hideMenuItem : function(menuName) {
		var menu = this.findMenuItemByName(menuName);
		if (menu) {
			menu.hide();
		}
	},
	/**
	 * 显示指定的菜单
 	 * @param {String} menuName 菜单名
	 */	
	showMenuItem : function(menuName) {
		var menu = this.findMenuItemByName(menuName);
		if (menu) {
			menu.show();
		}		
	},
	/**
	 * 禁用指定的菜单
 	 * @param {String} menuName 菜单名
	 */	
	disableMenuItem : function(menuName) {
		var menu = this.findMenuItemByName(menuName);
		if (menu) {
			menu.setDisable();
		}			
	},
	/**
	 * 启用指定的菜单
 	 * @param {String} menuName 菜单名
	 */	
	enableMenuItem : function(menuName) {
		var menu = this.findMenuItemByName(menuName);
		if (menu) {
			menu.setEnable();
		}		
	},
	/**
	 * 根据名称寻找菜单
 	 * @param {String} menuName 菜单名
 	 * @return {com.doubeye.MenuItem} 拥有该名称的菜单，如果不存在该名称的菜单，则返回null
	 */	
	findMenuItemByName : function(menuName) {
		for (var i = 0; i < this.__menuItems.length; i ++) {
			if (this.__menuItems[i].name == menuName) {
				return this.__menuItems[i];
			}
		}
		return null;
	}
});
