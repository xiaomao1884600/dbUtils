/**
 *  
 * jQuery Table对象的分页修饰器
 * 注意:由于考虑到浏览器效率问题，不再支持浏览器本地分页，近期也没有打算支持本地分页的计划
 * 		因此要使用分页必须由服务器进行支持
 * @author doubeye
 * @version 1.0.0
 * config
 * table {com.doubeye.dTable} dTable 对象
 * currentPage {int} 默认显示的页数
 * recordPerPage {int} 每页的记录数，默认为10
 * position {'head'|'foot'|'both'}，分页器的位置，head代表在表格上方，foot代表在表格的下方，both表示上方下发各放一个，默认为head
 * pagingRenderClassName : {com.doubeye.Component} 用来绘制分页使用的按钮的类，默认为com.doubeye.dTablePagingTextRender，如果想编写自己的render，可以参考com.doubeye.dTablePagingTextRender
 */
com.doubeye.dTablePageDecorator = com.doubeye.Ext.extend(com.doubeye.dTable, {
	className : 'com.doubeye.dTablePageDecorator',
	currentPage : 1,
	recordPerPage : 10,
	/**
	 * 由于一个dTable对象可能有两个分页器，所以需要保存他们的引用用来维持两个对象的状态 
	 */
	renders : null,
	position : 'head',
	pagingRenderClassName : 'com.doubeye.dTablePagingTextRender',
	maxPage : null,
	init : function(){
		this.renders = [];
		this.table.init();		
		return this;
	},
	render : function(){
		if (this.config.currentPage || this.config.recordPerPage) {
			this.__setAdditionParams();
		}
		this.table.render();
		this.__renderPagingComponent();
		return this;
	},
	__renderPagingComponent : function() {
		if (this.position == 'head' || this.position == 'both') {
			var trEl = jQuery('<tr>').prependTo(this.getTable().theadEl);
			var td = jQuery('<td>', {
				colSpan : this.getTable().columnDefinesObj.getDataColumnDefines().length
			}).appendTo(trEl);
			var config = {
				parent : td,
				needLastPage : this.maxPage,
				pageDecorator : this
			};
			var page = com.doubeye.Utils.getClassInstance(this.pagingRenderClassName, config).init().render().setCurrentPage(this.currentPage);
			this.renders.push(page);
		}
		if (this.position == 'foot' || this.position == 'both') {
			var trEl = jQuery('<tr>').prependTo(this.getTable().tfootEl);
			var td = jQuery('<td>', {
				colSpan : this.getTable().columnDefinesObj.getDataColumnDefines().length
			}).appendTo(trEl);
			var config = {
				parent : td,
				needLastPage : this.maxPage,
				pageDecorator : this
			};
			var page = com.doubeye.Utils.getClassInstance(this.pagingRenderClassName, config).init().render().setCurrentPage(this.currentPage);
			this.renders.push(page);
		}
	},
	__setAdditionParams : function(){
		var minRowNumber = (this.currentPage - 1) * this.recordPerPage + 1;
		var maxRowNumber = this.currentPage * this.recordPerPage;
		this.getTable().__additionParams.page = com.doubeye.Ext.encode({
			minRowNumber : minRowNumber,
			maxRowNumber : maxRowNumber
		});
	},
	setRecordPerPage : function(recordPerPage) {
		if (isNaN(recordPerPage) || recordPerPage <= 0) {
			new com.doubeye.BubbleDialog({message : '输入的记录数不正确'}).init().render();
			return;
		}
		this.recordPerPage = recordPerPage;
		this.getPage(this.currentPage);
		for (var i = 0; i < this.renders.length; i ++) {
			this.renders[i].setRecordPerPage(this.recordPerPage);
		}
	},
	getRecordPerPage : function() {
		return this.recordPerPage;
	},
	getPage : function(page) {
		if (isNaN(page) || page <= 0) {
			new com.doubeye.BubbleDialog({message : '输入的页数不正确'}).init().render();
			return;
		}
		if ( this.maxPage && (page > this.maxPage)) {
			new com.doubeye.BubbleDialog({message : '输入的页数超过最大页'}).init().render();
			return;
		}
		this.currentPage = page;
		for (var i = 0; i < this.renders.length; i ++) {
			this.renders[i].setCurrentPage(this.currentPage);
		}
		this.__setAdditionParams();
		this.getTable().clear().getData();
		if (this.getTable().getRecordCount() < parseInt(this.recordPerPage, 10)) {
			if (this.getTable().getRecordCount() == 0) {
				this.maxPage = this.currentPage - 1;
			} else {
				this.maxPage = this.currentPage;
			}
		} 
	},
	getPreviousPage : function() {
		if (this.currentPage == 1) {
			new com.doubeye.BubbleDialog({message : '已经到达首页'}).init().render();
			return;
		}
		this.getPage(this.currentPage - 1);
	},
	getNextPage : function(){
		if (this.maxPage && this.currentPage >= this.maxPage) {
			new com.doubeye.BubbleDialog({message : '已经到达尾页'}).init().render();
			return;
		}
		this.getPage(this.currentPage + 1);
	},
	getLastPage : function() {
		if (this.maxPage) {
			this.getPage(this.maxPage);
		} else {
			new com.doubeye.BubbleDialog({message : '无尾页信息'}).init().render();
		}
	},
	clearRowSelection : function() {
		this.table.clearRowSelection();
	}
/*	
	/**
	 * 获得标记为删除的列
	 * @return {Array<com.doubeye.Record>} 所有标记为删除的列
	 /
	getRowsMarkDeleted : function() {
		return this.table.getRowsMarkDeleted();
	},
	/**
	 * 获得新增的列
	 * @return {Array<com.doubeye.Record>} 所有新增的列
	/
	getRowsAdded : function() {
		return this.table.getRowsAdded();
	},
	/**
	 * 获得新增后被标记为删除的行 
	 * @return {Array<com.doubeye.Record>} 所有新增后背被删除的行
	/
	getRowsAddedAndDeleted : function() {
		return this.table.getRowsAddedAndDeleted();
	},	
	/**
	 * 获得修改过的列
	 * @return {Array<com.doubeye.Record>} 所有修改过的列
	/
	getRowsModified : function() {
		return this.table.getRowsModified();
	},
	/**
	* 获得指定类型的列
	* @param {Enumeration<com.doubeye.constant.DATA.RECORDSTATUS>} status 数据的状态
	* @return {Array<com.doubeye.Record>} 所有指定的列
	/
	__getSpecifiedStatusRows : function(status) {
		return this.table.__getSpecifiedStatusRows(status);
	}
*/
});
