/**
 * 程序主界面
 * @version 1.0.0
 * @import [Extjs, jQuery, jQueryUI], "doubeye/component/component.js" 
 * @css ../css/dropSite.css
 * 
 * 
 * 构造函数
 * menuWidth {number} 当指定为整数时表示为像素，小数时表示百分比，如果不指定此值，则默认为150px
 * topRenderClass {String} 指定主界面顶部绘制类，如不指定此属性，则不绘制顶部
 * topRegionCollapsible {boolean} 指定主界面顶部是否可以折叠，默认为不可以
 * bottomRenderClass {String} 指定主界面底部绘制类，如不指定此属性，则不绘制底部
 * bottomRegionCollapsible {boolean} 指定主界面低部是否可以折叠，默认为不可以
 * menuRenderClass {String} 指定主界面菜单绘制类
 * menuRegionCollapsible {boolean} 指定主界面菜单是否可以折叠，默认为不可以
 */
com.doubeye.MainFrame = com.doubeye.Ext.extend(com.doubeye.Component, {
	/**
	 * 当没有操作时的锁定计时器
	 */
	lockTimer : null,
	/**
	 * 顶部区域
	 */
	topRegion : null,
	init : function(){
		com.doubeye.MainFrame.superclass.init.call(this);	
		this.classThemePrefix = this.themePrefix + 'MainFrame-';
		this.topRenderClass = this.config.topRenderClass;
	},
	render : function(){
		var thiz = this;
		this.rootComponent = new Ext.Viewport({
			renderTo : this.parentId,
			layout : 'border',
			items : [{
				region : 'north',
				xtype : 'panel',
				id : this.rootId + 'north',
				height : 0,
				visible : false,
				collapseMode:'mini',
				collapsible : this.config.topRegionCollapsible
			},{
				xtype : 'panel',
				id : this.rootId + 'menu',
				region : 'west',
				width : 150,
				collapsible : this.config.menuRegionCollapsible
			}, {
				region : 'center',
				autoScroll : true,
				html : '<div id="' + this.rootId + 'container' + '" />' 
			}]			
		});
		this.topRegion = Ext.getCmp(this.rootId + 'north');
		if (this.topRenderClass) {
			this.topRegion.setHeight(64);
			this.topRegion.setVisible(true);
			this.rootComponent.doLayout();
			var cmp = new com.doubeye.Test_Dummy({
				parent : thiz.topRegion
			});
			cmp.init();
			cmp.render();
		}
		/**
		 * 加入锁定支持
		 */
		if (com.doubeye.GlobalParameters.noMoveLockTime) {
			this.lockTimer = new com.doubeye.LockUI({
				hiddingEl : this.rootComponent
			});
			this.lockTimer.setTimer();
			var thiz = this;
			jQuery(document).mousemove(function(){
				thiz.lockTimer.noMoveLasting = 0;
			});
			jQuery(document).keypress(function(){
				thiz.lockTimer.noMoveLasting = 0;
			});
		}
	}
});