/**
 * 在组件内进行文本搜索的助手类
 * @author doubeye
 * @version 1.0.0
 * config
 *  component : {com.doubeye.Component} 指定被搜索的组件
 *  selector : {jQuerySelector} 只在符合条件的selector中寻找，暂时作为解决部分组件事件失效的问题
 */
com.doubeye.ComponentSearch = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ComponentSearch',
	contentEl : null,
	selector : null,
	init : function() {
		com.doubeye.ComponentSearch.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ComponentSearch.superclass.render.call(this);
		this.__renderElements();
		return this;
	},
	__renderElements : function() {
		var thiz = this;
		this.contentEl = jQuery('<input>', {
			type : 'text'
		}).appendTo(this.rootComponent);
		jQuery('<input>', {
			type : 'button',
			value : '查找'
		}).appendTo(this.rootComponent).click(function() {
			thiz.find();
		});
		jQuery('<input>', {
			type : 'button',
			value : '取消高亮'
		}).appendTo(this.rootComponent).click(function() {
			thiz.clearSelection();
		});
	},
	find : function() {
		var thiz = this;
		if (!this.component) {
			return;
		}
		this.clearSelection();
		//先清空一下上次高亮显示的内容；
		var searchText = this.contentEl.val();
		//获取你输入的关键字；
		var regExp = new RegExp(searchText, 'g');
		//创建正则表达式，g表示全局的，如果不用g，则查找到第一个就不会继续向下查找了；
		if (this.selector) {
			this.component.rootComponent.find(this.selector).each(function()//遍历文章；
			{
				var html = $(this).html();
				var newHtml = html.replace(regExp, '<span class="' + (thiz.themePrefix + 'searchHighlight') + '">' + searchText + '</span>');
				//将找到的关键字替换，加上highlight属性；
				$(this).html(newHtml);
				//更新文章；
			});	
		} else {
			this.component.rootComponent.children().each(function()//遍历文章；
			{
				var html = $(this).html();
				var newHtml = html.replace(regExp, '<span class="' + (thiz.themePrefix + 'searchHighlight') + '">' + searchText + '</span>');
				//将找到的关键字替换，加上highlight属性；
				$(this).html(newHtml);
				//更新文章；
			});	
		}
		
	},
	clearSelection : function() {
		var thiz = this;
		if (this.selector) {
			this.component.rootComponent.find(this.selector).each(function()//遍历
			{
				$(this).find('.' + (thiz.themePrefix + 'searchHighlight')).each(function()//找到所有highlight属性的元素；
				{
					$(this).replaceWith($(this).html());
					//将他们的属性去掉；
				});
			});
		} else {
			this.component.rootComponent.children().each(function()//遍历
			{
				$(this).find('.' + (thiz.themePrefix + 'searchHighlight')).each(function()//找到所有highlight属性的元素；
				{
					$(this).replaceWith($(this).html());
					//将他们的属性去掉；
				});
			});
		}
		
	}
});