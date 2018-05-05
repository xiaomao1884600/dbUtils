/**
 * 文本形式的分页器
 * @author doubeye
 * @version 1.0.0
 * config
 * pageDecorator {com.doubeye.dTablePageDecorator} 引用
 * needLastPage {boolean} 是否需要末页，默认为fasle，因为大部分查询不去检查记录数 
 */
com.doubeye.dTablePagingTextRender = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTablePagingTextRender',
	pageDecorator : null,
	needLastPage : false,
	firstPageEl : null,
	previousPageEl : null,
	currentPageEl : null,
	jumpPageEl : null,
	nextPageEl : null,
	lastPageEl : null,
	recordPerPageEl : null,
	init : function() {
		com.doubeye.dTablePagingTextRender.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.dTablePagingTextRender.superclass.render.call(this);
		this.renderFirstPage();
		this.renderPreviousPage();
		this.renderCurrentPage();
		this.renderJumpPage();
		this.renderNextPage();
		if (this.needLastPage){
			this.renderLastPage();
		}
		this.renderRecordPerPage();
		return this;
	},
	renderFirstPage : function() {
		var thiz = this;
		this.firstPageEl = jQuery('<a>', {
			html : '首页',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);'
		}).appendTo(this.rootComponent).click(function(){
			thiz.pageDecorator.getPage(1);
		});
	},
	renderPreviousPage : function() {
		var thiz = this;
		this.previousPageEl = jQuery('<a>', {
			html : '上一页',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);'
		}).appendTo(this.rootComponent).click(function() {
			thiz.pageDecorator.getPreviousPage();
		});
	},
	renderCurrentPage : function(){
		var thiz = this;
		jQuery('<label>', {
			text : '当前页：',
			'class' : this.classThemePrefix + 'button'
		}).appendTo(this.rootComponent);
		this.currentPageEl = jQuery('<input>', {
			type : 'text',
			'class' : this.classThemePrefix + 'numberInput',
			value : this.pageDecorator.currentPage
		}).appendTo(this.rootComponent).keydown(function(event) {
			return (event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105) || event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39;
		});
	},
	setCurrentPage : function(page){
		this.currentPageEl.val(page);
		if (page == 1) {
			this.firstPageEl.hide();
			this.previousPageEl.hide();	
		} else {
			this.firstPageEl.show();
			this.previousPageEl.show();
		}
		if (this.needLastPage) {
			if (this.pageDecorator.maxPage && this.page == this.pageDecorator.maxPage) {
				this.lastPageEl.hide();
				this.nextPageEl.hide();	
			} else {
				this.lastPageEl.show();
				this.nextPageEl.show();
			}
		}
		return this;
	},
	renderJumpPage : function(){
		var thiz = this;
		this.jumpPageEl = jQuery('<a>', {
			html : '转到',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);',
			value : this.pageDecorator.currentPage
		}).appendTo(this.rootComponent).click(function(){
			thiz.pageDecorator.getPage(parseInt(thiz.currentPageEl.val()), 10);
		});
	},
	renderNextPage : function(){
		var thiz = this;
		this.nextPageEl = jQuery('<a>', {
			html : '下一页',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);'
		}).appendTo(this.rootComponent).click(function(){
			thiz.pageDecorator.getNextPage();
		});		
	},
	renderLastPage : function(){
		var thiz = this;
		this.lastPageEl = jQuery('<a>', {
			html : '末页',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);'
		}).appendTo(this.rootComponent).click(function(){
			thiz.pageDecorator.getLastPage();
		});
	},
	renderRecordPerPage : function(){
		var thiz = this;
		jQuery('<label>', {
			text : '[ 每页',
			'class' : this.classThemePrefix + 'button'
		}).appendTo(this.rootComponent);
		this.recordPerPageEl = jQuery('<input>', {
			type : 'text',
			'class' : this.classThemePrefix + 'numberInput',
			value : this.pageDecorator.recordPerPage
		}).appendTo(this.rootComponent).keydown(function(event) {
			return (event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105) || event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39;
		});
		jQuery('<label>', {
			text : '条记录 ]',
			'class' : this.classThemePrefix + 'button'
		}).appendTo(this.rootComponent);
		jQuery('<a>', {
			html : '确定',
			'class' : this.classThemePrefix + 'button',
			href : 'javascript:void(0);'
		}).appendTo(this.rootComponent).click(function(){
			var oldMaxPage = thiz.pageDecorator.maxPage;
			var oldRecordPerPage = thiz.pageDecorator.getRecordPerPage();
			thiz.pageDecorator.setRecordPerPage(parseInt(thiz.recordPerPageEl.val()), 10);			
			if (oldMaxPage && oldMaxPage > 0) {
				var estimatedRowCount = oldRecordPerPage * oldMaxPage;
				var newRecordPerPage = thiz.pageDecorator.getRecordPerPage();
				thiz.pageDecorator.maxPage = Math.ceil(estimatedRowCount * 1.0 / newRecordPerPage);
			}
		});
	},
	setRecordPerPage : function(recordPerPage) {
		this.recordPerPageEl.val(recordPerPage);
	}
});
