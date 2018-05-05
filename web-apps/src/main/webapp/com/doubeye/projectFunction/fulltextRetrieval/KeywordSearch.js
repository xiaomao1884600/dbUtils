/**
 * 全文检索关键词查询页 
 * config
 *  resultComponent {com.doubeye.SearchResult} 结果展示组件
 *  keyword {String} 要搜索的关键字
 */
com.doubeye.KeywordSearch = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.KeywordSearch',
	inputEl : null,
	resultEl : null,
	init : function() {
		com.doubeye.KeywordSearch.superclass.init.call(this);
		return this;		
	},
	render : function() {
		com.doubeye.KeywordSearch.superclass.render.call(this);
		var thiz = this;
		this.inputEl = jQuery('<input>').appendTo(this.rootComponent).val(this.keyword);
		this.button = new com.doubeye.Button({
			parent : this.rootComponent,
			text : '搜索',
			action : function() {
				thiz.doSearch();
			}
		}).init().render();
		return this;
	},
	doSearch : function() {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/FulltextRetrieval?action=search',
			params : {
				keyword : this.inputEl.val()
			},
			processResult : function(data){
				if (!thiz.resultComponent) {
					thiz.rootComponent.hide();
					new com.doubeye.SearchResult({
						parent : thiz.parent,
						keyword : thiz.inputEl.val(),
						result : data
					}).init().render();
					thiz.rootComponent.remove();
				} else {
					thiz.resultComponent.clearResult();
					thiz.resultComponent.showResult(data);
				}
			}
		}).sendRequest();
	}
});
